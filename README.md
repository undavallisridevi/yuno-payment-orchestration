# 💳 Yuno Payment Orchestration System

## 📌 Overview

This project implements a simplified **payment orchestration system**, inspired by real-world platforms like Yuno.

It demonstrates backend engineering concepts such as:

* Payment routing
* Idempotency handling
* Retry mechanisms with exponential backoff
* Distributed caching (Redis)
* Observability (Spring Boot Actuator)
* Containerized deployment using Docker

---

## 🏗️ Architecture

```text
Client
  ↓
Controller Layer
  ↓
Service Layer (Idempotency + Business Logic)
  ↓
Orchestration Engine
  ↓
Routing Engine
  ↓
Provider Connectors (A / B)
  ↓
Persistence Layer (PostgreSQL)
  ↓
Idempotency Cache (Redis)
```

---

## ⚙️ Technical Design

### 🔹 Payment APIs

* Create Payment
* Fetch Payment

### 🔁 Idempotency

* Prevents duplicate payment processing
* Implemented using:

  * Redis (fast lookup)
  * PostgreSQL (backup consistency and unique constraint)
* Handles race conditions using DB constraint

---

### 🔄 Retry Mechanism

* Implemented using **Spring Retry**
* Features:

  * Max 3 attempts
  * Exponential backoff (200ms → 400ms → 800ms)
  * Recovery method for failure handling

---

### 🔀 Routing Logic

* CARD → Provider A
* UPI → Provider B
* Implemented using Strategy Pattern

---

### 📊 Status Handling

* CREATED
* PROCESSING
* SUCCESS
* FAILED
* Supports concurrent requests

---

### 🌐 HTTP Status Codes

| Scenario          | Status Code |
|-------------------|------------|
| New Payment       | 201 Created |
| Duplicate Request | 200 OK      |
| Processing        | 202 Accepted |

### 📊 Observability & Metrics

* Integrated Spring Boot Actuator
* Health check endpoint:

  ```text
  /actuator/health
  ```


---

## 🚀 Installation & Setup

### 🔹 Prerequisites

**Option 1: Run using Docker (Recommended)**

* Docker & Docker Compose

**Option 2: Run using JAR**

* Java 17

**Option 3: Build from source (optional)**

* Maven

---

## 📦 Build JAR (Only if building from source)

```bash
mvn clean package
```

Output:

```text
target/payment.jar
```

👉 Skip this step if you already have the JAR.

---

## 🐳 Run Using Docker (Recommended)

```bash
docker-compose up --build
```

### Services:

* App → http://localhost:8080
* PostgreSQL → 5432
* Redis → 6379

👉 No need to install Java or Maven locally when using Docker.

---

## ▶️ Run Locally

### Step 1: Start services

* PostgreSQL (DB: payments)
* Redis

### Option A: Run using JAR

```bash
java -jar target/payment.jar
```

### Option B: Run using Maven (for development)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Step 2: Run application

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Access API

```bash
http://localhost:8080/payments
```
---

## ⚙️ Configuration

Profiles:

* `local` → localhost services
* `docker` → containerized services

Files:

* application-local.properties
* application-docker.properties

---

## 📡 API Endpoints

### 🔹 Create Payment

```text
POST /payments
```

Request:

```json
{
  "amount": 100,
  "currency": "INR",
  "method": "CARD",
  "idempotencyKey": "abc123"
}
```

---

### 🔹 Get Payment

```text
GET /payments/{id}
```

---

## 📊 Response Behavior

| Scenario            | Status       |
| ------------------- | ------------ |
| First request       | 201 CREATED  |
| Duplicate request   | 200 OK       |
| In-progress request | 202 ACCEPTED |

---

## 🧪 Testing

Run unit tests:

```bash
mvn test
```

Test coverage includes:

* Payment creation flow
* Idempotency handling
* Processing state handling

---

## ⚡ Performance Considerations

* Redis for fast idempotency lookup
* Retry mechanism improves resilience
* DB as source of truth
* Cache TTL prevents stale data

---

## 🧠 Design Decisions

* Used Redis + DB for hybrid idempotency
* Used Spring Retry instead of manual retry loops
* Used Docker for consistent environment setup
* Used Flyway for schema versioning

---

## 🔐 Security Considerations

* Input validation
* Idempotency prevents duplicate execution
* Proper exception handling

---


## 🚀 Future Improvements

* Circuit breaker (Resilience4j)
* Async processing (Kafka)
* Rate limiting
* Metrics & dashboards (Prometheus + Grafana)
* Multi-provider failover

---

## 📦 Deliverables

* Public GitHub repository
* Dockerized application
* Test case documentation

---

## 🔗 GitHub Repository

https://github.com/undavallisridevi/yuno-payment-orchestration
