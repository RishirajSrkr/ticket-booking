# 🎟️ TicketLock — Movie Ticket Booking System (Backend)

A backend-focused movie ticket booking system designed to handle **high-concurrency seat booking** using **Redis distributed locking**, with proper booking lifecycle management and cleanup.

This project focuses on **correctness under load**, not just CRUD operations.

---

## 🚀 Features

- 🎬 Movie & show management
- 💺 Seat booking with **Redis distributed locking**
- ⏳ Seat lock expiry using TTL
- 💳 Payment completion flow (dummy payment)
- ❌ Automatic expiry of unpaid bookings
- 🔐 JWT-based authentication & authorization
- 🧹 Scheduled cleanup job for expired locks and bookings
- ⚡ Concurrency-safe booking under heavy parallel load
- 🧪 Stress-tested with multi-threaded race condition tests

---

## 🧠 Core Design Principles

### 1️⃣ Backend as the Source of Truth
- Seat availability and booking state are **always validated server-side**
- Frontend never assumes seat state

---

### 2️⃣ Redis Distributed Locking
- `SETNX + TTL` used to lock seats atomically
- Prevents double booking under concurrent requests
- Locks auto-expire if payment is not completed

---

### 3️⃣ Booking Lifecycle

```
AVAILABLE → LOCKED → COMPLETED
                  ↘
                   EXPIRED
```

- Seats are locked temporarily during payment
- Unpaid bookings expire automatically
- Expired locks are cleaned via a scheduled job

---

### 4️⃣ Concurrency Safety (Key Focus)
- Simultaneous booking attempts handled safely
- Only **one user can book a seat**, even under heavy load
- Verified using parallel execution tests

---

## 🧪 Concurrency Testing (Integration Test)

The system was validated using a **Spring Boot integration test** that simulates real concurrent users.

### Test Details
- Uses `@SpringBootTest` with a random port
- Executes real HTTP requests via `TestRestTemplate`
- Runs against actual Redis and database instances
- Simulates **100 parallel users** attempting to book the same seat
- Synchronizes thread start using `CountDownLatch`

### Result
- ✅ Exactly **1 booking succeeds**
- ❌ All other requests fail gracefully
- ✅ Confirms Redis distributed locking prevents race conditions

---

## ▶️ Running the Project Locally

### 1️⃣ Clone the repository
```bash
git clone https://github.com/RishirajSrkr/ticket-booking.git
cd ticket-booking
```

### 2️⃣ Start dependencies
```bash
docker-compose up -d
```

### 3️⃣ Run the application
```bash
./mvnw spring-boot:run
```

---

## 🔒 Authentication

- JWT-based authentication
- Protected endpoints for booking and payment
- Authorization checks ensure:
  - Only the booking owner can complete payment
  - Expired or invalid bookings cannot be paid

---

## 🏷️ Versioning

- Current stable version: **v1.0.0**
- Tagged after full concurrency validation and system stabilization

---

## 🚧 What This Project Is (and Is Not)

### ✅ This project **is**
- A backend engineering showcase
- Focused on correctness, safety, and real-world problems
- Designed to explain and defend in interviews

### ❌ This project is **not**
- A frontend-heavy app
- A payment gateway integration demo
- A microservices or Kafka-based system (intentionally kept focused)

---

## 🔮 Future Enhancements (Planned Separately)

- Real-time seat updates using **Redis Pub/Sub + WebSockets**
- Read-only live seat availability board
- Separation into learning-focused real-time modules

---

## 👨‍💻 Author

**Rishiraj Sarkar**  
Backend Developer (Java, Spring Boot)

GitHub: https://github.com/RishirajSrkr

---

## ⭐ Final Note

This project was built to solve **one hard problem well**:  
**concurrent seat booking without race conditions**.

That problem is solved, tested, and stabilized.
