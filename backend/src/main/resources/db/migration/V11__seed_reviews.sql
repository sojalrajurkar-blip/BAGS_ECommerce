-- ============================================================================
-- V11__seed_reviews.sql
-- RÓRA Luxury Reviews & Ratings Engine
-- ============================================================================

DROP TABLE IF EXISTS reviews CASCADE;
CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(64) PRIMARY KEY,
    product_id VARCHAR(64) REFERENCES products(id) ON DELETE CASCADE,
    product_name VARCHAR(255),
    user_id VARCHAR(64) REFERENCES users(id) ON DELETE SET NULL,
    author VARCHAR(255) NOT NULL,
    role VARCHAR(128) DEFAULT 'Verified Buyer',
    rating INT NOT NULL DEFAULT 5,
    title VARCHAR(255),
    comment TEXT NOT NULL,
    verified_purchase BOOLEAN NOT NULL DEFAULT FALSE,
    helpful_count INT NOT NULL DEFAULT 0,
    status VARCHAR(64) NOT NULL DEFAULT 'PUBLISHED',
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    moderated_by VARCHAR(255),
    moderated_at TIMESTAMP WITH TIME ZONE,
    moderation_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reviews_product_id ON reviews(product_id);
CREATE INDEX IF NOT EXISTS idx_reviews_status ON reviews(status);
CREATE INDEX IF NOT EXISTS idx_reviews_is_featured ON reviews(is_featured);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);

-- Seed Initial Verified Customer & Editorial Reviews
INSERT INTO reviews (
    id, product_id, product_name, user_id, author, role, rating, title, comment,
    verified_purchase, helpful_count, status, is_featured, created_at, updated_at
) VALUES
(
    'rev-1',
    'prod-1',
    'The Nomad Backpack',
    NULL,
    'Elena Rostova',
    'Architect & Traveler',
    5,
    'The cleanest backpack I have ever owned',
    'The Nomad backpack has accompanied me through three countries and daily site visits. The olive tone is stunning in person, and the leather trims have aged beautifully.',
    TRUE,
    24,
    'PUBLISHED',
    TRUE,
    CURRENT_TIMESTAMP - INTERVAL '14 days',
    CURRENT_TIMESTAMP - INTERVAL '14 days'
),
(
    'rev-2',
    'prod-2',
    'The Classic Leather Tote',
    NULL,
    'Marcus Vance',
    'Creative Director',
    5,
    'Exceptional craftsmanship and restraint',
    'No loud logos or gimmicks. Just incredible leather, heavy brass zippers, and well-thought-out pockets for my laptop and notebooks. Worth every rupee.',
    TRUE,
    18,
    'PUBLISHED',
    TRUE,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
),
(
    'rev-3',
    'prod-4',
    'The Urban Sling',
    NULL,
    'Sophie Lindqvist',
    'Photographer',
    5,
    'Hands-free perfection for city shoots',
    'The Urban Sling holds my mirrorless camera, spare lens, passport, and phone securely. The magnetic buckle is deeply satisfying to use.',
    TRUE,
    31,
    'PUBLISHED',
    TRUE,
    CURRENT_TIMESTAMP - INTERVAL '30 days',
    CURRENT_TIMESTAMP - INTERVAL '30 days'
)
ON CONFLICT (id) DO NOTHING;

-- Synchronize initial rating count & score on seeded products
UPDATE products SET rating = 5.0, review_count = 1 WHERE id = 'prod-1';
UPDATE products SET rating = 5.0, review_count = 1 WHERE id = 'prod-2';
UPDATE products SET rating = 5.0, review_count = 1 WHERE id = 'prod-4';
