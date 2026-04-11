# 🧪 Test Case Documentation

## 📌 Overview

This document defines all functional, negative, integration, and edge test scenarios for the Payment Orchestration System.

---

# ✅ 1. Sanity Test Cases

| TC ID  | Scenario       | Input         | Expected Output |
| ------ | -------------- | ------------- | --------------- |
| TC-001 | Create Payment | Valid request | 201 CREATED     |
| TC-002 | Get Payment    | Valid ID      | Payment details |

---

# 🔁 2. Functional Test Cases

| TC ID  | Scenario        | Input           | Expected Output |
| ------ | --------------- | --------------- | --------------- |
| TC-003 | CARD routing    | method=CARD     | Provider A      |
| TC-004 | UPI routing     | method=UPI      | Provider B      |
| TC-005 | Payment success | Valid input     | SUCCESS         |
| TC-006 | Payment failure | Retry exhausted | FAILED          |

---

# 🔗 3. Integration Test Cases

| TC ID  | Scenario        | Component   | Expected Result  |
| ------ | --------------- | ----------- | ---------------- |
| TC-007 | DB Persistence  | PostgreSQL  | Record created   |
| TC-008 | Redis Cache     | Redis       | Key created      |
| TC-009 | End-to-end flow | Full system | Success response |

---

# ❌ 4. Negative Test Cases

| TC ID  | Scenario            | Input          | Expected Output  |
| ------ | ------------------- | -------------- | ---------------- |
| TC-010 | Invalid method      | method=BTC     | Error            |
| TC-011 | Missing fields      | Empty request  | Validation error |
| TC-012 | Duplicate key       | Same request   | 200 OK           |
| TC-013 | Concurrent requests | Same key       | Same response    |
| TC-014 | Provider failure    | Forced failure | FAILED           |

---

# 🔥 5. Idempotency Test Cases

| TC ID  | Scenario            | Expected Behavior |
| ------ | ------------------- | ----------------- |
| TC-015 | First request       | 201 CREATED       |
| TC-016 | Duplicate request   | 200 OK            |
| TC-017 | In-progress request | 202 ACCEPTED      |

---

# 🔁 6. Retry Test Cases

| TC ID  | Scenario            | Expected Behavior |
| ------ | ------------------- | ----------------- |
| TC-018 | First attempt fails | Retry triggered   |
| TC-019 | Retry succeeds      | SUCCESS           |
| TC-020 | All retries fail    | FAILED            |

---

# ⚡ 7. Performance Test Cases

| TC ID  | Scenario    | Expected Behavior   |
| ------ | ----------- | ------------------- |
| TC-021 | High load   | Stable system       |
| TC-022 | Redis hit   | Faster response     |
| TC-023 | DB fallback | Consistent behavior |

---

# 🧠 8. Edge Cases

| TC ID  | Scenario                   | Expected Behavior |
| ------ | -------------------------- | ----------------- |
| TC-024 | Same key different payload | Original response |
| TC-025 | Redis unavailable          | DB fallback       |
| TC-026 | DB unavailable             | Error handling    |

---

# 🧪 9. Automated Unit Tests

Covered in:

```text
src/test/java/com/yuno/payment/service/PaymentServiceImplTest.java
src/test/java/com/yuno/payment/service/PaymentControllerTest.java
```

### Covered Scenarios:

* Payment creation
* Idempotency handling
* Processing state (202)
* Failure handling

---

# 📊 Coverage Summary

| Area        | Status |
| ----------- | ------ |
| Functional  | ✅      |
| Negative    | ✅      |
| Integration | ✅      |
| Retry       | ✅      |
| Idempotency | ✅      |
| Edge Cases  | ✅      |

---
