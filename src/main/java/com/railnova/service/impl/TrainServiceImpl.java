package com.railnova.service.impl;

import com.railnova.dto.*;
import com.railnova.entity.*;
import com.railnova.exception.BadRequestException;
import com.railnova.exception.ResourceNotFoundException;
import com.railnova.repository.*;
import com.railnova.service.TrainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final FareRepository fareRepository;
    private final BookedSeatRepository bookedSeatRepository;

    public TrainServiceImpl(TrainRepository trainRepository,
                            StationRepository stationRepository,
                            TrainRouteRepository trainRouteRepository,
                            CoachRepository coachRepository,
                            SeatRepository seatRepository,
                            FareRepository fareRepository,
                            BookedSeatRepository bookedSeatRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.trainRouteRepository = trainRouteRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.fareRepository = fareRepository;
        this.bookedSeatRepository = bookedSeatRepository;
    }

    @Override
    public List<TrainSearchResultDto> searchTrains(String fromStation, String toStation, LocalDate journeyDate,
                                                   String classType, String trainType, Double maxPrice,
                                                   String timeSlot, String sortBy) {
        if (journeyDate == null) {
            journeyDate = LocalDate.now().plusDays(1);
        }

        List<Train> matchingTrains;
        if (fromStation != null && !fromStation.trim().isEmpty() && toStation != null && !toStation.trim().isEmpty()) {
            Station source = findStationOrThrow(fromStation);
            Station destination = findStationOrThrow(toStation);
            matchingTrains = trainRepository.findTrainsBetweenStations(source, destination);
        } else {
            matchingTrains = trainRepository.findByActiveTrue();
        }

        // Filter by day of week
        DayOfWeek dayOfWeek = journeyDate.getDayOfWeek();
        String dayShort = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // e.g. "Mon"

        final LocalDate finalJourneyDate = journeyDate;
        List<TrainSearchResultDto> results = new ArrayList<>();

        for (Train train : matchingTrains) {
            if (!train.isActive()) continue;
            if (train.getRunsOnDays() != null && !train.getRunsOnDays().contains(dayShort)) {
                // If specific running days are configured and this day isn't listed, continue
                // Unless "Daily" or all days
                if (!train.getRunsOnDays().equalsIgnoreCase("Daily") && !train.getRunsOnDays().equalsIgnoreCase("All")) {
                    continue;
                }
            }

            TrainSearchResultDto dto = new TrainSearchResultDto();
            dto.setId(train.getId());
            dto.setTrainNumber(train.getTrainNumber());
            dto.setTrainName(train.getTrainName());
            dto.setTrainType(train.getTrainType());
            dto.setSourceStationCode(train.getSourceStation().getCode());
            dto.setSourceStationName(train.getSourceStation().getName());
            dto.setDestinationStationCode(train.getDestinationStation().getCode());
            dto.setDestinationStationName(train.getDestinationStation().getName());
            dto.setDepartureTime(train.getDepartureTime());
            dto.setArrivalTime(train.getArrivalTime());
            dto.setDurationMinutes(train.getDurationMinutes());
            dto.setRunsOnDays(train.getRunsOnDays());
            dto.setDescription(train.getDescription());

            // Build class availabilities
            List<Fare> fares = fareRepository.findByTrain(train);
            List<ClassAvailabilityDto> classDtos = new ArrayList<>();
            Double minFare = Double.MAX_VALUE;

            for (Fare fare : fares) {
                CoachType ct = fare.getCoachType();
                List<Coach> coaches = coachRepository.findByTrainAndCoachType(train, ct);
                int totalSeatsInClass = coaches.stream().mapToInt(Coach::getTotalSeats).sum();

                // Count booked seats on this date
                int bookedSeatsCount = 0;
                for (Coach c : coaches) {
                    List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByCoachAndDate(c, finalJourneyDate);
                    bookedSeatsCount += bookedSeatIds.size();
                }

                int availableSeats = Math.max(0, totalSeatsInClass - bookedSeatsCount);
                ClassAvailabilityDto cad = new ClassAvailabilityDto(ct, fare.getBaseFare(), availableSeats);
                classDtos.add(cad);

                if (fare.getBaseFare() < minFare) {
                    minFare = fare.getBaseFare();
                }
            }

            dto.setClassAvailabilities(classDtos);
            dto.setStartingFare(minFare < Double.MAX_VALUE ? minFare : 0.0);

            // Filter by classType if specified
            if (classType != null && !classType.trim().isEmpty() && !classType.equalsIgnoreCase("ALL")) {
                boolean hasClass = classDtos.stream().anyMatch(c ->
                        c.getCoachTypeCode().equalsIgnoreCase(classType) ||
                        c.getCoachType().name().equalsIgnoreCase(classType));
                if (!hasClass) {
                    continue;
                }
            }

            // Filter by trainType if specified
            if (trainType != null && !trainType.trim().isEmpty() && !trainType.equalsIgnoreCase("ALL")) {
                if (!train.getTrainType().name().equalsIgnoreCase(trainType)) {
                    continue;
                }
            }

            // Filter by maxPrice if specified
            if (maxPrice != null && maxPrice > 0) {
                if (dto.getStartingFare() > maxPrice) {
                    continue;
                }
            }

            // Filter by timeSlot
            if (timeSlot != null && !timeSlot.trim().isEmpty() && !timeSlot.equalsIgnoreCase("ALL")) {
                int depHour = parseHour(train.getDepartureTime());
                if ("morning".equalsIgnoreCase(timeSlot) && (depHour < 6 || depHour >= 12)) continue;
                if ("afternoon".equalsIgnoreCase(timeSlot) && (depHour < 12 || depHour >= 18)) continue;
                if ("evening".equalsIgnoreCase(timeSlot) && (depHour < 18 || depHour >= 24)) continue;
                if ("early".equalsIgnoreCase(timeSlot) && depHour >= 6) continue;
            }

            results.add(dto);
        }

        // Apply sorting
        if ("fare".equalsIgnoreCase(sortBy) || "lowest_fare".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparing(TrainSearchResultDto::getStartingFare));
        } else if ("departure".equalsIgnoreCase(sortBy) || "earliest_departure".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparing(TrainSearchResultDto::getDepartureTime));
        } else if ("duration".equalsIgnoreCase(sortBy) || "shortest_duration".equalsIgnoreCase(sortBy)) {
            results.sort(Comparator.comparing(TrainSearchResultDto::getDurationMinutes));
        }

        return results;
    }

    private int parseHour(String timeStr) {
        try {
            if (timeStr != null && timeStr.contains(":")) {
                return Integer.parseInt(timeStr.split(":")[0].trim());
            }
        } catch (Exception ignored) {
        }
        return 12;
    }

    private Station findStationOrThrow(String identifier) {
        try {
            Long id = Long.parseLong(identifier.trim());
            return stationRepository.findById(id)
                    .orElseGet(() -> stationRepository.findByCodeIgnoreCase(identifier.trim())
                            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + identifier)));
        } catch (NumberFormatException e) {
            return stationRepository.findByCodeIgnoreCase(identifier.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + identifier));
        }
    }

    @Override
    public TrainDetailDto getTrainDetails(Long trainId, LocalDate journeyDate) {
        if (journeyDate == null) {
            journeyDate = LocalDate.now().plusDays(1);
        }

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + trainId));

        TrainDetailDto dto = new TrainDetailDto();
        dto.setId(train.getId());
        dto.setTrainNumber(train.getTrainNumber());
        dto.setTrainName(train.getTrainName());
        dto.setTrainType(train.getTrainType());
        dto.setSourceStationCode(train.getSourceStation().getCode());
        dto.setSourceStationName(train.getSourceStation().getName());
        dto.setDestinationStationCode(train.getDestinationStation().getCode());
        dto.setDestinationStationName(train.getDestinationStation().getName());
        dto.setDepartureTime(train.getDepartureTime());
        dto.setArrivalTime(train.getArrivalTime());
        dto.setDurationMinutes(train.getDurationMinutes());
        dto.setRunsOnDays(train.getRunsOnDays());
        dto.setDescription(train.getDescription());
        dto.setActive(train.isActive());

        // Routes
        List<TrainRoute> routes = trainRouteRepository.findByTrainOrderByStopNumberAsc(train);
        dto.setRouteStops(routes.stream().map(RouteStopDto::new).collect(Collectors.toList()));

        // Classes & Availability
        List<Fare> fares = fareRepository.findByTrain(train);
        List<ClassAvailabilityDto> classDtos = new ArrayList<>();
        final LocalDate finalDate = journeyDate;

        for (Fare fare : fares) {
            CoachType ct = fare.getCoachType();
            List<Coach> coaches = coachRepository.findByTrainAndCoachType(train, ct);
            int totalSeats = coaches.stream().mapToInt(Coach::getTotalSeats).sum();
            int bookedSeats = 0;
            for (Coach c : coaches) {
                bookedSeats += bookedSeatRepository.findBookedSeatIdsByCoachAndDate(c, finalDate).size();
            }
            int availableSeats = Math.max(0, totalSeats - bookedSeats);
            classDtos.add(new ClassAvailabilityDto(ct, fare.getBaseFare(), availableSeats));
        }
        dto.setClasses(classDtos);

        // Coaches
        List<Coach> coaches = coachRepository.findByTrain(train);
        dto.setCoaches(coaches.stream().map(CoachSummaryDto::new).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public CoachSeatMapDto getCoachSeats(Long coachId, LocalDate journeyDate) {
        if (journeyDate == null) {
            journeyDate = LocalDate.now().plusDays(1);
        }

        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with id: " + coachId));

        List<Seat> seats = seatRepository.findByCoachOrderBySeatNumberAsc(coach);
        List<Long> bookedSeatIds = bookedSeatRepository.findBookedSeatIdsByCoachAndDate(coach, journeyDate);

        CoachSeatMapDto dto = new CoachSeatMapDto();
        dto.setCoachId(coach.getId());
        dto.setCoachCode(coach.getCoachCode());
        dto.setCoachType(coach.getCoachType());
        dto.setTotalSeats(coach.getTotalSeats());

        List<SeatDto> seatDtos = new ArrayList<>();
        int bookedCount = 0;

        for (Seat seat : seats) {
            String status = bookedSeatIds.contains(seat.getId()) ? "BOOKED" : "AVAILABLE";
            if ("BOOKED".equals(status)) {
                bookedCount++;
            }
            seatDtos.add(new SeatDto(seat, status));
        }

        dto.setSeats(seatDtos);
        dto.setBookedCount(bookedCount);
        dto.setAvailableCount(seats.size() - bookedCount);

        return dto;
    }

    @Override
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    @Override
    public Train getTrainById(Long id) {
        return trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));
    }

    @Override
    @Transactional
    public Train createTrain(TrainCreateUpdateDto dto) {
        if (trainRepository.findByTrainNumber(dto.getTrainNumber().trim()).isPresent()) {
            throw new BadRequestException("Train with number " + dto.getTrainNumber() + " already exists");
        }

        Station source = stationRepository.findById(dto.getSourceStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found"));
        Station dest = stationRepository.findById(dto.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));

        Train train = new Train();
        train.setTrainNumber(dto.getTrainNumber().trim());
        train.setTrainName(dto.getTrainName().trim());
        train.setTrainType(dto.getTrainType());
        train.setSourceStation(source);
        train.setDestinationStation(dest);
        train.setDepartureTime(dto.getDepartureTime().trim());
        train.setArrivalTime(dto.getArrivalTime().trim());
        train.setDurationMinutes(dto.getDurationMinutes());
        train.setRunsOnDays(dto.getRunsOnDays() != null ? dto.getRunsOnDays() : "Mon,Tue,Wed,Thu,Fri,Sat,Sun");
        train.setActive(dto.isActive());
        train.setDescription(dto.getDescription());

        Train savedTrain = trainRepository.save(train);

        // Provision default coaches and seats
        provisionDefaultCoachesAndSeats(savedTrain);

        return savedTrain;
    }

    @Override
    @Transactional
    public Train updateTrain(Long id, TrainCreateUpdateDto dto) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));

        Station source = stationRepository.findById(dto.getSourceStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Source station not found"));
        Station dest = stationRepository.findById(dto.getDestinationStationId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination station not found"));

        train.setTrainNumber(dto.getTrainNumber().trim());
        train.setTrainName(dto.getTrainName().trim());
        train.setTrainType(dto.getTrainType());
        train.setSourceStation(source);
        train.setDestinationStation(dest);
        train.setDepartureTime(dto.getDepartureTime().trim());
        train.setArrivalTime(dto.getArrivalTime().trim());
        train.setDurationMinutes(dto.getDurationMinutes());
        train.setRunsOnDays(dto.getRunsOnDays());
        train.setActive(dto.isActive());
        train.setDescription(dto.getDescription());

        return trainRepository.save(train);
    }

    @Override
    @Transactional
    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new ResourceNotFoundException("Train not found with id: " + id);
        }
        trainRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Train toggleTrainStatus(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Train not found with id: " + id));
        train.setActive(!train.isActive());
        return trainRepository.save(train);
    }

    private void provisionDefaultCoachesAndSeats(Train train) {
        // Sleeper S1, S2
        Coach s1 = createCoachWithSeats(train, "S1", CoachType.SLEEPER, 72);
        Coach s2 = createCoachWithSeats(train, "S2", CoachType.SLEEPER, 72);
        // 3AC B1
        Coach b1 = createCoachWithSeats(train, "B1", CoachType.THIRD_AC, 64);
        // 2AC A1
        Coach a1 = createCoachWithSeats(train, "A1", CoachType.SECOND_AC, 48);

        // Fares
        fareRepository.save(new Fare(train, CoachType.SLEEPER, 480.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(train, CoachType.THIRD_AC, 1250.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(train, CoachType.SECOND_AC, 1850.0, 40.0, 45.0, 5.0));
    }

    private Coach createCoachWithSeats(Train train, String code, CoachType type, int totalSeats) {
        Coach coach = new Coach(train, code, type, totalSeats);
        coach = coachRepository.save(coach);

        List<Seat> seats = new ArrayList<>();
        BerthType[] berthOrder = {
                BerthType.LOWER, BerthType.MIDDLE, BerthType.UPPER,
                BerthType.LOWER, BerthType.MIDDLE, BerthType.UPPER,
                BerthType.SIDE_LOWER, BerthType.SIDE_UPPER
        };

        for (int i = 1; i <= totalSeats; i++) {
            BerthType bt = berthOrder[(i - 1) % berthOrder.length];
            int row = (i - 1) / 8 + 1;
            seats.add(new Seat(coach, i, bt, row));
        }
        seatRepository.saveAll(seats);
        return coach;
    }
}
