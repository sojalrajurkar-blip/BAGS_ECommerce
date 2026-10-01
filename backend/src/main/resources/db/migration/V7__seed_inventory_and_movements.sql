-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V7
-- Target: PostgreSQL 18+
-- Description: Inventory Management, Warehousing & Stock Movement Ledger
-- ============================================================================

-- 1. Enhance inventory table schema with warehousing metadata
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS warehouse_location VARCHAR(128) DEFAULT 'Main Atelier Vault, Mumbai';
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS bin_location VARCHAR(64) DEFAULT 'A-01-01';

-- 2. Enhance inventory_movements table with audit references
ALTER TABLE inventory_movements ADD COLUMN IF NOT EXISTS reference_id VARCHAR(128);
ALTER TABLE inventory_movements ADD COLUMN IF NOT EXISTS batch_number VARCHAR(128);

-- 3. Seed Master Inventory Records for Variants
INSERT INTO inventory (id, product_id, variant_id, sku, quantity_available, quantity_reserved, low_stock_threshold, warehouse_location, bin_location, created_at, updated_at)
VALUES
-- prod-1 variants (The Nomad Backpack)
('inv-1-1', 'prod-1', 'var-1-1', 'RRA-NMD-01-OLV', 10, 0, 4, 'Main Atelier Vault, Mumbai', 'A-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-1-2', 'prod-1', 'var-1-2', 'RRA-NMD-01-BLK', 6, 0, 4, 'Main Atelier Vault, Mumbai', 'A-01-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-1-3', 'prod-1', 'var-1-3', 'RRA-NMD-01-TAU', 5, 0, 4, 'Main Atelier Vault, Mumbai', 'A-01-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-1-4', 'prod-1', 'var-1-4', 'RRA-NMD-01-BRN', 3, 0, 4, 'Main Atelier Vault, Mumbai', 'A-01-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-2 variants (The Classic Leather Tote)
('inv-2-1', 'prod-2', 'var-2-1', 'RRA-TOT-02-COG', 8, 0, 4, 'Main Atelier Vault, Mumbai', 'B-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-2-2', 'prod-2', 'var-2-2', 'RRA-TOT-02-NOI', 6, 0, 4, 'Main Atelier Vault, Mumbai', 'B-02-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-2-3', 'prod-2', 'var-2-3', 'RRA-TOT-02-SND', 4, 0, 4, 'Main Atelier Vault, Mumbai', 'B-02-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-3 variants (The Urban Sling)
('inv-3-1', 'prod-3', 'var-3-1', 'RRA-SLG-03-BLK', 16, 0, 5, 'Main Atelier Vault, Mumbai', 'C-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-3-2', 'prod-3', 'var-3-2', 'RRA-SLG-03-OLV', 10, 0, 5, 'Main Atelier Vault, Mumbai', 'C-01-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-3-3', 'prod-3', 'var-3-3', 'RRA-SLG-03-GRY', 6, 0, 5, 'Main Atelier Vault, Mumbai', 'C-01-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-4 variants (The Weekend Duffle)
('inv-4-1', 'prod-4', 'var-4-1', 'RRA-DUF-04-SAF', 5, 0, 3, 'South Logistics Hub, Bengaluru', 'D-03-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-4-2', 'prod-4', 'var-4-2', 'RRA-DUF-04-BRN', 4, 0, 3, 'South Logistics Hub, Bengaluru', 'D-03-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-4-3', 'prod-4', 'var-4-3', 'RRA-DUF-04-GRY', 3, 0, 3, 'South Logistics Hub, Bengaluru', 'D-03-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-5 variants (The Executive Laptop Brief)
('inv-5-1', 'prod-5', 'var-5-1', 'RRA-LAP-05-CHR', 8, 0, 4, 'Main Atelier Vault, Mumbai', 'A-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-5-2', 'prod-5', 'var-5-2', 'RRA-LAP-05-ESP', 7, 0, 4, 'Main Atelier Vault, Mumbai', 'A-02-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-6 variants (The Minimal Studio Tote)
('inv-6-1', 'prod-6', 'var-6-1', 'RRA-STD-06-SND', 10, 0, 4, 'North Distribution Center, Delhi', 'E-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-6-2', 'prod-6', 'var-6-2', 'RRA-STD-06-OLV', 7, 0, 4, 'North Distribution Center, Delhi', 'E-01-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-6-3', 'prod-6', 'var-6-3', 'RRA-STD-06-BLK', 5, 0, 4, 'North Distribution Center, Delhi', 'E-01-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-7 variants (The Campus Explorer)
('inv-7-1', 'prod-7', 'var-7-1', 'RRA-CMP-07-OLV', 12, 0, 5, 'North Distribution Center, Delhi', 'E-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-7-2', 'prod-7', 'var-7-2', 'RRA-CMP-07-TAU', 9, 0, 5, 'North Distribution Center, Delhi', 'E-02-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-7-3', 'prod-7', 'var-7-3', 'RRA-CMP-07-BRN', 7, 0, 5, 'North Distribution Center, Delhi', 'E-02-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-8 variants (The City Crossbody Handbag)
('inv-8-1', 'prod-8', 'var-8-1', 'RRA-HND-08-COG', 8, 0, 4, 'Main Atelier Vault, Mumbai', 'B-03-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-8-2', 'prod-8', 'var-8-2', 'RRA-HND-08-NOI', 6, 0, 4, 'Main Atelier Vault, Mumbai', 'B-03-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-9 variants (The Artisan Atelier Handbag)
('inv-9-1', 'prod-9', 'var-9-1', 'RRA-HND-09-TER', 5, 0, 3, 'Main Atelier Vault, Mumbai', 'B-04-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-9-2', 'prod-9', 'var-9-2', 'RRA-HND-09-BLK', 5, 0, 3, 'Main Atelier Vault, Mumbai', 'B-04-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-10 variants (The Voyager Tech Pack)
('inv-10-1', 'prod-10', 'var-10-1', 'RRA-VOY-10-BLK', 10, 0, 4, 'South Logistics Hub, Bengaluru', 'D-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-10-2', 'prod-10', 'var-10-2', 'RRA-VOY-10-OLV', 10, 0, 4, 'South Logistics Hub, Bengaluru', 'D-01-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-11 variants (The Heritage Portfolio Brief)
('inv-11-1', 'prod-11', 'var-11-1', 'RRA-FOL-11-NOI', 8, 0, 4, 'Main Atelier Vault, Mumbai', 'A-03-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-11-2', 'prod-11', 'var-11-2', 'RRA-FOL-11-BRN', 8, 0, 4, 'Main Atelier Vault, Mumbai', 'A-03-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-12 variants (The Gateway Adventure Duffel)
('inv-12-1', 'prod-12', 'var-12-1', 'RRA-GTE-12-BLK', 9, 0, 4, 'South Logistics Hub, Bengaluru', 'D-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('inv-12-2', 'prod-12', 'var-12-2', 'RRA-GTE-12-OLV', 6, 0, 4, 'South Logistics Hub, Bengaluru', 'D-02-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    quantity_available = EXCLUDED.quantity_available,
    quantity_reserved = EXCLUDED.quantity_reserved,
    low_stock_threshold = EXCLUDED.low_stock_threshold,
    warehouse_location = EXCLUDED.warehouse_location,
    bin_location = EXCLUDED.bin_location,
    updated_at = CURRENT_TIMESTAMP;

-- 4. Seed Initial Stock Movement Ledger Entries
INSERT INTO inventory_movements (inventory_id, sku, movement_type, quantity_change, previous_quantity, new_quantity, reason, created_by, reference_id, batch_number, created_at)
VALUES
('inv-1-1', 'RRA-NMD-01-OLV', 'RESTOCK', 10, 0, 10, 'Initial seasonal atelier production intake', 'inventory.lead@rora-luxury.com', 'PO-2026-NMD-01', 'BATCH-2026-Q1-01', CURRENT_TIMESTAMP),
('inv-1-2', 'RRA-NMD-01-BLK', 'RESTOCK', 6, 0, 6, 'Initial seasonal atelier production intake', 'inventory.lead@rora-luxury.com', 'PO-2026-NMD-01', 'BATCH-2026-Q1-01', CURRENT_TIMESTAMP),
('inv-1-3', 'RRA-NMD-01-TAU', 'RESTOCK', 5, 0, 5, 'Initial seasonal atelier production intake', 'inventory.lead@rora-luxury.com', 'PO-2026-NMD-01', 'BATCH-2026-Q1-01', CURRENT_TIMESTAMP),
('inv-1-4', 'RRA-NMD-01-BRN', 'RESTOCK', 3, 0, 3, 'Initial seasonal atelier production intake', 'inventory.lead@rora-luxury.com', 'PO-2026-NMD-01', 'BATCH-2026-Q1-01', CURRENT_TIMESTAMP),
('inv-2-1', 'RRA-TOT-02-COG', 'RESTOCK', 8, 0, 8, 'Florence tannery dispatch', 'inventory.lead@rora-luxury.com', 'PO-2026-TOT-02', 'BATCH-2026-Q1-02', CURRENT_TIMESTAMP),
('inv-2-2', 'RRA-TOT-02-NOI', 'RESTOCK', 6, 0, 6, 'Florence tannery dispatch', 'inventory.lead@rora-luxury.com', 'PO-2026-TOT-02', 'BATCH-2026-Q1-02', CURRENT_TIMESTAMP),
('inv-2-3', 'RRA-TOT-02-SND', 'RESTOCK', 4, 0, 4, 'Florence tannery dispatch', 'inventory.lead@rora-luxury.com', 'PO-2026-TOT-02', 'BATCH-2026-Q1-02', CURRENT_TIMESTAMP),
('inv-3-1', 'RRA-SLG-03-BLK', 'RESTOCK', 16, 0, 16, 'High velocity restock run', 'inventory.lead@rora-luxury.com', 'PO-2026-SLG-03', 'BATCH-2026-Q1-03', CURRENT_TIMESTAMP),
('inv-3-2', 'RRA-SLG-03-OLV', 'RESTOCK', 10, 0, 10, 'High velocity restock run', 'inventory.lead@rora-luxury.com', 'PO-2026-SLG-03', 'BATCH-2026-Q1-03', CURRENT_TIMESTAMP),
('inv-3-3', 'RRA-SLG-03-GRY', 'RESTOCK', 6, 0, 6, 'High velocity restock run', 'inventory.lead@rora-luxury.com', 'PO-2026-SLG-03', 'BATCH-2026-Q1-03', CURRENT_TIMESTAMP),
('inv-4-1', 'RRA-DUF-04-SAF', 'RESTOCK', 5, 0, 5, 'Waxed canvas atelier consignment', 'inventory.lead@rora-luxury.com', 'PO-2026-DUF-04', 'BATCH-2026-Q1-04', CURRENT_TIMESTAMP),
('inv-4-2', 'RRA-DUF-04-BRN', 'RESTOCK', 4, 0, 4, 'Waxed canvas atelier consignment', 'inventory.lead@rora-luxury.com', 'PO-2026-DUF-04', 'BATCH-2026-Q1-04', CURRENT_TIMESTAMP),
('inv-4-3', 'RRA-DUF-04-GRY', 'RESTOCK', 3, 0, 3, 'Waxed canvas atelier consignment', 'inventory.lead@rora-luxury.com', 'PO-2026-DUF-04', 'BATCH-2026-Q1-04', CURRENT_TIMESTAMP),
('inv-5-1', 'RRA-LAP-05-CHR', 'RESTOCK', 8, 0, 8, 'Executive series craftsmanship batch', 'inventory.lead@rora-luxury.com', 'PO-2026-LAP-05', 'BATCH-2026-Q1-05', CURRENT_TIMESTAMP),
('inv-5-2', 'RRA-LAP-05-ESP', 'RESTOCK', 7, 0, 7, 'Executive series craftsmanship batch', 'inventory.lead@rora-luxury.com', 'PO-2026-LAP-05', 'BATCH-2026-Q1-05', CURRENT_TIMESTAMP);
