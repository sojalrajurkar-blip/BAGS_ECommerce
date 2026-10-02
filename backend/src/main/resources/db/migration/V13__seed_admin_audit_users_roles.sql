-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V13
-- Seed Admin Team Users, Canonical Roles Linkages & Master Audit Logs
-- ============================================================================

-- 1. Seed Backoffice Admin Team Users
-- Password: "Password123!" (BCrypt cost factor 12)
INSERT INTO users (id, email, password_hash, name, status, avatar_url, last_active, created_at, updated_at) VALUES
    (
        'usr-1',
        'sarah.j@rorastudios.com',
        '$2a$12$Srwzky6lNG6G3o6GrOu9O.jRGLcdk/hQTMpZGNQQdxf6V2/iCFRHS',
        'Sarah Jenkins',
        'ACTIVE',
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=400',
        CURRENT_TIMESTAMP - INTERVAL '12 minutes',
        CURRENT_TIMESTAMP - INTERVAL '30 days',
        CURRENT_TIMESTAMP
    ),
    (
        'usr-2',
        'kabir.v@rorastudios.com',
        '$2a$12$Srwzky6lNG6G3o6GrOu9O.jRGLcdk/hQTMpZGNQQdxf6V2/iCFRHS',
        'Kabir Verma',
        'ACTIVE',
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=400',
        CURRENT_TIMESTAMP - INTERVAL '3 hours',
        CURRENT_TIMESTAMP - INTERVAL '25 days',
        CURRENT_TIMESTAMP
    ),
    (
        'usr-3',
        'meera.r@rorastudios.com',
        '$2a$12$Srwzky6lNG6G3o6GrOu9O.jRGLcdk/hQTMpZGNQQdxf6V2/iCFRHS',
        'Meera Rao',
        'ACTIVE',
        'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=400',
        CURRENT_TIMESTAMP - INTERVAL '1 hour',
        CURRENT_TIMESTAMP - INTERVAL '20 days',
        CURRENT_TIMESTAMP
    ),
    (
        'usr-4',
        'david.c@rorastudios.com',
        '$2a$12$Srwzky6lNG6G3o6GrOu9O.jRGLcdk/hQTMpZGNQQdxf6V2/iCFRHS',
        'David Chen',
        'ACTIVE',
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=400',
        CURRENT_TIMESTAMP - INTERVAL '1 day',
        CURRENT_TIMESTAMP - INTERVAL '15 days',
        CURRENT_TIMESTAMP
    )
ON CONFLICT (email) DO NOTHING;

-- 2. Link Roles to Admin Team Users
INSERT INTO user_roles (user_id, role_id) VALUES
    ('usr-1', 'role-admin'),
    ('usr-2', 'role-manager'),
    ('usr-3', 'role-order-mgr'),
    ('usr-4', 'role-product-mgr')
ON CONFLICT DO NOTHING;

-- 3. Seed Master Audit Logs
INSERT INTO audit_logs (action, actor, target, entity_type, ip_address, status, severity, details, created_at) VALUES
    (
        'Product Price Updated',
        'Sarah Jenkins (Super Admin)',
        'The Nomad Backpack (₹4,899)',
        'Product',
        '192.168.1.101',
        'SUCCESS',
        'Info',
        '{"productId": "prod-1", "oldPrice": 5299, "newPrice": 4899}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '12 minutes'
    ),
    (
        'Order Dispatched',
        'Logistics Service',
        'Order #RRA89241 via Bluedart',
        'Order',
        '10.0.4.12',
        'SUCCESS',
        'Success',
        '{"orderNumber": "#RRA89241", "awb": "BLU-88239014", "carrier": "Bluedart Express"}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '1 hour'
    ),
    (
        'Coupon Code Created',
        'Kabir Verma (Store Manager)',
        'JOURNEY20 (20% Off)',
        'Coupon',
        '192.168.1.104',
        'SUCCESS',
        'Info',
        '{"couponCode": "JOURNEY20", "discount": 20, "type": "PERCENTAGE"}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '3 hours'
    ),
    (
        'Inventory Restocked',
        'Warehouse Manager',
        '+50 The Classic Leather Tote',
        'Inventory',
        '192.168.2.15',
        'SUCCESS',
        'Success',
        '{"sku": "RRA-TOT-02", "quantity": 50, "warehouse": "Mumbai Hub"}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '1 day'
    ),
    (
        'Return Approved',
        'Meera Rao (Support Lead)',
        'Return #RET-104 (₹5,499)',
        'Return',
        '192.168.1.108',
        'SUCCESS',
        'Warning',
        '{"returnId": "ret-test-104", "inspectionGrade": "PASSED_PRISTINE", "refundAmount": 5499.0}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '2 days'
    ),
    (
        'Review Published',
        'David Chen (Content Editor)',
        '5★ Review by Elena Rostova',
        'Review',
        '192.168.1.112',
        'SUCCESS',
        'Info',
        '{"reviewId": "rev-1", "author": "Elena Rostova", "rating": 5}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '3 days'
    ),
    (
        'Store Setting Changed',
        'Sarah Jenkins (Super Admin)',
        'Free Shipping Threshold set to ₹1,999',
        'Setting',
        '192.168.1.101',
        'SUCCESS',
        'Warning',
        '{"key": "freeShippingThreshold", "value": 1999}'::jsonb,
        CURRENT_TIMESTAMP - INTERVAL '5 days'
    );
