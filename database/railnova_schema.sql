-- ============================================================================
-- RailNova Railway Reservation & Ticket Booking System
-- MySQL Production Database Schema DDL
-- Compatible with MySQL 8.0+ and MySQL 9.x
-- ============================================================================

CREATE DATABASE IF NOT EXISTS railnova_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE railnova_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_PASSENGER',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- 2. Stations Table
CREATE TABLE IF NOT EXISTS stations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zone VARCHAR(20),
    INDEX idx_station_code (code),
    INDEX idx_station_name (name)
) ENGINE=InnoDB;

-- 3. Trains Table
CREATE TABLE IF NOT EXISTS trains (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_number VARCHAR(10) NOT NULL UNIQUE,
    train_name VARCHAR(100) NOT NULL,
    train_type VARCHAR(30) NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    departure_time VARCHAR(10) NOT NULL,
    arrival_time VARCHAR(10) NOT NULL,
    duration_minutes INT NOT NULL,
    runs_on_days VARCHAR(100) NOT NULL DEFAULT 'Daily',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(255),
    CONSTRAINT fk_trains_source FOREIGN KEY (source_station_id) REFERENCES stations(id) ON DELETE RESTRICT,
    CONSTRAINT fk_trains_dest FOREIGN KEY (destination_station_id) REFERENCES stations(id) ON DELETE RESTRICT,
    INDEX idx_train_number (train_number),
    INDEX idx_train_active (active)
) ENGINE=InnoDB;

-- 4. Train Routes (Intermediate Timetables)
CREATE TABLE IF NOT EXISTS train_routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    stop_number INT NOT NULL,
    arrival_time VARCHAR(10) NOT NULL,
    departure_time VARCHAR(10) NOT NULL,
    distance_km INT NOT NULL DEFAULT 0,
    halt_minutes INT NOT NULL DEFAULT 0,
    platform VARCHAR(20) DEFAULT '1',
    CONSTRAINT fk_routes_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    CONSTRAINT fk_routes_station FOREIGN KEY (station_id) REFERENCES stations(id) ON DELETE RESTRICT,
    INDEX idx_routes_train_stop (train_id, stop_number)
) ENGINE=InnoDB;

-- 5. Coaches Table
CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    coach_code VARCHAR(10) NOT NULL, -- S1, S2, B1, A1, C1
    coach_type VARCHAR(30) NOT NULL, -- SLEEPER, THIRD_AC, SECOND_AC, FIRST_AC, CHAIR_CAR
    total_seats INT NOT NULL,
    CONSTRAINT fk_coaches_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    INDEX idx_coach_train (train_id, coach_type)
) ENGINE=InnoDB;

-- 6. Seats Table
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coach_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    berth_type VARCHAR(30) NOT NULL, -- LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER, WINDOW, AISLE
    seat_row INT,
    CONSTRAINT fk_seats_coach FOREIGN KEY (coach_id) REFERENCES coaches(id) ON DELETE CASCADE,
    INDEX idx_seat_coach_num (coach_id, seat_number)
) ENGINE=InnoDB;

-- 7. Fares Table
CREATE TABLE IF NOT EXISTS fares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    coach_type VARCHAR(30) NOT NULL,
    base_fare DOUBLE NOT NULL,
    reservation_charge DOUBLE NOT NULL DEFAULT 40.0,
    superfast_charge DOUBLE NOT NULL DEFAULT 45.0,
    gst_percentage DOUBLE NOT NULL DEFAULT 5.0,
    CONSTRAINT fk_fares_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    INDEX idx_fare_train_type (train_id, coach_type)
) ENGINE=InnoDB;

-- 8. Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(30) NOT NULL UNIQUE,
    pnr_number VARCHAR(12) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    train_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_type VARCHAR(30) NOT NULL,
    total_passengers INT NOT NULL,
    base_amount DOUBLE NOT NULL,
    tax_amount DOUBLE NOT NULL,
    service_charge DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    booking_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_source FOREIGN KEY (source_station_id) REFERENCES stations(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_dest FOREIGN KEY (destination_station_id) REFERENCES stations(id) ON DELETE RESTRICT,
    INDEX idx_booking_pnr (pnr_number),
    INDEX idx_booking_user (user_id),
    INDEX idx_booking_date (journey_date)
) ENGINE=InnoDB;

-- 9. Booking Passengers Table
CREATE TABLE IF NOT EXISTS booking_passengers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    berth_preference VARCHAR(30),
    assigned_coach VARCHAR(10),
    assigned_seat INT,
    assigned_berth VARCHAR(30),
    id_type VARCHAR(30) DEFAULT 'Aadhaar',
    id_number VARCHAR(50),
    CONSTRAINT fk_passengers_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 10. Booked Seats Table (Guarantees zero concurrent double booking)
CREATE TABLE IF NOT EXISTS booked_seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_id BIGINT NOT NULL,
    journey_date DATE NOT NULL,
    coach_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    CONSTRAINT uk_train_date_seat UNIQUE (train_id, journey_date, seat_id),
    CONSTRAINT fk_booked_seats_train FOREIGN KEY (train_id) REFERENCES trains(id) ON DELETE CASCADE,
    CONSTRAINT fk_booked_seats_coach FOREIGN KEY (coach_id) REFERENCES coaches(id) ON DELETE CASCADE,
    CONSTRAINT fk_booked_seats_seat FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE,
    CONSTRAINT fk_booked_seats_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 11. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    order_id VARCHAR(100) NOT NULL UNIQUE,
    payment_id VARCHAR(100),
    payment_signature VARCHAR(255),
    payment_method VARCHAR(50) DEFAULT 'UPI',
    amount DOUBLE NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 12. Refunds Table
CREATE TABLE IF NOT EXISTS refunds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    refund_id VARCHAR(100) NOT NULL UNIQUE,
    original_amount DOUBLE NOT NULL,
    cancellation_charges DOUBLE NOT NULL,
    refund_amount DOUBLE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reason VARCHAR(255),
    refund_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refunds_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 13. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 14. Support Tickets Table
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pnr_number VARCHAR(20),
    subject VARCHAR(150) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    staff_response VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_support_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 15. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id VARCHAR(50),
    details VARCHAR(1000),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 16. Saved Favourite Routes
CREATE TABLE IF NOT EXISTS saved_routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_routes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_routes_source FOREIGN KEY (source_station_id) REFERENCES stations(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_routes_dest FOREIGN KEY (destination_station_id) REFERENCES stations(id) ON DELETE CASCADE
) ENGINE=InnoDB;
