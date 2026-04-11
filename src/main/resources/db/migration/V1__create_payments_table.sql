CREATE TABLE payments (
    id VARCHAR(255) PRIMARY KEY,
    amount DOUBLE PRECISION,
    currency VARCHAR(10),
    method VARCHAR(20),
    status VARCHAR(20),
    provider VARCHAR(20),
    idempotency_key VARCHAR(255) UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);