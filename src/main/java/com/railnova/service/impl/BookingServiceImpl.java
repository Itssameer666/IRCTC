package com.railnova.service.impl;

import com.railnova.dto.BookingRequestDto;
import com.railnova.dto.BookingResponseDto;
import com.railnova.dto.CancelBookingRequestDto;
import com.railnova.dto.PassengerRequestDto;
import com.railnova.entity.*;
import com.railnova.exception.BadRequestException;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.exception.SeatUnavailableException;
import com.railnova.exception.UnauthorizedException;
import com.railnova.repository.*;
import com.railnova.service.BookingService;
import com.railnova.service.NotificationService;
import com.railnova.util.PnrGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final FareRepository fareRepository;
    private final UserRepository userRepository;
    private final RefundRepository refundRepository;
    private final NotificationService notificationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingPassengerRepository bookingPassengerRepository,
                              BookedSeatRepository bookedSeatRepository,
                              TrainRepository trainRepository,
                              StationRepository stationRepository,
                              CoachRepository coachRepository,
                              SeatRepository seatRepository,
                              FareRepository fareRepository,
                              UserRepository userRepository,
                              RefundRepository refundRepository,
                              NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.bookedSeatRepository = bookedSeatRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.fareRepository = fareRepository;
        this.userRepository = userRepository;
        this.refundRepository = refundRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto createBooking(BookingRequestDto request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Train train = trainRepository.findById(request.getTrainId())
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + request.getTrainId()));

        if (!train.isActive()) {
            throw new BadRequestException("Train " + train.getTrainNumber() + " is currently inactive.");
        }

        LocalDate journeyDate = request.getJourneyDate();
        if (journeyDate.isBefore(LocalDate.now())) {
            throw new BadRequestException("Journey date cannot be in the past.");
        }

        Station sourceStation = request.getSourceStationId() != null
                ? stationRepository.findById(request.getSourceStationId()).orElse(train.getSourceStation())
                : train.getSourceStation();

        Station destinationStation = request.getDestinationStationId() != null
                ? stationRepository.findById(request.getDestinationStationId()).orElse(train.getDestinationStation())
                : train.getDestinationStation();

        List<Coach> coaches = coachRepository.findByTrainAndCoachType(train, request.getCoachType());
        if (coaches.isEmpty()) {
            throw new BadRequestException("No coaches of type " + request.getCoachType() + " available for this train.");
        }

        Fare fare = fareRepository.findByTrainAndCoachType(train, request.getCoachType())
                .orElseThrow(() -> new BadRequestException("Fare not configured for class " + request.getCoachType()));

        int totalPassengers = request.getPassengers().size();
        if (totalPassengers <= 0) {
            throw new BadRequestException("At least one passenger must be specified.");
        }

        // Calculate Fares
        double baseAmount = fare.getBaseFare() * totalPassengers;
        double serviceCharge = (fare.getReservationCharge() + fare.getSuperfastCharge()) * totalPassengers;
        double taxAmount = Math.round(((baseAmount + serviceCharge) * fare.getGstPercentage() / 100.0) * 100.0) / 100.0;
        double totalAmount = Math.round((baseAmount + serviceCharge + taxAmount) * 100.0) / 100.0;

        // Generate Unique PNR and Booking Ref
        String pnr = generateUniquePNR();
        String bookingRef = PnrGenerator.generateBookingReference();

        Booking booking = new Booking();
        booking.setBookingReference(bookingRef);
        booking.setPNRNumber(pnr);
        booking.setUser(user);
        booking.setTrain(train);
        booking.setSourceStation(sourceStation);
        booking.setDestinationStation(destinationStation);
        booking.setJourneyDate(journeyDate);
        booking.setCoachType(request.getCoachType());
        booking.setTotalPassengers(totalPassengers);
        booking.setBaseAmount(baseAmount);
        booking.setTaxAmount(taxAmount);
        booking.setServiceCharge(serviceCharge);
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // Seat Allocation & Locking
        List<BookingPassenger> passengerEntities = new ArrayList<>();
        List<BookedSeat> bookedSeatEntities = new ArrayList<>();

        // Collect all already booked seat IDs on this date across relevant coaches
        Set<Long> occupiedSeatIds = new HashSet<>();
        for (Coach c : coaches) {
            occupiedSeatIds.addAll(bookedSeatRepository.findBookedSeatIdsByCoachAndDate(c, journeyDate));
        }

        for (PassengerRequestDto pDto : request.getPassengers()) {
            BookingPassenger bp = new BookingPassenger();
            bp.setBooking(savedBooking);
            bp.setFullName(pDto.getFullName().trim());
            bp.setAge(pDto.getAge());
            bp.setGender(pDto.getGender());
            bp.setBerthPreference(pDto.getBerthPreference());
            bp.setIdType(pDto.getIdType() != null ? pDto.getIdType() : "Aadhaar");
            bp.setIdNumber(pDto.getIdNumber() != null ? pDto.getIdNumber() : "XXXX-XXXX");

            Seat allocatedSeat = null;

            // Check if passenger explicitly selected a seat
            if (pDto.getSelectedSeatId() != null) {
                Long seatId = pDto.getSelectedSeatId();
                if (occupiedSeatIds.contains(seatId)) {
                    throw new SeatUnavailableException("Seat #" + seatId + " is already booked or reserved.");
                }
                Seat seat = seatRepository.findById(seatId)
                        .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));
                allocatedSeat = seat;
                occupiedSeatIds.add(seatId);
            } else {
                // Auto-allocate first available seat in coaches
                for (Coach c : coaches) {
                    List<Seat> coachSeats = seatRepository.findByCoachOrderBySeatNumberAsc(c);
                    for (Seat s : coachSeats) {
                        if (!occupiedSeatIds.contains(s.getId())) {
                            // Check berth preference match if present
                            if (pDto.getBerthPreference() != null && !pDto.getBerthPreference().isEmpty()) {
                                if (s.getBerthType().name().equalsIgnoreCase(pDto.getBerthPreference())) {
                                    allocatedSeat = s;
                                    occupiedSeatIds.add(s.getId());
                                    break;
                                }
                            } else {
                                allocatedSeat = s;
                                occupiedSeatIds.add(s.getId());
                                break;
                            }
                        }
                    }
                    if (allocatedSeat != null) break;
                }

                // If preference didn't match, pick any available seat
                if (allocatedSeat == null) {
                    for (Coach c : coaches) {
                        List<Seat> coachSeats = seatRepository.findByCoachOrderBySeatNumberAsc(c);
                        for (Seat s : coachSeats) {
                            if (!occupiedSeatIds.contains(s.getId())) {
                                allocatedSeat = s;
                                occupiedSeatIds.add(s.getId());
                                break;
                            }
                        }
                        if (allocatedSeat != null) break;
                    }
                }
            }

            if (allocatedSeat == null) {
                throw new SeatUnavailableException("No seats available for class " + request.getCoachType().getDisplayName());
            }

            bp.setAssignedCoach(allocatedSeat.getCoach().getCoachCode());
            bp.setAssignedSeat(allocatedSeat.getSeatNumber());
            bp.setAssignedBerth(allocatedSeat.getBerthType().name());

            passengerEntities.add(bp);

            // Reserve seat
            BookedSeat bookedSeat = new BookedSeat(train, journeyDate, allocatedSeat.getCoach(), allocatedSeat, savedBooking, "RESERVED");
            bookedSeatEntities.add(bookedSeat);
        }

        bookingPassengerRepository.saveAll(passengerEntities);
        bookedSeatRepository.saveAll(bookedSeatEntities);

        savedBooking.setPassengers(passengerEntities);
        return new BookingResponseDto(savedBooking);
    }

    private String generateUniquePNR() {
        String pnr;
        do {
            pnr = PnrGenerator.generatePNR();
        } while (bookingRepository.findByPnrNumber(pnr).isPresent());
        return pnr;
    }

    @Override
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return new BookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBookingByPnr(String pnr) {
        Booking booking = bookingRepository.findByPnrNumber(pnr.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No booking found for PNR: " + pnr));
        return new BookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        return bookingRepository.findByUserOrderByBookingTimeDesc(user)
                .stream().map(BookingResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId, CancelBookingRequestDto request, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        // Check ownership or admin/staff
        if (!booking.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != Role.ROLE_ADMIN && currentUser.getRole() != Role.ROLE_STAFF) {
            throw new UnauthorizedException("You are not authorized to cancel this booking.");
        }

        if (booking.getBookingStatus() == BookingStatus.CANCELLED || booking.getBookingStatus() == BookingStatus.REFUNDED) {
            throw new BadRequestException("Booking is already cancelled.");
        }

        // Calculate Cancellation Charges & Refund
        // Railway cancellation rules: Rs. 120 or 20% whichever is higher per passenger
        double flatPerPass = 120.0 * booking.getTotalPassengers();
        double percentFee = booking.getTotalAmount() * 0.20;
        double cancellationCharge = Math.max(flatPerPass, percentFee);
        if (cancellationCharge > booking.getTotalAmount()) {
            cancellationCharge = booking.getTotalAmount();
        }
        double refundAmount = Math.max(0.0, booking.getTotalAmount() - cancellationCharge);
        refundAmount = Math.round(refundAmount * 100.0) / 100.0;

        // Release booked seats so they become immediately available for other users
        bookedSeatRepository.deleteByBookingId(booking.getId());

        // Create Refund record
        String refundId = PnrGenerator.generateRefundId();
        String reason = (request != null && request.getReason() != null && !request.getReason().trim().isEmpty())
                ? request.getReason().trim()
                : "Customer requested cancellation";

        Refund refund = new Refund(booking, refundId, booking.getTotalAmount(), cancellationCharge,
                refundAmount, RefundStatus.PROCESSED, reason);
        refundRepository.save(refund);

        booking.setBookingStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);

        // Send notification
        notificationService.sendNotification(booking.getUser(), "Ticket Cancelled",
                "Your booking for train " + booking.getTrain().getTrainName() + " (PNR: " + booking.getPNRNumber() +
                        ") has been cancelled. Refund of ₹" + refundAmount + " has been initiated.", "CANCELLATION");

        return new BookingResponseDto(updatedBooking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingTimeDesc()
                .stream().map(BookingResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Booking getBookingEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    @Override
    @Transactional
    public void confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Update reserved seats to BOOKED
        List<BookedSeat> bookedSeats = bookedSeatRepository.findByCoachIdAndJourneyDate(
                booking.getPassengers().get(0).getBooking().getId(), booking.getJourneyDate());
        for (BookedSeat bs : bookedSeats) {
            bs.setStatus("BOOKED");
        }
        bookedSeatRepository.saveAll(bookedSeats);

        // Send notification
        notificationService.sendNotification(booking.getUser(), "Booking Confirmed!",
                "Your journey on " + booking.getTrain().getTrainName() + " is confirmed. PNR: " + booking.getPNRNumber(), "BOOKING");
    }
}
