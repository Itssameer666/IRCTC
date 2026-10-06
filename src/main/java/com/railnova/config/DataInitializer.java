package com.railnova.config;

import com.railnova.entity.*;
import com.railnova.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final CoachRepository coachRepository;
    private final SeatRepository seatRepository;
    private final FareRepository fareRepository;
    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final BookedSeatRepository bookedSeatRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final NotificationRepository notificationRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StationRepository stationRepository,
                           TrainRepository trainRepository,
                           TrainRouteRepository trainRouteRepository,
                           CoachRepository coachRepository,
                           SeatRepository seatRepository,
                           FareRepository fareRepository,
                           BookingRepository bookingRepository,
                           BookingPassengerRepository bookingPassengerRepository,
                           BookedSeatRepository bookedSeatRepository,
                           PaymentRepository paymentRepository,
                           RefundRepository refundRepository,
                           NotificationRepository notificationRepository,
                           SupportTicketRepository supportTicketRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.trainRouteRepository = trainRouteRepository;
        this.coachRepository = coachRepository;
        this.seatRepository = seatRepository;
        this.fareRepository = fareRepository;
        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.bookedSeatRepository = bookedSeatRepository;
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.notificationRepository = notificationRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping initialization.");
            return;
        }

        log.info("Initializing RailNova database with seed data...");

        // 1. Seed Users
        User admin = new User("admin@railnova.com", passwordEncoder.encode("Admin@123"), "System Administrator", "+91 9876543210", Role.ROLE_ADMIN);
        User staff = new User("staff@railnova.com", passwordEncoder.encode("Staff@123"), "Senior Station Manager", "+91 9876543211", Role.ROLE_STAFF);
        User passenger1 = new User("user@railnova.com", passwordEncoder.encode("User@123"), "Rahul Sharma", "+91 9876543212", Role.ROLE_PASSENGER);
        User passenger2 = new User("sameer@railnova.com", passwordEncoder.encode("Sameer@123"), "Sameer Khan", "+91 9876543213", Role.ROLE_PASSENGER);

        userRepository.saveAll(List.of(admin, staff, passenger1, passenger2));

        // 2. Seed Stations
        Station ndls = stationRepository.save(new Station("NDLS", "New Delhi", "New Delhi", "Delhi", "NR"));
        Station bct = stationRepository.save(new Station("BCT", "Mumbai Central", "Mumbai", "Maharashtra", "WR"));
        Station csmt = stationRepository.save(new Station("CSMT", "Chhatrapati Shivaji Maharaj Terminus", "Mumbai", "Maharashtra", "CR"));
        Station hwh = stationRepository.save(new Station("HWH", "Howrah Junction", "Kolkata", "West Bengal", "ER"));
        Station sbc = stationRepository.save(new Station("SBC", "KSR Bengaluru", "Bengaluru", "Karnataka", "SWR"));
        Station mas = stationRepository.save(new Station("MAS", "Chennai Central", "Chennai", "Tamil Nadu", "SR"));
        Station pnbe = stationRepository.save(new Station("PNBE", "Patna Junction", "Patna", "Bihar", "ECR"));
        Station adi = stationRepository.save(new Station("ADI", "Ahmedabad Junction", "Ahmedabad", "Gujarat", "WR"));
        Station bsb = stationRepository.save(new Station("BSB", "Varanasi Junction", "Varanasi", "Uttar Pradesh", "NR"));
        Station cnb = stationRepository.save(new Station("CNB", "Kanpur Central", "Kanpur", "Uttar Pradesh", "NCR"));
        Station pryj = stationRepository.save(new Station("PRYJ", "Prayagraj Junction", "Prayagraj", "Uttar Pradesh", "NCR"));
        Station bpl = stationRepository.save(new Station("BPL", "Bhopal Junction", "Bhopal", "Madhya Pradesh", "WCR"));
        Station agc = stationRepository.save(new Station("AGC", "Agra Cantt", "Agra", "Uttar Pradesh", "NCR"));

        // 3. Seed Trains
        // Train 1: Vande Bharat Express NDLS -> BSB
        Train vb = new Train("22436", "Vande Bharat Express", TrainType.VANDE_BHARAT, ndls, bsb,
                "06:00", "14:00", 480, "Daily", true, "Semi-high speed premium express connecting capital to holy city");
        vb = trainRepository.save(vb);
        trainRouteRepository.save(new TrainRoute(vb, ndls, 1, "05:45", "06:00", 0, 0, "16"));
        trainRouteRepository.save(new TrainRoute(vb, cnb, 2, "10:08", "10:10", 440, 2, "1"));
        trainRouteRepository.save(new TrainRoute(vb, pryj, 3, "12:08", "12:10", 635, 2, "6"));
        trainRouteRepository.save(new TrainRoute(vb, bsb, 4, "14:00", "14:00", 755, 0, "1"));

        Coach vbC1 = createCoachWithSeats(vb, "C1", CoachType.CHAIR_CAR, 54);
        Coach vbC2 = createCoachWithSeats(vb, "C2", CoachType.CHAIR_CAR, 54);
        Coach vbE1 = createCoachWithSeats(vb, "E1", CoachType.EXECUTIVE_CHAIR_CAR, 40);
        fareRepository.save(new Fare(vb, CoachType.CHAIR_CAR, 1750.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(vb, CoachType.EXECUTIVE_CHAIR_CAR, 3300.0, 40.0, 45.0, 5.0));

        // Train 2: Mumbai Rajdhani Express NDLS -> BCT
        Train rajdhani = new Train("12952", "Mumbai Rajdhani Express", TrainType.RAJDHANI, ndls, bct,
                "16:55", "08:35", 940, "Daily", true, "King of Western Railway offering unparalleled comfort and speed");
        rajdhani = trainRepository.save(rajdhani);
        trainRouteRepository.save(new TrainRoute(rajdhani, ndls, 1, "16:40", "16:55", 0, 0, "3"));
        trainRouteRepository.save(new TrainRoute(rajdhani, agc, 2, "18:50", "18:55", 195, 5, "1"));
        trainRouteRepository.save(new TrainRoute(rajdhani, bct, 3, "08:35", "08:35", 1386, 0, "1"));

        Coach rajB1 = createCoachWithSeats(rajdhani, "B1", CoachType.THIRD_AC, 64);
        Coach rajB2 = createCoachWithSeats(rajdhani, "B2", CoachType.THIRD_AC, 64);
        Coach rajA1 = createCoachWithSeats(rajdhani, "A1", CoachType.SECOND_AC, 48);
        Coach rajH1 = createCoachWithSeats(rajdhani, "H1", CoachType.FIRST_AC, 24);
        fareRepository.save(new Fare(rajdhani, CoachType.THIRD_AC, 2080.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(rajdhani, CoachType.SECOND_AC, 2960.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(rajdhani, CoachType.FIRST_AC, 4850.0, 40.0, 45.0, 5.0));

        // Train 3: Bhopal Shatabdi Express NDLS -> BPL
        Train shatabdi = new Train("12002", "Bhopal Shatabdi Express", TrainType.SHATABDI, ndls, bpl,
                "06:00", "14:40", 520, "Daily", true, "Fastest morning express between New Delhi and Bhopal");
        shatabdi = trainRepository.save(shatabdi);
        trainRouteRepository.save(new TrainRoute(shatabdi, ndls, 1, "05:45", "06:00", 0, 0, "1"));
        trainRouteRepository.save(new TrainRoute(shatabdi, agc, 2, "07:50", "07:55", 195, 5, "1"));
        trainRouteRepository.save(new TrainRoute(shatabdi, bpl, 3, "14:40", "14:40", 707, 0, "1"));

        Coach shatC1 = createCoachWithSeats(shatabdi, "C1", CoachType.CHAIR_CAR, 54);
        Coach shatE1 = createCoachWithSeats(shatabdi, "E1", CoachType.EXECUTIVE_CHAIR_CAR, 40);
        fareRepository.save(new Fare(shatabdi, CoachType.CHAIR_CAR, 1180.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(shatabdi, CoachType.EXECUTIVE_CHAIR_CAR, 2120.0, 40.0, 45.0, 5.0));

        // Train 4: Howrah Rajdhani Express NDLS -> HWH
        Train hwhRaj = new Train("12302", "Howrah Rajdhani Express", TrainType.RAJDHANI, ndls, hwh,
                "16:50", "09:55", 1025, "Daily", true, "Premier flagship train linking New Delhi with the City of Joy");
        hwhRaj = trainRepository.save(hwhRaj);
        trainRouteRepository.save(new TrainRoute(hwhRaj, ndls, 1, "16:30", "16:50", 0, 0, "4"));
        trainRouteRepository.save(new TrainRoute(hwhRaj, cnb, 2, "21:32", "21:37", 440, 5, "4"));
        trainRouteRepository.save(new TrainRoute(hwhRaj, pryj, 3, "23:43", "23:45", 635, 2, "4"));
        trainRouteRepository.save(new TrainRoute(hwhRaj, hwh, 4, "09:55", "09:55", 1451, 0, "8"));

        Coach hwhB1 = createCoachWithSeats(hwhRaj, "B1", CoachType.THIRD_AC, 64);
        Coach hwhA1 = createCoachWithSeats(hwhRaj, "A1", CoachType.SECOND_AC, 48);
        fareRepository.save(new Fare(hwhRaj, CoachType.THIRD_AC, 2350.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(hwhRaj, CoachType.SECOND_AC, 3290.0, 40.0, 45.0, 5.0));

        // Train 5: Bihar Sampark Kranti Express NDLS -> PNBE
        Train bsk = new Train("12566", "Bihar Sampark Kranti Express", TrainType.SUPERFAST, ndls, pnbe,
                "13:00", "03:20", 860, "Daily", true, "High passenger capacity superfast connectivity to Bihar");
        bsk = trainRepository.save(bsk);
        trainRouteRepository.save(new TrainRoute(bsk, ndls, 1, "12:40", "13:00", 0, 0, "14"));
        trainRouteRepository.save(new TrainRoute(bsk, cnb, 2, "18:10", "18:15", 440, 5, "9"));
        trainRouteRepository.save(new TrainRoute(bsk, pnbe, 3, "03:20", "03:20", 1000, 0, "1"));

        Coach bskS1 = createCoachWithSeats(bsk, "S1", CoachType.SLEEPER, 72);
        Coach bskS2 = createCoachWithSeats(bsk, "S2", CoachType.SLEEPER, 72);
        Coach bskB1 = createCoachWithSeats(bsk, "B1", CoachType.THIRD_AC, 64);
        fareRepository.save(new Fare(bsk, CoachType.SLEEPER, 480.0, 40.0, 45.0, 5.0));
        fareRepository.save(new Fare(bsk, CoachType.THIRD_AC, 1280.0, 40.0, 45.0, 5.0));

        // 4. Seed Sample Completed Bookings for Testing PNR and Dashboard
        LocalDate travelDate = LocalDate.now().plusDays(2);

        // Booking 1: Rahul Sharma on Vande Bharat
        Booking b1 = new Booking("RN-2026-X83K1", "8492019384", passenger1, vb, ndls, bsb,
                travelDate, CoachType.CHAIR_CAR, 2, 3500.0, 183.50, 170.0, 3853.50, BookingStatus.CONFIRMED);
        b1 = bookingRepository.save(b1);

        BookingPassenger bp1 = new BookingPassenger(b1, "Rahul Sharma", 28, "MALE", "WINDOW", "Aadhaar", "XXXX-XXXX-9102");
        bp1.setAssignedCoach("C1");
        bp1.setAssignedSeat(12);
        bp1.setAssignedBerth("WINDOW");

        BookingPassenger bp2 = new BookingPassenger(b1, "Pooja Sharma", 26, "FEMALE", "AISLE", "Aadhaar", "XXXX-XXXX-9103");
        bp2.setAssignedCoach("C1");
        bp2.setAssignedSeat(13);
        bp2.setAssignedBerth("AISLE");

        bookingPassengerRepository.saveAll(List.of(bp1, bp2));

        // Booked Seats
        Seat s12 = seatRepository.findByCoachAndSeatNumber(vbC1, 12).orElse(null);
        Seat s13 = seatRepository.findByCoachAndSeatNumber(vbC1, 13).orElse(null);
        if (s12 != null) bookedSeatRepository.save(new BookedSeat(vb, travelDate, vbC1, s12, b1, "BOOKED"));
        if (s13 != null) bookedSeatRepository.save(new BookedSeat(vb, travelDate, vbC1, s13, b1, "BOOKED"));

        // Payment
        Payment p1 = new Payment(b1, "order_rn_1001", 3853.50, "INR", PaymentStatus.SUCCESS);
        p1.setPaymentId("pay_rn_9001");
        p1.setPaymentMethod("UPI");
        p1.setPaymentSignature("test_verified_signature");
        paymentRepository.save(p1);

        // Booking 2: Sameer Khan on Mumbai Rajdhani
        LocalDate travelDate2 = LocalDate.now().plusDays(5);
        Booking b2 = new Booking("RN-2026-Y92M4", "4291857201", passenger2, rajdhani, ndls, bct,
                travelDate2, CoachType.THIRD_AC, 1, 2080.0, 112.50, 85.0, 2277.50, BookingStatus.CONFIRMED);
        b2 = bookingRepository.save(b2);

        BookingPassenger bp3 = new BookingPassenger(b2, "Sameer Khan", 24, "MALE", "LOWER", "Passport", "P-8492019");
        bp3.setAssignedCoach("B1");
        bp3.setAssignedSeat(15);
        bp3.setAssignedBerth("LOWER");
        bookingPassengerRepository.save(bp3);

        Seat s15 = seatRepository.findByCoachAndSeatNumber(rajB1, 15).orElse(null);
        if (s15 != null) bookedSeatRepository.save(new BookedSeat(rajdhani, travelDate2, rajB1, s15, b2, "BOOKED"));

        Payment p2 = new Payment(b2, "order_rn_1002", 2277.50, "INR", PaymentStatus.SUCCESS);
        p2.setPaymentId("pay_rn_9002");
        p2.setPaymentMethod("CARD");
        paymentRepository.save(p2);

        // Booking 3: Cancelled booking with refund for testing
        Booking b3 = new Booking("RN-2026-C11Z9", "6190482715", passenger1, shatabdi, ndls, bpl,
                LocalDate.now().plusDays(7), CoachType.CHAIR_CAR, 1, 1180.0, 63.25, 85.0, 1328.25, BookingStatus.CANCELLED);
        b3 = bookingRepository.save(b3);

        BookingPassenger bp4 = new BookingPassenger(b3, "Amit Verma", 35, "MALE", "WINDOW", "PAN", "ABCDE1234F");
        bp4.setAssignedCoach("C1");
        bp4.setAssignedSeat(22);
        bp4.setAssignedBerth("WINDOW");
        bookingPassengerRepository.save(bp4);

        refundRepository.save(new Refund(b3, "ref_rn_7701", 1328.25, 265.65, 1062.60, RefundStatus.PROCESSED, "Trip postponed by passenger"));

        // Notifications
        notificationRepository.save(new Notification(passenger1, "Booking Confirmed",
                "Your Vande Bharat ticket (PNR: 8492019384) from NDLS to BSB is confirmed.", "BOOKING"));
        notificationRepository.save(new Notification(passenger2, "Welcome to RailNova",
                "Enjoy seamless railway ticket booking with zero hidden charges.", "SYSTEM"));

        // Support Tickets
        supportTicketRepository.save(new SupportTicket(passenger1, "8492019384", "Catering meal choice confirmation",
                "Can I choose Jain Vegetarian meal preference for train 22436?"));

        log.info("RailNova seed data successfully loaded! Ready for production and testing.");
    }

    private Coach createCoachWithSeats(Train train, String code, CoachType type, int totalSeats) {
        Coach coach = new Coach(train, code, type, totalSeats);
        coach = coachRepository.save(coach);

        List<Seat> seats = new ArrayList<>();
        BerthType[] berthOrder;
        if (type == CoachType.CHAIR_CAR || type == CoachType.EXECUTIVE_CHAIR_CAR) {
            berthOrder = new BerthType[]{BerthType.WINDOW, BerthType.AISLE, BerthType.AISLE, BerthType.WINDOW};
        } else {
            berthOrder = new BerthType[]{
                    BerthType.LOWER, BerthType.MIDDLE, BerthType.UPPER,
                    BerthType.LOWER, BerthType.MIDDLE, BerthType.UPPER,
                    BerthType.SIDE_LOWER, BerthType.SIDE_UPPER
            };
        }

        for (int i = 1; i <= totalSeats; i++) {
            BerthType bt = berthOrder[(i - 1) % berthOrder.length];
            int row = (i - 1) / berthOrder.length + 1;
            seats.add(new Seat(coach, i, bt, row));
        }
        seatRepository.saveAll(seats);
        return coach;
    }
}
