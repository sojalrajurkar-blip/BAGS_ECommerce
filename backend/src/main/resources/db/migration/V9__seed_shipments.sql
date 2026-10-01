-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V9
-- Target: PostgreSQL 18+
-- Description: Shipments, Consignment Tracking & Historical Seed Events
-- ============================================================================

-- 1. Create Shipments Table
CREATE TABLE IF NOT EXISTS shipments (
    id VARCHAR(64) PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    order_number VARCHAR(64) NOT NULL,
    customer_id VARCHAR(64) REFERENCES customers(id) ON DELETE SET NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(32),
    courier VARCHAR(128) NOT NULL,
    awb_number VARCHAR(128) NOT NULL UNIQUE,
    origin VARCHAR(255) NOT NULL DEFAULT 'Mumbai Central Studio',
    destination VARCHAR(255) NOT NULL,
    status VARCHAR(64) NOT NULL DEFAULT 'CREATED',
    dispatch_date TIMESTAMP WITH TIME ZONE,
    estimated_delivery VARCHAR(128),
    actual_delivery_date TIMESTAMP WITH TIME ZONE,
    tracking_url VARCHAR(512),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Shipment Events Table
CREATE TABLE IF NOT EXISTS shipment_events (
    id BIGSERIAL PRIMARY KEY,
    shipment_id VARCHAR(64) NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    status VARCHAR(64) NOT NULL DEFAULT 'CREATED',
    location VARCHAR(255),
    activity VARCHAR(512) NOT NULL,
    event_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT
);

-- 3. Create Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_shipments_order_id ON shipments(order_id);
CREATE INDEX IF NOT EXISTS idx_shipments_order_number ON shipments(order_number);
CREATE INDEX IF NOT EXISTS idx_shipments_awb_number ON shipments(awb_number);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_customer_email ON shipments(customer_email);
CREATE INDEX IF NOT EXISTS idx_shipment_events_shipment_id ON shipment_events(shipment_id);

-- 4. Seed Historical Luxury Shipments
INSERT INTO shipments (
    id, order_id, order_number, customer_id, customer_name, customer_email, customer_phone,
    courier, awb_number, origin, destination, status, dispatch_date, estimated_delivery,
    actual_delivery_date, tracking_url, notes, created_at, updated_at
) VALUES
-- Shipment 1: Delivered
(
    'ship-441', 'RRA-89241', '#RRA89241', 'cust-admin', 'Sarah Johnson', 'admin@rora-luxury.com', '+91 98200 12345',
    'Bluedart Express', 'BLU-88239014', 'Mumbai Central Studio', 'Bandra West, Mumbai', 'DELIVERED',
    TIMESTAMP WITH TIME ZONE '2026-04-29 10:30:00+05:30', 'May 01, 2026',
    TIMESTAMP WITH TIME ZONE '2026-05-01 13:20:00+05:30',
    'https://track.rora-luxury.com/shipment/BLU-88239014', 'Delivered securely with white-glove packaging and signature verification.',
    TIMESTAMP WITH TIME ZONE '2026-04-29 10:30:00+05:30', TIMESTAMP WITH TIME ZONE '2026-05-01 13:20:00+05:30'
),
-- Shipment 2: In Transit
(
    'ship-440', 'RRA-89105', '#RRA89105', 'cust-admin', 'Arjun Mehta', 'admin@rora-luxury.com', '+91 98211 54321',
    'Delhivery Surface', 'DEL-99120481', 'Mumbai Central Studio', 'Indiranagar, Bengaluru', 'IN_TRANSIT',
    TIMESTAMP WITH TIME ZONE '2026-04-15 14:00:00+05:30', 'Expected by May 04, 2026',
    NULL,
    'https://track.rora-luxury.com/shipment/DEL-99120481', 'Dispatched from central fulfillment center via surface express.',
    TIMESTAMP WITH TIME ZONE '2026-04-15 14:00:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-15 14:00:00+05:30'
),
-- Shipment 3: Delivered
(
    'ship-439', 'RRA-88940', '#RRA88940', 'cust-admin', 'Priya Sundaram', 'admin@rora-luxury.com', '+91 98333 99887',
    'DTDC Priority Air', 'DTD-10924822', 'Mumbai Central Studio', 'Vasant Vihar, New Delhi', 'DELIVERED',
    TIMESTAMP WITH TIME ZONE '2026-03-31 09:00:00+05:30', 'Apr 02, 2026',
    TIMESTAMP WITH TIME ZONE '2026-04-02 11:45:00+05:30',
    'https://track.rora-luxury.com/shipment/DTD-10924822', 'Handed over directly to recipient.',
    TIMESTAMP WITH TIME ZONE '2026-03-31 09:00:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-02 11:45:00+05:30'
)
ON CONFLICT (id) DO UPDATE SET
    status = EXCLUDED.status,
    actual_delivery_date = EXCLUDED.actual_delivery_date,
    updated_at = CURRENT_TIMESTAMP;

-- 5. Seed Shipment Tracking Events
INSERT INTO shipment_events (shipment_id, status, location, activity, event_timestamp, notes) VALUES
-- Events for ship-441
('ship-441', 'MANIFESTED', 'Mumbai Central Studio', 'Manifest generated and tamper-evident security seal applied.', TIMESTAMP WITH TIME ZONE '2026-04-29 10:30:00+05:30', NULL),
('ship-441', 'PICKED_UP', 'Mumbai Central Studio', 'Package picked up by Bluedart courier agent.', TIMESTAMP WITH TIME ZONE '2026-04-29 16:45:00+05:30', NULL),
('ship-441', 'IN_TRANSIT', 'Mumbai Sorting Hub - Kurla', 'Processed through primary hub scanning.', TIMESTAMP WITH TIME ZONE '2026-04-30 04:15:00+05:30', NULL),
('ship-441', 'OUT_FOR_DELIVERY', 'Bandra Delivery Center', 'Out for delivery with delivery executive Rajesh K.', TIMESTAMP WITH TIME ZONE '2026-05-01 09:10:00+05:30', NULL),
('ship-441', 'DELIVERED', 'Bandra West, Mumbai', 'Delivered to Sarah Johnson. Signature captured.', TIMESTAMP WITH TIME ZONE '2026-05-01 13:20:00+05:30', 'Signature: Verified'),

-- Events for ship-440
('ship-440', 'MANIFESTED', 'Mumbai Central Studio', 'Manifest generated and packaging verified.', TIMESTAMP WITH TIME ZONE '2026-04-15 14:00:00+05:30', NULL),
('ship-440', 'PICKED_UP', 'Mumbai Central Studio', 'Package collected by Delhivery logistics agent.', TIMESTAMP WITH TIME ZONE '2026-04-15 18:20:00+05:30', NULL),
('ship-440', 'IN_TRANSIT', 'Pune Transit Hub', 'In transit to Bengaluru main distribution center.', TIMESTAMP WITH TIME ZONE '2026-04-16 08:30:00+05:30', NULL),

-- Events for ship-439
('ship-439', 'MANIFESTED', 'Mumbai Central Studio', 'Manifest created and premium dust bag secured.', TIMESTAMP WITH TIME ZONE '2026-03-31 09:00:00+05:30', NULL),
('ship-439', 'PICKED_UP', 'Mumbai Central Studio', 'Handed over to DTDC Priority Air team.', TIMESTAMP WITH TIME ZONE '2026-03-31 12:00:00+05:30', NULL),
('ship-439', 'IN_TRANSIT', 'Delhi Cargo Terminal (IGI T3)', 'Arrived at destination airport hub.', TIMESTAMP WITH TIME ZONE '2026-04-01 06:10:00+05:30', NULL),
('ship-439', 'OUT_FOR_DELIVERY', 'South Delhi Delivery Hub', 'Dispatched for doorstep delivery.', TIMESTAMP WITH TIME ZONE '2026-04-02 08:30:00+05:30', NULL),
('ship-439', 'DELIVERED', 'Vasant Vihar, New Delhi', 'Delivered to Priya Sundaram.', TIMESTAMP WITH TIME ZONE '2026-04-02 11:45:00+05:30', NULL);
