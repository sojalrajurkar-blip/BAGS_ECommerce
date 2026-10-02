-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V8
-- Target: PostgreSQL 18+
-- Description: Local Mock Payments, Transactions Ledger & Initial Seed Records
-- ============================================================================

-- 1. Create Payments Table
DROP TABLE IF EXISTS payment_transactions CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
CREATE TABLE IF NOT EXISTS payments (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    order_number VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) REFERENCES customers(id) ON DELETE SET NULL,
    customer_email VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(16) NOT NULL DEFAULT 'INR',
    payment_method VARCHAR(64) NOT NULL DEFAULT 'UPI',
    payment_provider VARCHAR(64) NOT NULL DEFAULT 'MOCK_GATEWAY',
    status VARCHAR(64) NOT NULL DEFAULT 'INITIATED',
    transaction_reference VARCHAR(128) UNIQUE,
    idempotency_key VARCHAR(128),
    failure_reason TEXT,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Payment Transactions Table
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGSERIAL PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    transaction_type VARCHAR(64) NOT NULL DEFAULT 'PAYMENT_ATTEMPT',
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(64) NOT NULL DEFAULT 'SUCCESS',
    gateway_reference VARCHAR(128),
    gateway_response_code VARCHAR(64),
    gateway_message TEXT,
    raw_payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Indexes for fast retrieval
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_order_number ON payments(order_number);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_customer_email ON payments(customer_email);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_payment_id ON payment_transactions(payment_id);

-- 4. Seed Historical Luxury Payment Records
INSERT INTO payments (
    id, order_id, order_number, customer_id, customer_email, amount, currency,
    payment_method, payment_provider, status, transaction_reference, failure_reason,
    metadata, created_at, updated_at
) VALUES
-- Payment 1 for Order #RRA89241 (Delivered)
(
    'pay-89241', 'RRA-89241', '#RRA89241', 'cust-admin', 'admin@rora-luxury.com',
    8798.00, 'INR', 'CARD', 'MOCK_GATEWAY', 'SUCCESS', 'TXN-MOCK-89241-SUCCESS', NULL,
    '{"cardNetwork": "VISA", "last4": "4242", "cardHolder": "Sarah Johnson", "authCode": "AUTH-99214"}',
    TIMESTAMP WITH TIME ZONE '2026-04-28 09:15:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-28 09:15:00+05:30'
),
-- Payment 2 for Order #RRA89105 (Shipped)
(
    'pay-89105', 'RRA-89105', '#RRA89105', 'cust-admin', 'admin@rora-luxury.com',
    6499.00, 'INR', 'UPI', 'MOCK_GATEWAY', 'SUCCESS', 'TXN-MOCK-89105-SUCCESS', NULL,
    '{"upiApp": "GooglePay", "vpa": "arjun.mehta@okaxis", "rrn": "392019481029"}',
    TIMESTAMP WITH TIME ZONE '2026-04-14 11:21:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-14 11:21:00+05:30'
),
-- Payment 3 for Order #RRA88940 (Processing)
(
    'pay-88940', 'RRA-88940', '#RRA88940', 'cust-admin', 'admin@rora-luxury.com',
    4409.10, 'INR', 'UPI', 'MOCK_GATEWAY', 'SUCCESS', 'TXN-MOCK-88940-SUCCESS', NULL,
    '{"upiApp": "PhonePe", "vpa": "priya.sundaram@ybl", "rrn": "392019481030"}',
    TIMESTAMP WITH TIME ZONE '2026-04-20 14:31:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-20 14:31:00+05:30'
)
ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    amount = EXCLUDED.amount,
    updated_at = CURRENT_TIMESTAMP;

-- 5. Seed Payment Transaction Ledger Entries
INSERT INTO payment_transactions (
    payment_id, transaction_type, amount, status, gateway_reference, gateway_response_code, gateway_message, created_at
) VALUES
('pay-89241', 'CAPTURE', 8798.00, 'SUCCESS', 'GATEWAY-REF-89241-CAP', '200', 'Payment authorized and captured successfully', TIMESTAMP WITH TIME ZONE '2026-04-28 09:15:00+05:30'),
('pay-89105', 'CAPTURE', 6499.00, 'SUCCESS', 'GATEWAY-REF-89105-CAP', '200', 'UPI Collect approved by customer bank', TIMESTAMP WITH TIME ZONE '2026-04-14 11:21:00+05:30'),
('pay-88940', 'CAPTURE', 4409.10, 'SUCCESS', 'GATEWAY-REF-88940-CAP', '200', 'UPI Intent transaction successful', TIMESTAMP WITH TIME ZONE '2026-04-20 14:31:00+05:30');
