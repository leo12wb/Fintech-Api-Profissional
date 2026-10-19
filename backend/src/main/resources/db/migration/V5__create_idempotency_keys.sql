CREATE TABLE idempotency_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(255) NOT NULL,
    customer_id UUID NOT NULL REFERENCES customers(id),
    operation_type VARCHAR(50) NOT NULL,
    params_hash VARCHAR(64) NOT NULL,
    response_status INT,
    response_body TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMPTZ NOT NULL DEFAULT (NOW() + INTERVAL '24 hours'),
    CONSTRAINT uq_idempotency_key_customer UNIQUE (idempotency_key, customer_id)
);

CREATE INDEX idx_idempotency_key_customer ON idempotency_keys (idempotency_key, customer_id);
