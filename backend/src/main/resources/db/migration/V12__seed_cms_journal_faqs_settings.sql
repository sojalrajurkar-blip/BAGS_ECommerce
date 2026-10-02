-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V12
-- Seed CMS Homepage Banners, Editorial Journal Articles, FAQs & Store Settings
-- ============================================================================

-- 1. Create FAQs Table (if not exists)
CREATE TABLE IF NOT EXISTS faqs (
    id VARCHAR(64) PRIMARY KEY,
    category VARCHAR(128) NOT NULL,
    question VARCHAR(500) NOT NULL,
    answer TEXT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_faqs_category ON faqs(category);
CREATE INDEX IF NOT EXISTS idx_faqs_active ON faqs(is_active);

-- 2. Seed Homepage Editorial CMS Content
INSERT INTO cms_content (id, content_key, title, content_data, updated_at) VALUES
(
    'cms-homepage-default',
    'homepage',
    'Homepage Editorial Content',
    '{
        "announcementBar": {
            "enabled": true,
            "text": "Complimentary shipping across India on orders over ₹1,999 • Handcrafted with certified recycled textiles",
            "link": "/categories/all"
        },
        "heroBanner": {
            "eyebrow": "Architectural Carry • Autumn 2026",
            "title": "Engineered for the Modern Journey",
            "subtitle": "Tactile Japanese recycled nylon and full-grain Italian leather, crafted in small artisanal batches with zero compromise.",
            "primaryButtonText": "Explore Collection",
            "secondaryButtonText": "Read the Journal",
            "primaryButtonLink": "/categories/all",
            "secondaryButtonLink": "/journal",
            "backgroundImage": "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=2000&q=85"
        },
        "craftsmanshipFeature": {
            "heading": "Architectural Restraint Meets Uncompromising Craft",
            "paragraph1": "Every RÓRA silhouette begins as a mathematical exercise in volume, balance, and tactile reduction. We eliminate unnecessary ornamentation to let premium materials and ergonomic geometry shine.",
            "paragraph2": "Hand-burnished leather edges, custom anodized matte hardware, and weatherproof stormproof zippers ensure a lifetime of faithful companion carry.",
            "image": "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80"
        }
    }'::jsonb,
    CURRENT_TIMESTAMP
)
ON CONFLICT (content_key) DO UPDATE SET
    title = EXCLUDED.title,
    content_data = EXCLUDED.content_data,
    updated_at = EXCLUDED.updated_at;

-- 3. Seed Editorial Journal Articles
INSERT INTO journal_articles (
    id, slug, title, subtitle, category, read_time, author, image, excerpt, content, tags, published_at, created_at, updated_at
) VALUES
(
    'art-1',
    '5-must-have-features-in-a-travel-bag',
    '5 Must-Have Features in an Intentional Travel Bag',
    'Geometry, materials, and weight distribution that redefine transit.',
    'Travel & Mobility',
    '5 min read',
    'Søren Lindqvist',
    'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80',
    'Navigating international transit requires deliberate geometry: why water-resistance, hidden security pockets, and balanced weight distribution matter most.',
    'Traveling with a single, well-crafted bag changes your entire relationship with journeys. Instead of fighting oversized rolling luggage on European cobblestones or crowded subway platforms, an ergonomically balanced bag keeps your hands free and your mind focused on the horizon.

### 1. Dual-Access Architecture
When you are in security lines, digging through layers of clothing to retrieve a laptop is the fastest way to cause friction. A dedicated perimeter zipper allows instant access to electronics without exposing personal items.

### 2. High-Denier Weather Repellency
Sudden rain showers in Tokyo or morning mist in the Scottish Highlands shouldn''t threaten your sketchbook or camera gear. Dense weaves treated with hydrophobic coatings create natural bead-and-roll defense without toxic fluorochemicals.

### 3. Concealed Security Geography
Placing high-value documents—passports, boarding passes, currency—against your lumbar back eliminates opportunist theft in crowded market squares.',
    '["Travel", "Design", "Carry-On", "Architecture"]'::jsonb,
    'April 25, 2026',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'art-2',
    'the-art-of-minimal-packing',
    'The Art of Minimal Packing: Traveling Lighter for Longer',
    'Why packing less is the ultimate luxury on the road.',
    'Philosophy & Lifestyle',
    '4 min read',
    'Elena Rostova',
    'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80',
    'Why packing less is the ultimate luxury on the road, and how modular organization creates boundless freedom.',
    'True luxury is unencumbered movement. When you carry only what is essential, you eliminate decision fatigue and create room for spontaneous detours.

Start by auditing every item: if it does not serve at least two distinct purposes, it stays behind. Natural fibers like merino wool and linen breathe effortlessly and resist odor across multiple days, halving your wardrobe requirements.

Combine modular compression cubes with intentional bag volume to create an airtight capsule setup ready for 14 days of travel in a single 28L silhouette.',
    '["Minimalism", "Packing", "Lifestyle"]'::jsonb,
    'April 18, 2026',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'art-3',
    'sustainable-materials-in-modern-bags',
    'Sustainable Materials: The Evolution of Recycled Cordura & Leather',
    'Circular manufacturing in high-end carry gear.',
    'Craftsmanship & Materials',
    '6 min read',
    'Marcus Vance',
    'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80',
    'How circular manufacturing, vegetable tannins, and ocean-bound plastics are redefining high-end luggage standards.',
    'For generations, the luxury industry treated synthetic nylon and virgin animal hides as disposable markers of status. Today, engineering advancements allow us to spin discarded fishing nets into 900D fabrics that outperform virgin nylon in tensile tear tests.

Combined with certified vegetable-tanned leathers utilizing mimosa and chestnut extracts, modern bags can age with grace while leaving minimal environmental debt.',
    '["Sustainability", "Italian Leather", "Recycled Nylon", "Craft"]'::jsonb,
    'April 12, 2026',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'art-4',
    'best-bags-for-your-next-weekend-getaway',
    'Curating the Perfect Weekend Carry: Form Meets Function',
    'A comparative study between structured duffles and expandable backpacks.',
    'Product Stories',
    '5 min read',
    'RÓRA Editorial',
    'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80',
    'A comparative guide between the structured duffle and the expandable travel backpack for 72-hour escapes.',
    'Whether taking an evening express train to the coast or driving up into the pines for three days of quiet, your carry piece dictates your rhythm. We compare the tactile joy of waxed canvas against high-performance technical ripstop.',
    '["Weekend", "Travel Bags", "Duffle", "Backpack"]'::jsonb,
    'April 3, 2026',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (slug) DO NOTHING;

-- 4. Seed FAQs Knowledge Base
INSERT INTO faqs (id, category, question, answer, display_order, is_active, created_at, updated_at) VALUES
(
    'faq-1',
    'General & Craftsmanship',
    'Where are RÓRA bags designed and manufactured?',
    'Our design studio is based in Copenhagen, where prototypes are drafted and stress-tested. Production is carried out in family-owned heritage workshops across Portugal and Northern Italy that meet the highest ethical labor and environmental certifications.',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-2',
    'General & Craftsmanship',
    'What materials do you use in your products?',
    'We use certified vegetable-tanned Tuscan leather, 100% GOTS organic canvas, recycled 900D ballistic nylon, solid brass hardware, and waterproof YKK Aquaguard zippers.',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-3',
    'General & Craftsmanship',
    'Are RÓRA bags water-resistant?',
    'Yes. All our canvas and nylon styles are treated with non-toxic, PFC-free durable water repellent (DWR) coatings. Our zippers feature weather seals to keep contents dry in heavy downpours.',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-4',
    'Shipping & Delivery',
    'How long does shipping take and what does it cost?',
    'Standard Shipping (3–5 business days) is complimentary across India on all orders over ₹1,999. Express shipping (1–2 business days) is available at checkout for ₹199.',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-5',
    'Shipping & Delivery',
    'Do you offer nationwide and international shipping?',
    'Yes, we deliver pan-India with express carbon-neutral couriers. All duties and GST are transparently included in the final price.',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-6',
    'Shipping & Delivery',
    'How can I track my package once dispatched?',
    'Once your order is packed, you will receive an email and SMS with live tracking link. You can also view real-time status in your Account under My Orders.',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-7',
    'Returns & Warranty',
    'What is your return policy?',
    'We offer 30-day hassle-free returns on all unused items in original packaging. Return pickup is complimentary and arranged directly from your doorstep.',
    1,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-8',
    'Returns & Warranty',
    'Do your bags come with a warranty?',
    'Every RÓRA bag carries our Lifetime Craftsmanship Guarantee. If any seam, zipper, or buckle fails under normal use, we will repair or replace it free of charge.',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'faq-9',
    'Returns & Warranty',
    'How do I initiate an exchange for a different color?',
    'Simply visit the Returns & Exchanges portal in your account or contact our concierge at hello@rorabags.com with your order number.',
    3,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

-- 5. Seed Store Operational Settings
INSERT INTO store_settings (id, setting_key, setting_value, setting_type, description, updated_at) VALUES
    ('set-1', 'storeName', 'RÓRA Studios', 'STRING', 'Official store brand name', CURRENT_TIMESTAMP),
    ('set-2', 'tagline', 'Thoughtfully Designed Bags for Modern Journeys', 'STRING', 'Brand marketing tagline', CURRENT_TIMESTAMP),
    ('set-3', 'currency', 'INR (₹)', 'STRING', 'Primary currency display', CURRENT_TIMESTAMP),
    ('set-4', 'supportEmail', 'concierge@rorastudios.com', 'STRING', 'Customer support concierge email', CURRENT_TIMESTAMP),
    ('set-5', 'supportPhone', '+91 22 6944 8000', 'STRING', 'Customer phone hotline', CURRENT_TIMESTAMP),
    ('set-6', 'warehouseAddress', 'Studio 4B, Mathuradas Mills Compound, Lower Parel, Mumbai 400013, India', 'STRING', 'Primary warehouse dispatch atelier', CURRENT_TIMESTAMP),
    ('set-7', 'freeShippingThreshold', '1999', 'NUMBER', 'Free shipping order spend threshold (INR)', CURRENT_TIMESTAMP),
    ('set-8', 'standardShippingFee', '199', 'NUMBER', 'Standard shipping flat rate (INR)', CURRENT_TIMESTAMP),
    ('set-9', 'taxRate', '18% GST (Included in MRP)', 'STRING', 'Tax disclosure statement', CURRENT_TIMESTAMP),
    ('set-10', 'orderPrefix', 'RRA', 'STRING', 'Order number prefix', CURRENT_TIMESTAMP),
    ('set-11', 'inventoryAlertThreshold', '5', 'NUMBER', 'Global inventory low stock alert threshold', CURRENT_TIMESTAMP)
ON CONFLICT (setting_key) DO UPDATE SET
    setting_value = EXCLUDED.setting_value,
    setting_type = EXCLUDED.setting_type,
    description = EXCLUDED.description,
    updated_at = EXCLUDED.updated_at;
