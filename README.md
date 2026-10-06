# RailNova - Advanced Railway Ticket Booking Platform

[![Java](https://img.shields.io/badge/Java-21%20%7C%2023-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-green.svg)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/Database-MySQL%20%2F%20H2-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Deploy on Render](https://img.shields.io/badge/Deploy-Render-black.svg?logo=render)](https://render.com)

**RailNova** is a modern, production-grade Railway Reservation & Ticket Booking Web Application inspired by modern reservation platforms like IRCTC, built with an original brand identity, sleek UI design, interactive visual coach layouts, and enterprise-grade Spring Boot architecture.

---

## 🌟 Key Features

### 1. Passenger Portal
- **Interactive Train Search:** Real-time search across origin and destination stations, operating days, and travel classes.
- **Dynamic Filters & Sorting:** Filter by train types (Vande Bharat, Rajdhani, Shatabdi, Superfast), departure time slots, fare price sliders, and sort by lowest fare or shortest duration.
- **Visual Coach & Berth Selector:** Interactive visual layout of railway coaches (S1, S2, B1, B2, A1, C1) displaying realistic berths (Lower, Middle, Upper, Side Lower, Side Upper, Window, Aisle) with live color-coded availability.
- **Concurrency & Overbooking Protection:** Database-level transactional isolation and unique constraints preventing simultaneous double-booking of identical seats.
- **Multi-Passenger Reservation:** Dynamic form supporting up to 6 passengers per booking with automated fare calculation (Base Fare, Reservation & Superfast fees, GST, and discount coupon codes like `RAILNOVA50`).
- **Official Sandbox Payment Workflow:** Order creation, simulated checkout (UPI / QR, Test Cards, Net Banking, Wallet), and server-side cryptographic **HMAC-SHA256 signature verification**.
- **Printable E-Ticket (ERS):** Confirmed electronic reservation ticket with scannable QR verification code, realistic 10-digit PNR, passenger berth assignments, and print-ready stylesheet.
- **Live PNR Status Enquiry:** Standalone 10-digit PNR tracking tool showing current journey timeline, boarding station, coach number, and confirmation status.
- **Ticket Cancellation & Auto-Refund:** Transparent cancellation fee calculation with automated refund record creation and immediate seat release.
- **Personal Dashboard:** Upcoming journeys, booking history, saved favourite routes, profile updates, and travel notification alerts.

### 2. Operations & Admin Command Center
- **Key Metrics Dashboard:** Total Users, Active Trains, Today's Bookings, Confirmed Tickets, Cancelled Tickets, Platform Revenue, and Pending Refunds.
- **Interactive Analytics Charts:** Powered by Chart.js for monthly booking volume trends and train type fleet breakdown.
- **Fleet Management:** Create, update, toggle operational status, and delete train schedules.
- **Passenger Bookings & Refund Audit:** Real-time visibility into all bookings, transactions, and audit logs.
- **User Account Governance:** One-click activation/deactivation of passenger accounts.

### 3. Customer Support & Staff Desk
- **Grievance Resolution:** Review customer support tickets and issue official staff resolutions.
- **Passenger Assistance:** View passenger rosters and travel statuses.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Frontend** | HTML5, CSS3, Bootstrap 5.3.3, Vanilla JavaScript, Chart.js 4.4, Bootstrap Icons |
| **Backend** | Java 21 / 23, Spring Boot 3.3.4, Spring MVC, Spring Data JPA, Hibernate, Spring Security 6 |
| **Authentication** | JWT (JSON Web Tokens - JJWT 0.12.6) with BCrypt password hashing |
| **Database** | MySQL 8.0+ / MySQL 9.x (Production) with auto H2 MySQL-mode fallback for local zero-config dev |
| **Build & Tooling** | Apache Maven 3.9+, Maven Wrapper (`mvnw`), Docker (Multi-stage) |
| **Documentation** | Swagger / OpenAPI 3.0 (`springdoc-openapi-starter-webmvc-ui:2.6.0`) |
| **Cloud Deployment** | Render Web Service (Docker containerized) + Cloud MySQL |

---

## 🏗️ Layered Project Structure

```
IRCTC/
├── pom.xml                               # Maven project descriptor
├── Dockerfile                            # Production multi-stage Docker build
├── render.yaml                           # Render Cloud Infrastructure as Code specification
├── .dockerignore                         # Docker build exclusions
├── .gitignore                            # Git repository exclusions
├── database/
│   ├── railnova_schema.sql               # MySQL DDL schema definitions
│   └── railnova_seed_data.sql            # MySQL seed data inserts
├── docs/
│   ├── API_DOCUMENTATION.md              # REST API reference catalog
│   ├── DATABASE_DESIGN.md                # ER diagram and concurrency strategies
│   └── RENDER_DEPLOYMENT_GUIDE.md        # Step-by-step Render cloud deployment guide
└── src/
    ├── main/
    │   ├── java/com/railnova/
    │   │   ├── RailNovaApplication.java  # Main application entrypoint
    │   │   ├── config/                   # OpenAPI, DataInitializer
    │   │   ├── controller/               # REST API Controllers (Auth, Train, Booking, Payment, PNR, Admin)
    │   │   ├── dto/                      # Request & Response Data Transfer Objects
    │   │   ├── entity/                   # JPA Entities (User, Train, Booking, Seat, Payment, Refund, etc.)
    │   │   ├── exception/                # GlobalExceptionHandler (@RestControllerAdvice)
    │   │   ├── repository/               # Spring Data JPA Repositories
    │   │   ├── security/                 # Spring Security, JWT utilities, Authentication filter
    │   │   ├── service/                  # Business logic interfaces
    │   │   │   └── impl/                 # Service implementations
    │   │   └── util/                     # PNR generator, HMAC-SHA256 signature utility
    │   └── resources/
    │       ├── application.properties    # Default local dev properties (H2 MySQL mode)
    │       ├── application-prod.properties # Production MySQL properties
    │       └── static/                   # Standalone Responsive Single Page Application
    │           ├── index.html            # Main UI (Hero, Search, Modals, E-Ticket, Dashboards)
    │           ├── css/style.css         # RailNova design system & print styles
    │           └── js/app.js             # Client application logic & REST API integration
    └── test/
        └── java/com/railnova/
            ├── RailNovaApplicationTests.java # Context loading test
            └── BookingServiceTest.java       # Train search and PNR retrieval tests
```

---

## 🚀 Quick Start (Running Locally)

### Prerequisites
- **Java 17, 21, or 23** installed
- Git installed

### 1. Clone & Enter Project
```bash
git clone https://github.com/Itssameer666/IRCTC.git
cd IRCTC
```

### 2. Run the Application
The project includes the Maven Wrapper. Out of the box, it boots with an in-memory MySQL-compatible database seeded with sample stations, trains, coaches, fares, and demo accounts:

**On Windows (PowerShell / Command Prompt):**
```powershell
.\mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
chmod +x ./mvnw
./mvnw spring-boot:run
```

### 3. Access the Web Application
Open your browser and navigate to:
- **Web Application:** [http://localhost:8080/](http://localhost:8080/)
- **Swagger UI API Documentation:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:railnova_db`, User: `sa`, Password: *blank*)

---

## 🔑 Pre-Seeded Demo Accounts

The application includes one-click demo login buttons directly on the homepage:

| Role | Email | Password | Access Rights |
|---|---|---|---|
| **Admin** | `admin@railnova.com` | `Admin@123` | Full Admin Dashboard, fleet control, revenue charts, user toggles |
| **Staff** | `staff@railnova.com` | `Staff@123` | Staff Portal, customer inquiries, ticket resolution |
| **Passenger** | `user@railnova.com` | `User@123` | Booking tickets, personal dashboard, booking history |
| **Passenger** | `sameer@railnova.com` | `Sameer@123` | Pre-booked Mumbai Rajdhani confirmed reservation |

### Pre-Seeded PNRs for Live Testing
- `8492019384` - Vande Bharat Express (Confirmed 2 Passengers)
- `4291857201` - Mumbai Rajdhani Express (Confirmed 1 Passenger)
- `6190482715` - Bhopal Shatabdi Express (Cancelled & Processed Refund)

---

## 💳 Sandbox Payment Gateway Workflow

RailNova strictly mirrors the official payment gateway architecture:

```
[Frontend Client]
       |
       | 1. POST /api/payments/create-order?bookingId=4
       v
[Spring Boot Backend] ---> Generates unique Order ID & returns public key
       |
       | 2. Displays Sandbox Modal (UPI QR / Test Card 4111 2222 3333 4444)
       v
[Payment Client] ---> Produces Payment ID & HMAC-SHA256 signature
       |
       | 3. POST /api/payments/verify (orderId, paymentId, signature)
       v
[Backend Signature Verification] ---> Verifies HMAC-SHA256(orderId + "|" + paymentId, SECRET)
       |
       +---> [CONFIRMED] Seat locks finalized, Booking marked CONFIRMED, E-Ticket issued!
```

> **Security Guarantee:** The payment secret key (`PAYMENT_KEY_SECRET`) is stored strictly in server-side environment variables and is never exposed to frontend JavaScript.

---

## ☁️ Deployment on Render

RailNova is pre-configured for one-click deployment on Render.

1. Push this repository to GitHub.
2. Follow the detailed steps in [docs/RENDER_DEPLOYMENT_GUIDE.md](docs/RENDER_DEPLOYMENT_GUIDE.md).
3. Set the following Environment Variables in your Render Dashboard:
   - `SPRING_PROFILES_ACTIVE`: `prod`
   - `DB_URL`: `jdbc:mysql://<host>:<port>/<dbname>?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
   - `DB_USERNAME`: `<your_db_username>`
   - `DB_PASSWORD`: `<your_db_password>`
   - `JWT_SECRET`: `<your_256bit_jwt_secret>`

---

## 🧪 Testing

Run the automated test suite with:
```powershell
.\mvnw.cmd test
```

Tests verify:
- Spring Boot Application Context initialization
- Real-time train search between stations (e.g. `NDLS` -> `BSB`)
- PNR lookup and passenger berth extraction

---

## 📄 License

This project is licensed under the Apache License 2.0.
