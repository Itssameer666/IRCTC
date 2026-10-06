-- ============================================================================
-- RailNova Railway Reservation & Ticket Booking System
-- MySQL Seed Data Script
-- ============================================================================

USE railnova_db;

-- 1. Seed Demo Users (Passwords hashed with BCrypt)
-- admin@railnova.com / Admin@123
-- staff@railnova.com / Staff@123
-- user@railnova.com / User@123
-- sameer@railnova.com / Sameer@123
INSERT INTO users (id, full_name, email, password, phone, role, active, created_at) VALUES
(1, 'System Administrator', 'admin@railnova.com', '$2a$10$9Z0L6GkXF1xUeOqD2y3mEuT0k4w5r6v7s8t9u0v1w2x3y4z5a6b7c', '+91 9876543210', 'ROLE_ADMIN', TRUE, NOW()),
(2, 'Senior Station Manager', 'staff@railnova.com', '$2a$10$9Z0L6GkXF1xUeOqD2y3mEuT0k4w5r6v7s8t9u0v1w2x3y4z5a6b7c', '+91 9876543211', 'ROLE_STAFF', TRUE, NOW()),
(3, 'Rahul Sharma', 'user@railnova.com', '$2a$10$9Z0L6GkXF1xUeOqD2y3mEuT0k4w5r6v7s8t9u0v1w2x3y4z5a6b7c', '+91 9876543212', 'ROLE_PASSENGER', TRUE, NOW()),
(4, 'Sameer Khan', 'sameer@railnova.com', '$2a$10$9Z0L6GkXF1xUeOqD2y3mEuT0k4w5r6v7s8t9u0v1w2x3y4z5a6b7c', '+91 9876543213', 'ROLE_PASSENGER', TRUE, NOW())
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- 2. Seed Stations
INSERT INTO stations (id, code, name, city, state, zone) VALUES
(1, 'NDLS', 'New Delhi', 'New Delhi', 'Delhi', 'NR'),
(2, 'BCT', 'Mumbai Central', 'Mumbai', 'Maharashtra', 'WR'),
(3, 'CSMT', 'Chhatrapati Shivaji Maharaj Terminus', 'Mumbai', 'Maharashtra', 'CR'),
(4, 'HWH', 'Howrah Junction', 'Kolkata', 'West Bengal', 'ER'),
(5, 'SBC', 'KSR Bengaluru', 'Bengaluru', 'Karnataka', 'SWR'),
(6, 'MAS', 'Chennai Central', 'Chennai', 'Tamil Nadu', 'SR'),
(7, 'PNBE', 'Patna Junction', 'Patna', 'Bihar', 'ECR'),
(8, 'ADI', 'Ahmedabad Junction', 'Ahmedabad', 'Gujarat', 'WR'),
(9, 'BSB', 'Varanasi Junction', 'Varanasi', 'Uttar Pradesh', 'NR'),
(10, 'CNB', 'Kanpur Central', 'Kanpur', 'Uttar Pradesh', 'NCR'),
(11, 'PRYJ', 'Prayagraj Junction', 'Prayagraj', 'Uttar Pradesh', 'NCR'),
(12, 'BPL', 'Bhopal Junction', 'Bhopal', 'Madhya Pradesh', 'WCR'),
(13, 'AGC', 'Agra Cantt', 'Agra', 'Uttar Pradesh', 'NCR')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Seed Trains
INSERT INTO trains (id, train_number, train_name, train_type, source_station_id, destination_station_id, departure_time, arrival_time, duration_minutes, runs_on_days, active, description) VALUES
(1, '22436', 'Vande Bharat Express', 'VANDE_BHARAT', 1, 9, '06:00', '14:00', 480, 'Daily', TRUE, 'Semi-high speed premium express connecting capital to holy city'),
(2, '12952', 'Mumbai Rajdhani Express', 'RAJDHANI', 1, 2, '16:55', '08:35', 940, 'Daily', TRUE, 'King of Western Railway offering unparalleled comfort and speed'),
(3, '12002', 'Bhopal Shatabdi Express', 'SHATABDI', 1, 12, '06:00', '14:40', 520, 'Daily', TRUE, 'Fastest morning express between New Delhi and Bhopal'),
(4, '12302', 'Howrah Rajdhani Express', 'RAJDHANI', 1, 4, '16:50', '09:55', 1025, 'Daily', TRUE, 'Premier flagship train linking New Delhi with the City of Joy'),
(5, '12566', 'Bihar Sampark Kranti Express', 'SUPERFAST', 1, 7, '13:00', '03:20', 860, 'Daily', TRUE, 'High passenger capacity superfast connectivity to Bihar')
ON DUPLICATE KEY UPDATE train_name=VALUES(train_name);

-- 4. Seed Intermediate Routes (Timetables)
INSERT INTO train_routes (train_id, station_id, stop_number, arrival_time, departure_time, distance_km, halt_minutes, platform) VALUES
-- Vande Bharat (22436)
(1, 1, 1, '05:45', '06:00', 0, 0, '16'),
(1, 10, 2, '10:08', '10:10', 440, 2, '1'),
(1, 11, 3, '12:08', '12:10', 635, 2, '6'),
(1, 9, 4, '14:00', '14:00', 755, 0, '1'),

-- Mumbai Rajdhani (12952)
(2, 1, 1, '16:40', '16:55', 0, 0, '3'),
(2, 13, 2, '18:50', '18:55', 195, 5, '1'),
(2, 2, 3, '08:35', '08:35', 1386, 0, '1'),

-- Bhopal Shatabdi (12002)
(3, 1, 1, '05:45', '06:00', 0, 0, '1'),
(3, 13, 2, '07:50', '07:55', 195, 5, '1'),
(3, 12, 3, '14:40', '14:40', 707, 0, '1'),

-- Howrah Rajdhani (12302)
(4, 1, 1, '16:30', '16:50', 0, 0, '4'),
(4, 10, 2, '21:32', '21:37', 440, 5, '4'),
(4, 11, 3, '23:43', '23:45', 635, 2, '4'),
(4, 4, 4, '09:55', '09:55', 1451, 0, '8'),

-- Bihar Sampark Kranti (12566)
(5, 1, 1, '12:40', '13:00', 0, 0, '14'),
(5, 10, 2, '18:10', '18:15', 440, 5, '9'),
(5, 7, 3, '03:20', '03:20', 1000, 0, '1')
ON DUPLICATE KEY UPDATE stop_number=VALUES(stop_number);

-- 5. Seed Fares
INSERT INTO fares (train_id, coach_type, base_fare, reservation_charge, superfast_charge, gst_percentage) VALUES
(1, 'CHAIR_CAR', 1750.0, 40.0, 45.0, 5.0),
(1, 'EXECUTIVE_CHAIR_CAR', 3300.0, 40.0, 45.0, 5.0),
(2, 'THIRD_AC', 2080.0, 40.0, 45.0, 5.0),
(2, 'SECOND_AC', 2960.0, 40.0, 45.0, 5.0),
(2, 'FIRST_AC', 4850.0, 40.0, 45.0, 5.0),
(3, 'CHAIR_CAR', 1180.0, 40.0, 45.0, 5.0),
(3, 'EXECUTIVE_CHAIR_CAR', 2120.0, 40.0, 45.0, 5.0),
(4, 'THIRD_AC', 2350.0, 40.0, 45.0, 5.0),
(4, 'SECOND_AC', 3290.0, 40.0, 45.0, 5.0),
(5, 'SLEEPER', 480.0, 40.0, 45.0, 5.0),
(5, 'THIRD_AC', 1280.0, 40.0, 45.0, 5.0)
ON DUPLICATE KEY UPDATE base_fare=VALUES(base_fare);
