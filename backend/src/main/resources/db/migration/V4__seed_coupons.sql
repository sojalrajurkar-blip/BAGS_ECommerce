-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V4
-- Target: PostgreSQL 18+
-- Description: Seed Promotional Discount Coupons
-- ============================================================================

INSERT INTO coupons (
    id, code, description, discount_type, discount_value, discount_percent,
    minimum_spend, max_discount_amount, usage_limit, usage_count, per_user_limit,
    is_active, start_date, expiry_date, created_at, updated_at
) VALUES
(
    'coup-rora10', 'RORA10', '10% off your entire order', 'PERCENTAGE', NULL, 10,
    1999.00, 2000.00, 1000, 142, 2,
    TRUE, '2026-01-01 00:00:00+00', '2026-12-31 23:59:59+00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'coup-welcome15', 'WELCOME15', '15% off first purchase for new members', 'PERCENTAGE', NULL, 15,
    2999.00, 2500.00, 5000, 389, 1,
    TRUE, '2026-01-01 00:00:00+00', '2026-11-30 23:59:59+00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'coup-journey20', 'JOURNEY20', '20% off travel collection', 'PERCENTAGE', NULL, 20,
    4999.00, 3000.00, 500, 86, 1,
    TRUE, '2026-01-01 00:00:00+00', '2026-10-15 23:59:59+00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'coup-architect25', 'ARCHITECT25', '25% VIP architectural event code', 'PERCENTAGE', NULL, 25,
    7999.00, 5000.00, 100, 24, 1,
    TRUE, '2026-01-01 00:00:00+00', '2026-12-31 23:59:59+00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
ON CONFLICT (code) DO UPDATE SET
    description = EXCLUDED.description,
    discount_type = EXCLUDED.discount_type,
    discount_percent = EXCLUDED.discount_percent,
    minimum_spend = EXCLUDED.minimum_spend,
    max_discount_amount = EXCLUDED.max_discount_amount,
    usage_limit = EXCLUDED.usage_limit,
    usage_count = EXCLUDED.usage_count,
    is_active = EXCLUDED.is_active,
    expiry_date = EXCLUDED.expiry_date,
    updated_at = CURRENT_TIMESTAMP;
