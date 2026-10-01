-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V6
-- Target: PostgreSQL 18+
-- Description: Seed Full Customer 360 Profiles & Saved Atelier Addresses
-- ============================================================================

-- 1. Seed Customers
INSERT INTO customers (
    id, user_id, first_name, last_name, email, phone, tier, total_spent, lifetime_value, orders_count, created_at, updated_at
) VALUES
('cust-1', NULL, 'Sarah', 'Johnson', 'sarah.j@example.com', '+91 98201 44521', 'VIP', 28450.00, 28450.00, 5, TIMESTAMP WITH TIME ZONE '2025-01-15 10:00:00+05:30', CURRENT_TIMESTAMP),
('cust-2', NULL, 'Arjun', 'Mehta', 'arjun.mehta@designstudio.in', '+91 98450 11234', 'VIP', 16890.00, 16890.00, 3, TIMESTAMP WITH TIME ZONE '2025-03-10 11:30:00+05:30', CURRENT_TIMESTAMP),
('cust-3', NULL, 'Priya', 'Sundaram', 'priya.s@techventures.co', '+91 97110 88921', 'Regular', 9798.00, 9798.00, 2, TIMESTAMP WITH TIME ZONE '2025-07-22 14:15:00+05:30', CURRENT_TIMESTAMP),
('cust-4', NULL, 'Rohan', 'Kapoor', 'rohan.k@kapoorarchitects.com', '+91 99882 33410', 'New', 6499.00, 6499.00, 1, TIMESTAMP WITH TIME ZONE '2026-04-02 09:45:00+05:30', CURRENT_TIMESTAMP),
('cust-5', NULL, 'Ananya', 'Deshmukh', 'ananya.d@deshmukhlaw.com', '+91 98190 66733', 'VIP', 21400.00, 21400.00, 4, TIMESTAMP WITH TIME ZONE '2025-02-18 16:20:00+05:30', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    email = EXCLUDED.email,
    phone = EXCLUDED.phone,
    tier = EXCLUDED.tier,
    total_spent = EXCLUDED.total_spent,
    lifetime_value = EXCLUDED.lifetime_value,
    orders_count = EXCLUDED.orders_count,
    updated_at = CURRENT_TIMESTAMP;

-- 2. Seed Customer Addresses
INSERT INTO addresses (
    id, customer_id, full_name, street, address_line2, city, state, postal_code, country, phone, is_default, created_at, updated_at
) VALUES
('addr-1-1', 'cust-1', 'Sarah Johnson', '142 Bandra West, Hill Road', 'Apt 4B, Ocean View Towers', 'Mumbai', 'Maharashtra', '400050', 'India', '+91 98201 44521', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('addr-1-2', 'cust-1', 'Sarah Johnson', 'Studio 18, Worli Sea Face', 'Floor 6, Design District', 'Mumbai', 'Maharashtra', '400018', 'India', '+91 98201 44521', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('addr-2-1', 'cust-2', 'Arjun Mehta', '88 Koregaon Park, Lane 5', 'Villa Serenade', 'Pune', 'Maharashtra', '411001', 'India', '+91 98450 11234', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('addr-3-1', 'cust-3', 'Priya Sundaram', '502 Indiranagar, 100ft Road', 'Skyline Residency', 'Bengaluru', 'Karnataka', '560038', 'India', '+91 97110 88921', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('addr-4-1', 'cust-4', 'Rohan Kapoor', 'Plot 42, Jubilee Hills, Road No. 36', 'Architects Haven', 'Hyderabad', 'Telangana', '500033', 'India', '+91 99882 33410', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('addr-5-1', 'cust-5', 'Ananya Deshmukh', '12 Senapati Bapat Road', 'Deshmukh Chambers', 'Pune', 'Maharashtra', '411016', 'India', '+91 98190 66733', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    street = EXCLUDED.street,
    city = EXCLUDED.city,
    state = EXCLUDED.state,
    postal_code = EXCLUDED.postal_code,
    is_default = EXCLUDED.is_default,
    updated_at = CURRENT_TIMESTAMP;
