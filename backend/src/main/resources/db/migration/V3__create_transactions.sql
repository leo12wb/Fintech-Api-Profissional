CREATE TABLE financial_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(20) NOT NULL,
    source_account_id UUID REFERENCES accounts(id),
    destination_account_id UUID REFERENCES accounts(id),
    amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    idempotency_key VARCHAR(255),
    business_reference VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_transactions_type CHECK (type IN ('DEPOSIT', 'TRANSFER', 'REVERSAL')),
    CONSTRAINT chk_transactions_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    CONSTRAINT chk_transactions_amount_positive CHECK (amount_cents > 0)
);

CREATE UNIQUE INDEX idx_transactions_idempotency_key ON financial_transactions (idempotency_key) WHERE idempotency_key IS NOT NULL;
CREATE INDEX idx_transactions_source_account ON financial_transactions (source_account_id);
CREATE INDEX idx_transactions_destination_account ON financial_transactions (destination_account_id);
CREATE INDEX idx_transactions_created_at ON financial_transactions (created_at DESC);
