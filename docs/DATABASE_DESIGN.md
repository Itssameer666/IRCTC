# RailNova Database Design & Architecture

Comprehensive database schema documentation for the **RailNova Railway Reservation & Ticket Booking Platform**.

---

## 1. Database Specifications
- **DBMS:** MySQL 8.0+ / MySQL 9.x (also compatible with H2 in-memory MySQL emulation mode)
- **Character Set:** `utf8mb4`
- **Collation:** `utf8mb4_unicode_ci`
- **Storage Engine:** `InnoDB` (ACID compliance with row-level locking)

---

## 2. Entity-Relationship Overview

```
 +-------------+       +---------------+       +------------------+
 |    users    | 1---* |   bookings    | 1---* |booking_passengers|
 +-------------+       +---------------+       +------------------+
        |                      |                         |
        | 1                    | 1                       | 1
        |                      |                         |
        v *                    v 1                       v *
 +-------------+       +---------------+       +------------------+
 |saved_routes |       |   payments    |       |   booked_seats   |
 +-------------+       +---------------+       +------------------+
                               |                         |
                               v *                       | *
                       +---------------+                 |
                       |    refunds    |                 |
                       +---------------+                 |
                                                         |
 +-------------+       +---------------+                 |
 |   stations  | 1---* |  train_routes |                 |
 +-------------+       +---------------+                 |
        | 1                    | *                       |
        |                      |                         |
        v *                    v 1                       |
 +-------------+       +---------------+                 |
 |   trains    | 1---* |    coaches    | 1---*           |
 +-------------+       +---------------+       |         |
        | 1                    | 1             |         |
        |                      |               v *       v 1
        v *                    |          +---------------+
 +-------------+               +--------> |     seats     |
 |    fares    |                          +---------------+
 +-------------+
```

---

## 3. Concurrency & Overbooking Prevention Strategy

A primary challenge in railway ticketing systems is preventing two concurrent users from reserving the exact same seat on the same train and travel date.

RailNova addresses this using a dedicated **`booked_seats`** table with a database-level composite unique constraint:

```sql
CONSTRAINT uk_train_date_seat UNIQUE (train_id, journey_date, seat_id)
```

### Execution Flow:
1. When a user requests seat allocation (either manually through the visual coach picker or via automatic algorithm):
2. The transaction executes under `Isolation.SERIALIZABLE` or pessimistic locking.
3. If another transaction has already committed or inserted a reservation for `(train_id, journey_date, seat_id)`, the MySQL unique index throws a constraint violation.
4. The service catches this and throws `SeatUnavailableException`, safely rolling back without corrupting the reservation state.
5. In addition, when a booking is cancelled, the `booked_seats` records are released, immediately restoring capacity for subsequent passenger searches.

---

## 4. Tables Detail

### 4.1 `users`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique User ID |
| `email` | VARCHAR(100) | NOT NULL, UNIQUE, INDEX | User login email |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `full_name` | VARCHAR(100) | NOT NULL | Passenger / Admin name |
| `phone` | VARCHAR(20) | NULL | Phone number |
| `role` | VARCHAR(20) | NOT NULL | `ROLE_PASSENGER`, `ROLE_ADMIN`, `ROLE_STAFF` |
| `active` | BOOLEAN | NOT NULL DEFAULT TRUE | Account active flag |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Registration timestamp |

### 4.2 `stations`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Station ID |
| `code` | VARCHAR(10) | NOT NULL, UNIQUE, INDEX | Station code (e.g. `NDLS`, `BCT`) |
| `name` | VARCHAR(100) | NOT NULL | Station name |
| `city` | VARCHAR(50) | NOT NULL | Station city |
| `state` | VARCHAR(50) | NOT NULL | State |
| `zone` | VARCHAR(20) | NULL | Railway zone (`NR`, `WR`, `CR`) |

### 4.3 `trains`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Train ID |
| `train_number` | VARCHAR(10) | NOT NULL, UNIQUE, INDEX | Train Number (e.g. `22436`) |
| `train_name` | VARCHAR(100) | NOT NULL | Train Name |
| `train_type` | VARCHAR(30) | NOT NULL | `VANDE_BHARAT`, `RAJDHANI`, etc. |
| `source_station_id` | BIGINT | FOREIGN KEY (`stations.id`) | Origin station |
| `destination_station_id` | BIGINT | FOREIGN KEY (`stations.id`) | Terminating station |
| `departure_time` | VARCHAR(10) | NOT NULL | Departure time (e.g. `06:00`) |
| `arrival_time` | VARCHAR(10) | NOT NULL | Arrival time (e.g. `14:00`) |
| `duration_minutes` | INT | NOT NULL | Total travel duration |
| `runs_on_days` | VARCHAR(100) | DEFAULT 'Daily' | Active operating days |
| `active` | BOOLEAN | DEFAULT TRUE | Operational flag |

### 4.4 `bookings`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal Booking ID |
| `booking_reference` | VARCHAR(30) | NOT NULL, UNIQUE, INDEX | Human-readable ref (e.g. `RN-2026-X83K1`) |
| `pnr_number` | VARCHAR(12) | NOT NULL, UNIQUE, INDEX | 10-digit PNR Number |
| `user_id` | BIGINT | FOREIGN KEY (`users.id`) | Booking owner |
| `train_id` | BIGINT | FOREIGN KEY (`trains.id`) | Reserved train |
| `journey_date` | DATE | NOT NULL, INDEX | Travel date |
| `coach_type` | VARCHAR(30) | NOT NULL | Class (`CHAIR_CAR`, `THIRD_AC`) |
| `total_passengers` | INT | NOT NULL | Passenger count |
| `total_amount` | DOUBLE | NOT NULL | Final paid fare |
| `booking_status` | VARCHAR(30) | NOT NULL | `CONFIRMED`, `CANCELLED`, `PENDING` |
| `booking_time` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Booking timestamp |
