-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V10
-- Target: PostgreSQL 18+
-- Description: Return Requests, Return Items, Refund Records & Initial Seeds
-- ============================================================================

-- 1. Create Returns Table
DROP TABLE IF EXISTS refunds CASCADE;
DROP TABLE IF EXISTS return_items CASCADE;
DROP TABLE IF EXISTS returns CASCADE;
CREATE TABLE IF NOT EXISTS returns (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    order_number VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) REFERENCES customers(id) ON DELETE SET NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(32),
    item VARCHAR(255) NOT NULL,
    reason VARCHAR(512) NOT NULL,
    customer_notes TEXT,
    request_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    inspection_status VARCHAR(64) NOT NULL DEFAULT 'AWAITING_HUB_DELIVERY',
    status VARCHAR(64) NOT NULL DEFAULT 'UNDER_REVIEW',
    amount NUMERIC(12, 2) NOT NULL,
    admin_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Return Items Table
CREATE TABLE IF NOT EXISTS return_items (
    id BIGSERIAL PRIMARY KEY,
    return_id VARCHAR(64) NOT NULL REFERENCES returns(id) ON DELETE CASCADE,
    order_item_id VARCHAR(64),
    product_id VARCHAR(64),
    variant_id VARCHAR(64),
    product_name VARCHAR(255) NOT NULL,
    color_name VARCHAR(64),
    quantity INT NOT NULL DEFAULT 1,
    unit_price NUMERIC(12, 2) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL,
    reason VARCHAR(512),
    condition_notes TEXT
);

-- 3. Create Refunds Table
CREATE TABLE IF NOT EXISTS refunds (
    id VARCHAR(64) PRIMARY KEY,
    return_id VARCHAR(64) REFERENCES returns(id) ON DELETE SET NULL,
    return_ref VARCHAR(64),
    order_id VARCHAR(64) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    order_number VARCHAR(64) NOT NULL,
    payment_id VARCHAR(64) REFERENCES payments(id) ON DELETE SET NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    method VARCHAR(128) NOT NULL DEFAULT 'Original Payment Source',
    transaction_ref VARCHAR(128) UNIQUE,
    amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(64) NOT NULL DEFAULT 'COMPLETED',
    processed_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(512),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create Performance Indexes
CREATE INDEX IF NOT EXISTS idx_returns_order_id ON returns(order_id);
CREATE INDEX IF NOT EXISTS idx_returns_order_number ON returns(order_number);
CREATE INDEX IF NOT EXISTS idx_returns_status ON returns(status);
CREATE INDEX IF NOT EXISTS idx_returns_customer_email ON returns(customer_email);
CREATE INDEX IF NOT EXISTS idx_return_items_return_id ON return_items(return_id);
CREATE INDEX IF NOT EXISTS idx_refunds_return_id ON refunds(return_id);
CREATE INDEX IF NOT EXISTS idx_refunds_order_id ON refunds(order_id);
CREATE INDEX IF NOT EXISTS idx_refunds_order_number ON refunds(order_number);
CREATE INDEX IF NOT EXISTS idx_refunds_status ON refunds(status);

-- 5. Seed Historical Return Records
INSERT INTO returns (
    id, order_id, order_number, customer_id, customer_name, customer_email, customer_phone,
    item, reason, customer_notes, request_date, inspection_status, status, amount, admin_notes, created_at, updated_at
) VALUES
-- Return 1: Approved & Refunded
(
    'ret-104', 'RRA-89241', '#RRA89241', 'cust-admin', 'Ananya Deshmukh', 'admin@rora-luxury.com', '+91 98200 12345',
    'The Executive Briefcase (Chestnut Brown)', 'Size / Laptop fit requirement changed', 'Needs smaller compact profile.',
    TIMESTAMP WITH TIME ZONE '2026-03-18 11:30:00+05:30', 'PASSED_PRISTINE', 'APPROVED_AND_REFUNDED', 5499.00,
    'Item inspected at Mumbai Atelier Hub. Pristine tag intact.',
    TIMESTAMP WITH TIME ZONE '2026-03-18 11:30:00+05:30', TIMESTAMP WITH TIME ZONE '2026-03-20 14:00:00+05:30'
),
-- Return 2: Under Review
(
    'ret-105', 'RRA-89105', '#RRA89105', 'cust-admin', 'Kavita Roy', 'admin@rora-luxury.com', '+91 98211 54321',
    'The Minimalist Crossbody (Taupe)', 'Color tone preference', 'Requesting exchange for Obsidian Black variant.',
    TIMESTAMP WITH TIME ZONE '2026-04-22 15:45:00+05:30', 'AWAITING_HUB_DELIVERY', 'UNDER_REVIEW', 2899.00,
    NULL,
    TIMESTAMP WITH TIME ZONE '2026-04-22 15:45:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-22 15:45:00+05:30'
)
ON CONFLICT (id) DO NOTHING;

-- 6. Seed Return Items
INSERT INTO return_items (return_id, product_name, color_name, quantity, unit_price, total_price, reason) VALUES
('ret-104', 'The Executive Briefcase', 'Chestnut Brown', 1, 5499.00, 5499.00, 'Size / Laptop fit requirement changed'),
('ret-105', 'The Minimalist Crossbody', 'Taupe', 1, 2899.00, 2899.00, 'Color tone preference');

-- 7. Seed Refund Records
INSERT INTO refunds (
    id, return_id, return_ref, order_id, order_number, payment_id, customer_name, customer_email,
    method, transaction_ref, amount, status, processed_date, reason, notes, created_at, updated_at
) VALUES
-- Refund 1 (Linked to Return ret-104)
(
    'ref-801', 'ret-104', 'ret-104', 'RRA-89241', '#RRA89241', 'pay-89241', 'Ananya Deshmukh', 'admin@rora-luxury.com',
    'Source Bank (ICICI NetBanking)', 'REF-ICICI-99231', 5499.00, 'COMPLETED',
    TIMESTAMP WITH TIME ZONE '2026-03-20 14:00:00+05:30', 'Return ret-104 approved and pristine condition verified.',
    'Processed back to source banking rail.',
    TIMESTAMP WITH TIME ZONE '2026-03-20 14:00:00+05:30', TIMESTAMP WITH TIME ZONE '2026-03-20 14:00:00+05:30'
),
-- Refund 2 (Historical Standalone Return)
(
    'ref-800', NULL, 'ret-099', 'RRA-88940', '#RRA88940', 'pay-88940', 'Vikram Seth', 'admin@rora-luxury.com',
    'UPI / PhonePe', 'REF-UPI-440129', 3899.00, 'COMPLETED',
    TIMESTAMP WITH TIME ZONE '2026-02-12 10:15:00+05:30', 'Customer service goodwill return reimbursement.',
    'Instant UPI settlement.',
    TIMESTAMP WITH TIME ZONE '2026-02-12 10:15:00+05:30', TIMESTAMP WITH TIME ZONE '2026-02-12 10:15:00+05:30'
)
ON CONFLICT (id) DO NOTHING;
