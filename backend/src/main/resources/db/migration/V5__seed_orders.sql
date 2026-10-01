-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V5
-- Target: PostgreSQL 18+
-- Description: Seed Initial Realistic Luxury Orders & Timeline Tracking Events
-- ============================================================================

-- 1. Seed Customer Record
INSERT INTO customers (
    id, user_id, first_name, last_name, email, phone, tier, total_spent, lifetime_value, orders_count, created_at, updated_at
) VALUES
('cust-admin', 'user-admin-root', 'Sarah', 'Johnson', 'admin@rora-luxury.com', '+91 98200 12345', 'VIP Patron', 15297.00, 15297.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. Seed Orders
INSERT INTO orders (
    id, order_number, user_id, customer_id, customer_name, customer_email, customer_phone,
    status, subtotal, discount_amount, shipping_fee, tax_amount, total, coupon_code,
    shipping_address, billing_address, payment_method, payment_status,
    tracking_number, carrier, estimated_delivery, created_at, updated_at
) VALUES
-- Order 1: Delivered
(
    'RRA-89241', '#RRA89241', 'user-admin-root', 'cust-admin', 'Sarah Johnson', 'admin@rora-luxury.com', '+91 98200 12345',
    'Delivered', 8798.00, 0.00, 0.00, 0.00, 8798.00, NULL,
    '{"fullName": "Sarah Johnson", "street": "142 Bandra West, Hill Road", "city": "Mumbai", "state": "MH", "postalCode": "400050", "country": "India", "phone": "+91 98200 12345"}',
    '{"fullName": "Sarah Johnson", "street": "142 Bandra West, Hill Road", "city": "Mumbai", "state": "MH", "postalCode": "400050", "country": "India", "phone": "+91 98200 12345"}',
    'UPI / Card ending in 4242', 'Captured',
    'BLU-88239014', 'Bluedart Express', 'Delivered on May 01, 2026',
    TIMESTAMP WITH TIME ZONE '2026-04-28 09:14:00+05:30', TIMESTAMP WITH TIME ZONE '2026-05-01 13:20:00+05:30'
),
-- Order 2: Shipped / Dispatched
(
    'RRA-89105', '#RRA89105', 'user-admin-root', 'cust-admin', 'Arjun Mehta', 'admin@rora-luxury.com', '+91 98211 54321',
    'Shipped', 6499.00, 0.00, 0.00, 0.00, 6499.00, NULL,
    '{"fullName": "Arjun Mehta", "street": "88 Koregaon Park, Lane 5", "city": "Pune", "state": "MH", "postalCode": "411001", "country": "India", "phone": "+91 98211 54321"}',
    '{"fullName": "Arjun Mehta", "street": "88 Koregaon Park, Lane 5", "city": "Pune", "state": "MH", "postalCode": "411001", "country": "India", "phone": "+91 98211 54321"}',
    'UPI / Google Pay', 'Captured',
    'DEL-99214012', 'Delhivery Express', 'Expected by May 04, 2026',
    TIMESTAMP WITH TIME ZONE '2026-04-14 11:20:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-15 16:10:00+05:30'
),
-- Order 3: Processing
(
    'RRA-88940', '#RRA88940', 'user-admin-root', 'cust-admin', 'Priya Sundaram', 'admin@rora-luxury.com', '+91 98333 99887',
    'Processing', 4899.00, 489.90, 0.00, 0.00, 4409.10, 'RORA10',
    '{"fullName": "Priya Sundaram", "street": "502 Indiranagar, 100ft Road", "city": "Bengaluru", "state": "KA", "postalCode": "560038", "country": "India", "phone": "+91 98333 99887"}',
    '{"fullName": "Priya Sundaram", "street": "502 Indiranagar, 100ft Road", "city": "Bengaluru", "state": "KA", "postalCode": "560038", "country": "India", "phone": "+91 98333 99887"}',
    'UPI / Google Pay', 'Captured',
    'BD-77192348', 'Bluedart Express', 'Expected in 2-4 Business Days',
    TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30', TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30'
)
ON CONFLICT (id) DO UPDATE SET
    order_number = EXCLUDED.order_number,
    customer_name = EXCLUDED.customer_name,
    customer_email = EXCLUDED.customer_email,
    status = EXCLUDED.status,
    total = EXCLUDED.total,
    updated_at = CURRENT_TIMESTAMP;

-- 2. Seed Order Items
INSERT INTO order_items (id, order_id, product_id, variant_id, product_name, color_name, image_url, unit_price, quantity, total_price, created_at)
VALUES
('item-89241-1', 'RRA-89241', 'prod-1', 'var-1-1', 'The Nomad Backpack', 'Olive Green', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=80', 4899.00, 1, 4899.00, TIMESTAMP WITH TIME ZONE '2026-04-28 09:14:00+05:30'),
('item-89241-2', 'RRA-89241', 'prod-2', 'var-2-1', 'The Classic Leather Tote', 'Cognac Brown', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=600&q=80', 3899.00, 1, 3899.00, TIMESTAMP WITH TIME ZONE '2026-04-28 09:14:00+05:30'),
('item-89105-1', 'RRA-89105', 'prod-4', 'var-4-1', 'The Weekend Duffle', 'Safari Olive', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=600&q=80', 6499.00, 1, 6499.00, TIMESTAMP WITH TIME ZONE '2026-04-14 11:20:00+05:30'),
('item-88940-1', 'RRA-88940', 'prod-1', 'var-1-2', 'The Nomad Backpack', 'Stealth Black', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=600&q=80', 4899.00, 1, 4899.00, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30')
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Order Timeline Events
INSERT INTO order_timeline_events (order_id, step_name, completed, event_time, title, description, display_order, created_at)
VALUES
-- Timeline for RRA-89241 (Delivered)
('RRA-89241', 'Order Placed', true, 'Apr 28, 2026 · 09:14 AM', 'Order Confirmed', 'Your bespoke luxury order has been confirmed and forwarded to the atelier.', 1, TIMESTAMP WITH TIME ZONE '2026-04-28 09:14:00+05:30'),
('RRA-89241', 'Payment Verified', true, 'Apr 28, 2026 · 09:15 AM', 'Payment Captured', 'Secure transaction of ₹8,798 captured via UPI.', 2, TIMESTAMP WITH TIME ZONE '2026-04-28 09:15:00+05:30'),
('RRA-89241', 'Dispatched from Hub', true, 'Apr 29, 2026 · 02:30 PM', 'Dispatched via Bluedart', 'Handcrafted packaging and white-glove inspection completed.', 3, TIMESTAMP WITH TIME ZONE '2026-04-29 14:30:00+05:30'),
('RRA-89241', 'Out for Delivery', true, 'May 01, 2026 · 08:45 AM', 'Out with Courier', 'Assigned to courier agent for morning delivery.', 4, TIMESTAMP WITH TIME ZONE '2026-05-01 08:45:00+05:30'),
('RRA-89241', 'Delivered', true, 'May 01, 2026 · 01:20 PM', 'Signature Delivery', 'Package safely handed over at customer destination.', 5, TIMESTAMP WITH TIME ZONE '2026-05-01 13:20:00+05:30'),

-- Timeline for RRA-89105 (Shipped)
('RRA-89105', 'Order Placed', true, 'Apr 14, 2026 · 11:20 AM', 'Order Confirmed', 'Your order was received and verified.', 1, TIMESTAMP WITH TIME ZONE '2026-04-14 11:20:00+05:30'),
('RRA-89105', 'Payment Verified', true, 'Apr 14, 2026 · 11:21 AM', 'Payment Captured', 'Secure payment of ₹6,499 authorized.', 2, TIMESTAMP WITH TIME ZONE '2026-04-14 11:21:00+05:30'),
('RRA-89105', 'Dispatched from Hub', true, 'Apr 15, 2026 · 04:10 PM', 'Dispatched via Delhivery', 'Package dispatched from Mumbai Central Distribution Center.', 3, TIMESTAMP WITH TIME ZONE '2026-04-15 16:10:00+05:30'),
('RRA-89105', 'Out for Delivery', false, NULL, 'Courier Handover', 'In transit to local destination hub.', 4, TIMESTAMP WITH TIME ZONE '2026-04-15 16:10:00+05:30'),
('RRA-89105', 'Delivered', false, NULL, 'Signature Handover', 'Expected delivery in 2-4 business days.', 5, TIMESTAMP WITH TIME ZONE '2026-04-15 16:10:00+05:30'),

-- Timeline for RRA-88940 (Processing)
('RRA-88940', 'Order Placed', true, 'Apr 20, 2026 · 02:30 PM', 'Order Confirmed', 'Order placed with bespoke coupon RORA10.', 1, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30'),
('RRA-88940', 'Payment Verified', true, 'Apr 20, 2026 · 02:30 PM', 'Payment Captured', 'Payment authorized and verified.', 2, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30'),
('RRA-88940', 'Dispatched from Hub', false, NULL, 'Quality Inspection & Packaging', 'Atelier artisans preparing packaging and authenticity cards.', 3, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30'),
('RRA-88940', 'Out for Delivery', false, NULL, 'Courier Handover', 'Pending fulfillment.', 4, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30'),
('RRA-88940', 'Delivered', false, NULL, 'Signature Handover', 'Pending fulfillment.', 5, TIMESTAMP WITH TIME ZONE '2026-04-20 14:30:00+05:30');
