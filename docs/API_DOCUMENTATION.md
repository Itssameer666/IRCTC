# RailNova REST API Documentation

Comprehensive REST API reference for **RailNova Railway Reservation & Ticket Booking Platform**.

- **Base URL (Local):** `http://localhost:8080/api`
- **Swagger UI Interactive Documentation:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec:** `http://localhost:8080/v3/api-docs`

---

## 1. Authentication APIs

### 1.1 Register New Account
- **Endpoint:** `POST /api/auth/register`
- **Access:** Public
- **Request Body:**
```json
{
  "fullName": "Rahul Sharma",
  "email": "user@railnova.com",
  "phone": "+91 9876543210",
  "password": "Password@123",
  "role": "ROLE_PASSENGER"
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Registration successful",
  "status": 201,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "user": {
      "id": 3,
      "email": "user@railnova.com",
      "fullName": "Rahul Sharma",
      "phone": "+91 9876543210",
      "role": "ROLE_PASSENGER",
      "active": true
    }
  },
  "timestamp": "2026-10-06T21:40:00"
}
```

### 1.2 User Login
- **Endpoint:** `POST /api/auth/login`
- **Access:** Public
- **Request Body:**
```json
{
  "email": "user@railnova.com",
  "password": "Password@123"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "status": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "user": {
      "id": 3,
      "email": "user@railnova.com",
      "fullName": "Rahul Sharma",
      "phone": "+91 9876543210",
      "role": "ROLE_PASSENGER",
      "active": true
    }
  },
  "timestamp": "2026-10-06T21:40:00"
}
```

---

## 2. Train Search & Timetable APIs

### 2.1 Search Trains
- **Endpoint:** `GET /api/trains/search`
- **Access:** Public
- **Query Parameters:**
  - `from` (string): Station Code or Name (e.g. `NDLS`)
  - `to` (string): Station Code or Name (e.g. `BSB`)
  - `date` (ISO date): Journey Date (`YYYY-MM-DD`)
  - `classType` (string): `1A`, `2A`, `3A`, `SL`, `CC`, `EC`, or `ALL`
  - `trainType` (string): `VANDE_BHARAT`, `RAJDHANI`, `SHATABDI`, `SUPERFAST`
  - `maxPrice` (number): Max fare filter
  - `timeSlot` (string): `morning`, `afternoon`, `evening`, `early`
  - `sortBy` (string): `lowest_fare`, `earliest_departure`, `shortest_duration`
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Trains found: 1",
  "status": 200,
  "data": [
    {
      "id": 1,
      "trainNumber": "22436",
      "trainName": "Vande Bharat Express",
      "trainType": "VANDE_BHARAT",
      "trainTypeName": "VANDE BHARAT",
      "sourceStationCode": "NDLS",
      "sourceStationName": "New Delhi",
      "destinationStationCode": "BSB",
      "destinationStationName": "Varanasi Junction",
      "departureTime": "06:00",
      "arrivalTime": "14:00",
      "durationMinutes": 480,
      "durationFormatted": "8h 0m",
      "runsOnDays": "Daily",
      "startingFare": 1750.0,
      "classAvailabilities": [
        {
          "coachTypeCode": "CC",
          "coachTypeName": "AC Chair Car",
          "coachType": "CHAIR_CAR",
          "fare": 1750.0,
          "availableSeats": 106,
          "status": "AVAILABLE 106"
        },
        {
          "coachTypeCode": "EC",
          "coachTypeName": "Exec Chair Car",
          "coachType": "EXECUTIVE_CHAIR_CAR",
          "fare": 3300.0,
          "availableSeats": 40,
          "status": "AVAILABLE 40"
        }
      ]
    }
  ]
}
```

### 2.2 Train Detailed Route & Schedule
- **Endpoint:** `GET /api/trains/{id}/details?date=YYYY-MM-DD`
- **Access:** Public
- **Response (200 OK):** Includes route stops with arrival, departure, halt minutes, platform, coach list, and fares.

### 2.3 Visual Coach Seat Map
- **Endpoint:** `GET /api/trains/coaches/{coachId}/seats?date=YYYY-MM-DD`
- **Access:** Public
- **Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "coachId": 1,
    "coachCode": "C1",
    "coachType": "CHAIR_CAR",
    "totalSeats": 54,
    "availableCount": 52,
    "bookedCount": 2,
    "seats": [
      {
        "id": 1,
        "seatNumber": 1,
        "berthType": "WINDOW",
        "berthTypeName": "WINDOW",
        "status": "AVAILABLE"
      },
      {
        "id": 12,
        "seatNumber": 12,
        "berthType": "WINDOW",
        "berthTypeName": "WINDOW",
        "status": "BOOKED"
      }
    ]
  }
}
```

---

## 3. Booking & Concurrency APIs

### 3.1 Create Ticket Booking
- **Endpoint:** `POST /api/bookings`
- **Access:** Authenticated (`Bearer <token>`)
- **Request Body:**
```json
{
  "trainId": 1,
  "journeyDate": "2026-10-08",
  "coachType": "CHAIR_CAR",
  "passengers": [
    {
      "fullName": "Rahul Sharma",
      "age": 28,
      "gender": "MALE",
      "berthPreference": "WINDOW",
      "selectedSeatId": 14
    }
  ]
}
```
- **Response (201 Created):**
```json
{
  "success": true,
  "message": "Booking created successfully. Please complete payment.",
  "status": 201,
  "data": {
    "id": 4,
    "bookingReference": "RN-2026-K91A2",
    "pnrNumber": "8920194821",
    "trainNumber": "22436",
    "trainName": "Vande Bharat Express",
    "sourceStationName": "New Delhi",
    "destinationStationName": "Varanasi Junction",
    "journeyDate": "2026-10-08",
    "totalPassengers": 1,
    "baseAmount": 1750.0,
    "taxAmount": 91.75,
    "serviceCharge": 85.0,
    "totalAmount": 1926.75,
    "bookingStatus": "PENDING",
    "passengers": [
      {
        "fullName": "Rahul Sharma",
        "age": 28,
        "gender": "MALE",
        "assignedCoach": "C1",
        "assignedSeat": 14,
        "assignedBerth": "WINDOW"
      }
    ]
  }
}
```

### 3.2 Cancel Ticket & Process Refund
- **Endpoint:** `PUT /api/bookings/{id}/cancel`
- **Access:** Authenticated
- **Request Body:**
```json
{
  "reason": "Personal travel emergency"
}
```
- **Response (200 OK):**
```json
{
  "success": true,
  "message": "Booking cancelled successfully and refund initiated",
  "status": 200,
  "data": {
    "bookingStatus": "CANCELLED"
  }
}
```

---

## 4. Payment Gateway Integration APIs

### 4.1 Create Payment Order
- **Endpoint:** `POST /api/payments/create-order?bookingId=4`
- **Access:** Authenticated
- **Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderId": "order_rn_1728239019283",
    "amount": 1926.75,
    "currency": "INR",
    "keyId": "rzp_test_railnova_sandbox_key",
    "bookingReference": "RN-2026-K91A2",
    "pnrNumber": "8920194821",
    "bookingId": 4
  }
}
```

### 4.2 Verify HMAC Signature & Confirm Booking
- **Endpoint:** `POST /api/payments/verify`
- **Access:** Authenticated
- **Request Body:**
```json
{
  "bookingId": 4,
  "orderId": "order_rn_1728239019283",
  "paymentId": "pay_rn_1728239088491",
  "signature": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
  "paymentMethod": "UPI"
}
```
- **Response (200 OK):** Booking status updated to `CONFIRMED`.

---

## 5. PNR Status API

### 5.1 Lookup PNR Status
- **Endpoint:** `GET /api/pnr/{pnr}`
- **Access:** Public
- **Response (200 OK):**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "pnrNumber": "8492019384",
    "bookingReference": "RN-2026-X83K1",
    "trainNumber": "22436",
    "trainName": "Vande Bharat Express",
    "sourceStation": "New Delhi (NDLS)",
    "destinationStation": "Varanasi Junction (BSB)",
    "departureTime": "06:00",
    "arrivalTime": "14:00",
    "journeyDate": "2026-10-08",
    "coachType": "AC Chair Car",
    "bookingStatus": "CONFIRMED",
    "passengers": [
      {
        "fullName": "Rahul Sharma",
        "assignedCoach": "C1",
        "assignedSeat": 12,
        "assignedBerth": "WINDOW"
      }
    ]
  }
}
```

---

## 6. Admin APIs

- `GET /api/admin/stats` - Platform metrics and Chart.js aggregation data
- `GET /api/admin/users` - All registered user accounts
- `PUT /api/admin/users/{id}/toggle-status` - Activate or deactivate user
- `POST /api/admin/trains` - Create new train schedule
- `PUT /api/admin/trains/{id}/toggle-status` - Toggle train availability
- `DELETE /api/admin/trains/{id}` - Remove train schedule
- `GET /api/admin/bookings` - All platform bookings
- `GET /api/admin/refunds` - All processed refunds
- `GET /api/admin/audit-logs` - System audit logs
