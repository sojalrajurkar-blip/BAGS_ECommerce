-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V2
-- Seed Canonical Roles, Permissions, and Default Administrator
-- ============================================================================

-- 1. Seed Roles
INSERT INTO roles (id, name, description) VALUES
    ('role-customer', 'ROLE_CUSTOMER', 'Standard customer role with cart, wishlist, and ordering access'),
    ('role-admin', 'ROLE_ADMIN', 'Super administrator with full access to all system modules'),
    ('role-manager', 'ROLE_MANAGER', 'Store operations manager with order and inventory management'),
    ('role-product-mgr', 'ROLE_PRODUCT_MANAGER', 'Product manager with catalog, categories, and inventory authority'),
    ('role-order-mgr', 'ROLE_ORDER_MANAGER', 'Order and fulfillment specialist for shipments and returns')
ON CONFLICT (id) DO NOTHING;

-- 2. Seed Permissions
INSERT INTO permissions (id, name, description) VALUES
    ('perm-product-create', 'PRODUCT_CREATE', 'Create new product listings and variants'),
    ('perm-product-update', 'PRODUCT_UPDATE', 'Update existing product listings and pricing'),
    ('perm-product-delete', 'PRODUCT_DELETE', 'Archive or delete product listings'),
    ('perm-product-view', 'PRODUCT_VIEW', 'View internal product information'),
    ('perm-order-view', 'ORDER_VIEW', 'View all customer orders and transaction history'),
    ('perm-order-update', 'ORDER_UPDATE', 'Update order statuses and fulfillment details'),
    ('perm-inventory-manage', 'INVENTORY_MANAGE', 'Adjust inventory levels and stock movements'),
    ('perm-customer-view', 'CUSTOMER_VIEW', 'View customer details, lifetime value and addresses'),
    ('perm-cms-manage', 'CMS_MANAGE', 'Manage editorial journal, hero banner, and FAQ'),
    ('perm-settings-manage', 'SETTINGS_MANAGE', 'Update store settings and shipping thresholds'),
    ('perm-audit-view', 'AUDIT_VIEW', 'Inspect tamper-evident system audit logs')
ON CONFLICT (id) DO NOTHING;

-- 3. Link Admin Role to All Permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 'role-admin', id FROM permissions
ON CONFLICT DO NOTHING;

-- 4. Seed Default Admin User
-- Password: "Password123!" (BCrypt hashed with cost factor 12)
INSERT INTO users (id, email, password_hash, name, status, avatar_url, last_active, created_at, updated_at) VALUES
    ('user-admin-root', 'admin@rora-luxury.com', '$2a$12$Srwzky6lNG6G3o6GrOu9O.jRGLcdk/hQTMpZGNQQdxf6V2/iCFRHS', 'RÓRA Administrator', 'ACTIVE', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=400', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- 5. Assign ROLE_ADMIN to Default Admin User
INSERT INTO user_roles (user_id, role_id) VALUES
    ('user-admin-root', 'role-admin')
ON CONFLICT DO NOTHING;
