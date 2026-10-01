-- ============================================================================
-- RÓRA Luxury Atelier — Database Schema Migration V3
-- Target: PostgreSQL 18+
-- Description: Seed Categories, Products, Variants, Images & Specifications
-- ============================================================================

-- Ensure optional columns exist on products table
ALTER TABLE products ADD COLUMN IF NOT EXISTS features JSONB;
ALTER TABLE products ADD COLUMN IF NOT EXISTS story TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS subtitle VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS tagline TEXT;
ALTER TABLE products ADD COLUMN IF NOT EXISTS dimensions VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS weight VARCHAR(64);
ALTER TABLE products ADD COLUMN IF NOT EXISTS capacity VARCHAR(64);
ALTER TABLE products ADD COLUMN IF NOT EXISTS sku VARCHAR(128);

-- 1. Seed Categories (8 Master Luxury Categories)
INSERT INTO categories (id, slug, name, title, headline, subtitle, description, hero_image, image, product_count, created_at, updated_at)
VALUES
('backpacks', 'backpacks', 'Backpacks', 'Everyday & Travel Backpacks', 'Everyday & Travel Backpacks', 'Ergonomic luxury for daily commutes and weekend escapes', 'Built for modern explorers, from daily commutes to weekend escapes with weatherproof materials and ergonomic support.', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('laptop-bags', 'laptop-bags', 'Laptop Bags', 'Executive & Workspace Briefs', 'Executive & Workspace Briefs', 'Architectural silhouettes for the focused professional', 'Padded laptop protection meeting sleek architectural silhouettes for the focused professional.', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('handbags', 'handbags', 'Handbags', 'Sculptural & Daily Handbags', 'Sculptural & Daily Handbags', 'Handcrafted full-grain Italian leather with understated hardware', 'Handcrafted full-grain Italian leather with understated hardware and timeless proportions.', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('sling-bags', 'sling-bags', 'Sling Bags', 'Compact Crossbody & Slings', 'Compact Crossbody & Slings', 'Hands-free versatility designed for rapid city movement', 'Hands-free versatility designed for rapid city movement, essentials access, and lightweight carry.', 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1000&q=80', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('travel-bags', 'travel-bags', 'Travel Bags', 'Weekender & Expedition Duffels', 'Weekender & Expedition Duffels', 'Spacious water-repellent luggage engineered for smooth transitions', 'Spacious, water-repellent luggage engineered for smooth transitions from airport tarmac to mountain retreat.', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('tote-bags', 'tote-bags', 'Tote Bags', 'Structured & Market Totes', 'Structured & Market Totes', 'Generous volume, reinforced handles, and thoughtful internal organization', 'Generous volume, reinforced handles, and thoughtful internal organization for seamless all-day carry.', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1000&q=80', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('office-bags', 'office-bags', 'Office Bags', 'Workday & Meeting Folios', 'Workday & Meeting Folios', 'Refined presentation cases with modular document dividers', 'Refined presentation cases with modular document dividers and luggage trolley pass-through sleeves.', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1000&q=80', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('school-college-bags', 'school-college-bags', 'Campus Bags', 'Campus & Academy Packs', 'Campus & Academy Packs', 'Heavy-duty recycled canvas engineered for heavy textbooks and tech', 'Heavy-duty recycled canvas engineered for heavy textbooks, tech accessories, and active days.', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1600&q=80', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    slug = EXCLUDED.slug,
    name = EXCLUDED.name,
    title = EXCLUDED.title,
    headline = EXCLUDED.headline,
    subtitle = EXCLUDED.subtitle,
    description = EXCLUDED.description,
    hero_image = EXCLUDED.hero_image,
    image = EXCLUDED.image,
    updated_at = CURRENT_TIMESTAMP;

-- 2. Seed Products (12 High-End Curated Luxury Items)
INSERT INTO products (
    id, slug, name, subtitle, tagline, category_id, category_name,
    price, original_price, compare_at_price, discount, currency,
    rating, review_count, badge, stock, in_stock,
    is_new_arrival, is_best_seller, is_curated, is_featured,
    description, story, material, specifications, care_instructions, features, tags,
    dimensions, weight, capacity, sku, created_at, updated_at
) VALUES
-- 1. The Nomad Backpack
(
    'prod-1', 'the-nomad-backpack', 'The Nomad Backpack', 'Adventure-ready. Everyday style.',
    'Adventure-ready. Everyday style.', 'backpacks', 'Backpacks',
    4899.00, 5499.00, 5499.00, 11, 'INR',
    4.8, 304, 'Best Seller', 24, true,
    false, true, true, true,
    'Built for modern explorers, the Nomad Backpack combines functionality with timeless design. Crafted from durable, water-resistant recycled materials, it offers the perfect balance of style, comfort and rugged everyday functionality.',
    'Conceived on the train line between Zurich and Milan, the Nomad was engineered to withstand unpredictable alpine rain while remaining sharp enough for boardroom presentations.',
    'Weather-resistant 900D Recycled Nylon & Full-grain Leather',
    '{"Volume": "20 Liters", "Laptop Fit": "Up to 16-inch MacBook Pro", "Outer Fabric": "900D Recycled Oxford Nylon", "Lining": "100% Recycled Poly Jacquard", "Hardware": "Matte Gunmetal Alloy & YKK Zippers", "Origin": "Handcrafted in Porto, Portugal"}',
    '["Wipe clean with a damp microfiber cloth", "Apply leather balm to trims biannually", "Do not machine wash or tumble dry", "Store in provided dust bag in a cool, dry place"]',
    '["20L versatile main volume with dual-access zipper", "Padded laptop compartment (fits up to 16\" MacBook Pro)", "Water-repellent PU coated shell with YKK Aquaguard zippers", "Ergonomic padded shoulder straps with breathable mesh back panel", "Hidden passport and phone security pocket on lumbar back", "Luggage handle pass-through strap for effortless travel"]',
    '["bestseller", "waterproof", "backpack", "travel", "laptop"]',
    '46 × 30 × 16 cm', '0.85 kg', '20L', 'RRA-NMD-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 2. The Classic Leather Tote
(
    'prod-2', 'the-classic-leather-tote', 'The Classic Leather Tote', 'Understated elegance for city and studio.',
    'Understated elegance for city and studio.', 'tote-bags', 'Tote Bags',
    3899.00, 4499.00, 4499.00, 13, 'INR',
    4.9, 182, 'Popular', 18, true,
    false, true, true, true,
    'A minimalist carry-all crafted from supple Tuscan vegetable-tanned leather that develops a rich, personal patina over years of faithful use.',
    'Designed with zero visible exterior seams, focusing purely on raw material integrity and precise hand-burnished edge paint.',
    'Full-Grain Vegetable Tanned Italian Leather',
    '{"Volume": "16 Liters", "Laptop Fit": "Fits 14\" laptop horizontally or 16\" vertically", "Outer Leather": "Full-grain Vegetable Tanned Cowhide", "Handle Drop": "25 cm (comfortable on coats)", "Origin": "Florence, Italy"}',
    '["Condition with organic beeswax cream every 6 months", "Avoid prolonged exposure to direct sunlight", "Wipe moisture immediately with dry cotton cloth"]',
    '["Spacious main compartment holds water bottle, umbrella, tablet, and books", "Interior zippered safety pocket with brass key leash", "Dual magnetic snap closure with leather reinforced tabs", "Reinforced base panel with subtle metal feet to protect leather"]',
    '["leather", "tote", "handcrafted", "italian", "work"]',
    '38 × 42 × 14 cm', '0.72 kg', '16L', 'RRA-TOT-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 3. The Urban Sling
(
    'prod-3', 'the-urban-sling', 'The Urban Sling', 'Minimalist hands-free city companion.',
    'Minimalist hands-free city companion.', 'sling-bags', 'Sling Bags',
    2899.00, 3299.00, 3299.00, 12, 'INR',
    4.7, 96, 'Essential', 32, true,
    true, false, true, false,
    'An asymmetrical crossbody sling engineered for rapid access to phone, keys, sunglasses, and travel credentials without bulk.',
    'Streamlined for cyclists and urban pedestrians who value freedom of movement without sacrificing refined aesthetics.',
    'Structured Cordura Nylon & Weatherproof Zips',
    '{"Volume": "4.5 Liters", "Strap Range": "78cm to 135cm", "Outer Fabric": "500D Ballistic Cordura", "Buckles": "Fidlock Magnetic System"}',
    '["Spot clean with mild soapy water", "Air dry naturally away from direct heat", "Do not iron"]',
    '["Quick-release magnetic Fidlock V-buckle strap", "Self-compressing gusset expanding from 2L to 4.5L", "Soft micro-fleece lined sunglasses pocket", "Concealed rear zip for passport and transit cards"]',
    '["sling", "crossbody", "minimal", "commute", "waterproof"]',
    '18 × 32 × 9 cm', '0.38 kg', '4.5L', 'RRA-SLG-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 4. The Weekend Duffle
(
    'prod-4', 'the-weekend-duffle', 'The Weekend Duffle', 'Spacious getaway luggage with vintage soul.',
    'Spacious getaway luggage with vintage soul.', 'travel-bags', 'Travel Bags',
    6499.00, 7299.00, 7299.00, 11, 'INR',
    4.9, 142, 'Staff Pick', 12, true,
    false, true, true, true,
    'An heirloom-quality duffle crafted from heavy Scottish waxed canvas. Meets major international airline carry-on dimensions while accommodating 3-5 days of travel apparel.',
    'Built to gain character through airports, road trips, and train journeys. Naturally waterproofed with non-toxic beeswax.',
    'Heavy 18oz Waxed Cotton Canvas & Bridle Leather',
    '{"Volume": "42 Liters", "Airline Status": "Carry-on compliant (IATA)", "Canvas": "18oz Halley Stevensons Waxed Cotton", "Leather": "Vegetable Tanned Bridle Leather"}',
    '["Re-wax canvas every 2-3 years using paraffin or beeswax", "Do not dry clean or use detergent", "Brush off dried mud with soft bristle brush"]',
    '["42L volume with wide U-shaped doctor-bag style opening", "Dedicated ventilated shoe compartment with water-resistant lining", "Detachable padded leather shoulder strap", "Solid antique brass rivets and hardware"]',
    '["duffle", "travel", "waxed-canvas", "heritage", "weekender"]',
    '54 × 30 × 26 cm', '1.45 kg', '42L', 'RRA-DUF-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 5. The Executive Laptop Brief
(
    'prod-5', 'the-executive-laptop-brief', 'The Executive Laptop Brief', 'Tailored architecture for modern work.',
    'Tailored architecture for modern work.', 'laptop-bags', 'Laptop Bags',
    4499.00, 4999.00, 4999.00, 10, 'INR',
    4.8, 118, 'New Arrival', 15, true,
    true, false, true, true,
    'A razor-sharp silhouette designed for professionals carrying laptops, tablets, chargers, and documents in pristine order.',
    'Constructed around a lightweight structural frame that stands upright when placed on meeting tables or floors.',
    'Matte Technical Canvas with Italian Calfskin Trims',
    '{"Volume": "14 Liters", "Max Laptop": "Up to 15.6 inches (38 × 26 cm)", "Outer Fabric": "Recycled Poly with DWR Coating", "Origin": "Porto, Portugal"}',
    '["Wipe clean with a damp lint-free cloth", "Avoid overloading beyond 8kg"]',
    '["Dual-cushioned 15.6\" laptop and 12.9\" tablet sleeves", "Accordion document divider for contracts and notebooks", "Magnetic quick-grab phone and badge exterior pocket", "Luggage trolley pass-through on back panel"]',
    '["briefcase", "laptop", "office", "executive", "tech"]',
    '40 × 29 × 8.5 cm', '0.92 kg', '14L', 'RRA-LAP-05', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 6. The Minimal Studio Tote
(
    'prod-6', 'the-minimal-studio-tote', 'The Minimal Studio Tote', 'Pure form, daily utility.',
    'Pure form, daily utility.', 'tote-bags', 'Tote Bags',
    3299.00, 3699.00, 3699.00, 10, 'INR',
    4.6, 79, 'Studio Edition', 22, true,
    false, false, true, false,
    'An architectural daily tote stripped of ornamentation. Built from unbleached organic cotton canvas with saddle-stitched leather handles.',
    'Inspired by Japanese utility bags from Kyoto artisan workshops.',
    'Natural Heavy Canvas & Vegetable Leather Straps',
    '{"Volume": "18 Liters", "Material": "100% GOTS Certified Organic Cotton", "Origin": "Kyoto Inspired, Made in India"}',
    '["Hand wash cold with gentle detergent", "Lay flat to dry naturally in shade"]',
    '["Wide open access with internal zip organizer", "Key ring hook and bottle holder sleeve", "Reinforced box-stitched handle anchor points"]',
    '["studio", "tote", "organic", "cotton", "minimal"]',
    '36 × 40 × 12 cm', '0.55 kg', '18L', 'RRA-STD-06', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 7. The Campus Explorer
(
    'prod-7', 'the-campus-explorer', 'The Campus Explorer', 'Engineered for lectures, libraries, and weekend escapes.',
    'Engineered for lectures, libraries, and weekend escapes.', 'school-college-bags', 'Campus Bags',
    3999.00, 4499.00, 4499.00, 11, 'INR',
    4.8, 210, 'Popular', 28, true,
    true, true, false, false,
    'Designed for student schedules and active lifestyles with dedicated spaces for laptops, lunch containers, water bottles, and stationery.',
    'Stress-tested with 15kg load cycles to guarantee season-after-season dependability.',
    'Rugged Cordura with Reinforced Leather Base',
    '{"Volume": "24 Liters", "Laptop Capacity": "16-inch laptops", "Weight": "880g"}',
    '["Wipe clean with soapy sponge", "Do not submerge leather base in water"]',
    '["High-density EVA foam shoulder pads", "Dual exterior expandable water bottle holders", "Fleece-lined top quick-access stash pocket", "Reflective subtle trim on zipper pulls"]',
    '["campus", "student", "backpack", "durable", "college"]',
    '48 × 31 × 18 cm', '0.88 kg', '24L', 'RRA-CMP-07', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 8. The City Crossbody Handbag
(
    'prod-8', 'the-city-crossbody-handbag', 'The City Crossbody Handbag', 'Structured luxury in a compact silhouette.',
    'Structured luxury in a compact silhouette.', 'handbags', 'Handbags',
    4299.00, 4899.00, 4899.00, 12, 'INR',
    4.9, 88, 'Craft Series', 14, true,
    true, false, true, true,
    'An elegant structured day-to-evening bag with detachable adjustable crossbody strap and magnetic clasp flap.',
    'Handmade by master artisans in Spain, utilizing ancient edge-creasing techniques.',
    'Box Calf Leather with Gold-Tone Hardware',
    '{"Volume": "6 Liters", "Leather": "100% Spanish Box Calf", "Lining": "Suede Microfiber"}',
    '["Protect from rain with silicone waterproofer", "Store stuffed with tissue to retain structure"]',
    '["Detachable and reversible leather strap", "Three interior card slots & zip coin pocket", "Flawless hand-painted edge finishing"]',
    '["handbag", "luxury", "calfskin", "evening", "crossbody"]',
    '26 × 20 × 10 cm', '0.48 kg', '6L', 'RRA-HND-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 9. The Artisan Atelier Handbag
(
    'prod-9', 'the-artisan-atelier-handbag', 'The Artisan Atelier Handbag', 'Sculptural masterpiece in full-grain calf leather.',
    'Sculptural masterpiece in full-grain calf leather.', 'handbags', 'Handbags',
    5799.00, 6499.00, 6499.00, 10, 'INR',
    5.0, 64, 'Limited Edition', 10, true,
    true, false, true, true,
    'A statement sculptural silhouette marrying architectural geometry with the richest vegetable-tanned full-grain leather.',
    'Sculpted by hand over custom wooden lasts in Florence, creating an iconic rigid yet sensual curve.',
    'Full-Grain Tuscan Calf Leather & Brushed Brass',
    '{"Volume": "8 Liters", "Leather": "Tuscan Semi-Vegetable Calf", "Hardware": "Solid Brushed Brass", "Origin": "Florence, Italy"}',
    '["Store in flannel dust pouch", "Polish with neutral cream every 4 months"]',
    '["Hand-molded leather top handle with 12cm drop", "Magnetic kissing lock closure", "Hidden suede slip pocket for phone"]',
    '["sculptural", "handbag", "atelier", "collector", "leather"]',
    '28 × 22 × 12 cm', '0.62 kg', '8L', 'RRA-HND-09', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 10. The Voyager Expandable Backpack
(
    'prod-10', 'the-voyager-expandable-backpack', 'The Voyager Expandable Backpack', 'Expands from 22L daily carry to 34L travel pack.',
    'Expands from 22L daily carry to 34L travel pack.', 'backpacks', 'Backpacks',
    5299.00, 5999.00, 5999.00, 11, 'INR',
    4.9, 175, 'Bestseller', 20, true,
    false, true, true, true,
    'Engineered for digital nomads, effortlessly expanding with a 360-degree perimeter zipper from a slim daily commuter to an international flight carry-on.',
    'Developed through 200,000 miles of air travel testing to refine weight balance and pocket ergonomics.',
    '1000D CORDURA Ballistic Nylon & TPU Base',
    '{"Capacity": "22L to 34L Expanded", "Laptop Fit": "17-inch Gaming/Workstation Laptops", "Waterproof Rating": "IPX4 Rainproof", "Zippers": "YKK Aquaguard RC"}',
    '["Clean with wet cloth and mild soap", "Air dry away from heat sources"]',
    '["360-degree zip expansion adds 12L instant packing volume", "Clamshell 180-degree suitcase style main opening", "TSA-approved lie-flat laptop compartment", "Magnetic sternum strap and stowable waist belt"]',
    '["travel", "expandable", "backpack", "tech", "nomad"]',
    '49 × 32 × 18 cm', '1.15 kg', '34L', 'RRA-VOY-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 11. The Heritage Document Folio
(
    'prod-11', 'the-heritage-document-folio', 'The Heritage Document Folio', 'Executive meeting companion with pen loop and notebook sleeve.',
    'Executive meeting companion with pen loop and notebook sleeve.', 'office-bags', 'Office Bags',
    3499.00, 3999.00, 3999.00, 12, 'INR',
    4.8, 52, 'Executive', 16, true,
    false, false, true, false,
    'Sleek zippered portfolio crafted to safeguard legal documents, A4 notebooks, 13\" MacBook Air, and writing instruments with effortless gravitas.',
    'Designed for the modern decision-maker who values tactile refinement in every boardroom engagement.',
    'Smooth Napa Leather & Microfiber Suede',
    '{"Fit": "13-inch laptops & A4 Documents", "Leather": "Grade-A Full Grain Napa", "Closure": "Smooth gliding Excella metal zipper"}',
    '["Moisturize with leather cream", "Keep away from sharp objects"]',
    '["Dedicated padded 13-inch tablet/laptop slip pocket", "4 business card slots, pen loop, and passport sleeve", "Expanding file divider for contracts and legal pads"]',
    '["folio", "office", "leather", "executive", "meeting"]',
    '34 × 25 × 3 cm', '0.42 kg', '3L', 'RRA-FOL-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
-- 12. The Grand Tourer Expedition Bag
(
    'prod-12', 'the-grand-tourer-expedition-bag', 'The Grand Tourer Expedition Bag', 'Rugged 55L adventure duffle with convertible backpack straps.',
    'Rugged 55L adventure duffle with convertible backpack straps.', 'travel-bags', 'Travel Bags',
    7899.00, 8999.00, 8999.00, 12, 'INR',
    4.9, 112, 'Explorer Choice', 15, true,
    true, false, true, true,
    'The ultimate overland travel duffle. Heavyweight waterproof tarpaulin combined with ballistic nylon and stowable ergonomic shoulder straps.',
    'Built for extreme weather and remote expeditions from Scandinavian fjords to Himalayan basecamps.',
    'Waterproof 840D TPU Tarpaulin & Ballistic Nylon',
    '{"Volume": "55 Liters", "Weight": "1.65 kg", "Waterproof Rating": "Submersible Base / Stormproof Body"}',
    '["Hose down with water after muddy expeditions", "Air dry fully before storing"]',
    '["Contoured backpack straps tuck completely away into lid panel", "Internal compression straps keep heavy gear stabilized", "Dual side haul handles tested to 50kg lifting capacity", "End cap zippered compartment for dirty boots or laundry"]',
    '["expedition", "travel", "waterproof", "duffle", "adventure"]',
    '62 × 36 × 28 cm', '1.65 kg', '55L', 'RRA-GTE-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO UPDATE SET
    slug = EXCLUDED.slug,
    name = EXCLUDED.name,
    subtitle = EXCLUDED.subtitle,
    tagline = EXCLUDED.tagline,
    category_id = EXCLUDED.category_id,
    category_name = EXCLUDED.category_name,
    price = EXCLUDED.price,
    original_price = EXCLUDED.original_price,
    compare_at_price = EXCLUDED.compare_at_price,
    discount = EXCLUDED.discount,
    currency = EXCLUDED.currency,
    rating = EXCLUDED.rating,
    review_count = EXCLUDED.review_count,
    badge = EXCLUDED.badge,
    stock = EXCLUDED.stock,
    in_stock = EXCLUDED.in_stock,
    is_new_arrival = EXCLUDED.is_new_arrival,
    is_best_seller = EXCLUDED.is_best_seller,
    is_curated = EXCLUDED.is_curated,
    is_featured = EXCLUDED.is_featured,
    description = EXCLUDED.description,
    story = EXCLUDED.story,
    material = EXCLUDED.material,
    specifications = EXCLUDED.specifications,
    care_instructions = EXCLUDED.care_instructions,
    features = EXCLUDED.features,
    tags = EXCLUDED.tags,
    dimensions = EXCLUDED.dimensions,
    weight = EXCLUDED.weight,
    capacity = EXCLUDED.capacity,
    sku = EXCLUDED.sku,
    updated_at = CURRENT_TIMESTAMP;

-- 3. Seed Product Variants (Color & Style options)
DELETE FROM product_variants WHERE product_id IN (
    'prod-1', 'prod-2', 'prod-3', 'prod-4', 'prod-5', 'prod-6',
    'prod-7', 'prod-8', 'prod-9', 'prod-10', 'prod-11', 'prod-12'
);

INSERT INTO product_variants (id, product_id, sku, name, color_name, color_hex, image, stock, created_at, updated_at)
VALUES
-- prod-1 variants
('var-1-1', 'prod-1', 'RRA-NMD-01-OLV', 'Olive Green', 'Olive Green', '#555E48', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-1-2', 'prod-1', 'RRA-NMD-01-BLK', 'Charcoal Black', 'Charcoal Black', '#1E1D1A', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1000&q=80', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-1-3', 'prod-1', 'RRA-NMD-01-TAU', 'Warm Taupe', 'Warm Taupe', '#B9AD9D', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-1-4', 'prod-1', 'RRA-NMD-01-BRN', 'Muted Brown', 'Muted Brown', '#715B49', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-2 variants
('var-2-1', 'prod-2', 'RRA-TOT-02-COG', 'Cognac Brown', 'Cognac Brown', '#715B49', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1000&q=80', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-2-2', 'prod-2', 'RRA-TOT-02-NOI', 'Noir Black', 'Noir Black', '#1E1D1A', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-2-3', 'prod-2', 'RRA-TOT-02-SND', 'Sand Taupe', 'Sand Taupe', '#D8CFC1', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-3 variants
('var-3-1', 'prod-3', 'RRA-SLG-03-BLK', 'Obsidian Black', 'Obsidian Black', '#11110F', 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1000&q=80', 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-3-2', 'prod-3', 'RRA-SLG-03-OLV', 'Olive Drab', 'Olive Drab', '#555E48', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-3-3', 'prod-3', 'RRA-SLG-03-GRY', 'Stone Grey', 'Stone Grey', '#9A8D7D', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-4 variants
('var-4-1', 'prod-4', 'RRA-DUF-04-SAF', 'Safari Olive', 'Safari Olive', '#555E48', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-4-2', 'prod-4', 'RRA-DUF-04-BRN', 'Earth Brown', 'Earth Brown', '#715B49', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-4-3', 'prod-4', 'RRA-DUF-04-GRY', 'Charcoal Grey', 'Charcoal Grey', '#1E1D1A', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-5 variants
('var-5-1', 'prod-5', 'RRA-LAP-05-CHR', 'Matte Charcoal', 'Matte Charcoal', '#1E1D1A', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-5-2', 'prod-5', 'RRA-LAP-05-ESP', 'Espresso Brown', 'Espresso Brown', '#594637', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-6 variants
('var-6-1', 'prod-6', 'RRA-STD-06-SND', 'Natural Sand', 'Natural Sand', '#D8CFC1', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1000&q=80', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-6-2', 'prod-6', 'RRA-STD-06-OLV', 'Olive Green', 'Olive Green', '#555E48', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-6-3', 'prod-6', 'RRA-STD-06-BLK', 'Charcoal Black', 'Charcoal Black', '#1E1D1A', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-7 variants
('var-7-1', 'prod-7', 'RRA-CMP-07-OLV', 'Olive Green', 'Olive Green', '#68705A', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-7-2', 'prod-7', 'RRA-CMP-07-TAU', 'Navy Taupe', 'Navy Taupe', '#9A8D7D', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-7-3', 'prod-7', 'RRA-CMP-07-BRN', 'Earth Brown', 'Earth Brown', '#715B49', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-8 variants
('var-8-1', 'prod-8', 'RRA-HND-08-COG', 'Cognac Leather', 'Cognac Leather', '#715B49', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-8-2', 'prod-8', 'RRA-HND-08-NOI', 'Noir Black', 'Noir Black', '#11110F', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-9 variants
('var-9-1', 'prod-9', 'RRA-HND-09-TER', 'Terracotta Sienna', 'Terracotta Sienna', '#A45A2A', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-9-2', 'prod-9', 'RRA-HND-09-BLK', 'Obsidian Calf', 'Obsidian Calf', '#1E1D1A', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-10 variants
('var-10-1', 'prod-10', 'RRA-VOY-10-BLK', 'Stealth Black', 'Stealth Black', '#11110F', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-10-2', 'prod-10', 'RRA-VOY-10-OLV', 'Alpine Olive', 'Alpine Olive', '#555E48', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-11 variants
('var-11-1', 'prod-11', 'RRA-FOL-11-NOI', 'Noir Black', 'Noir Black', '#1E1D1A', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1000&q=80', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-11-2', 'prod-11', 'RRA-FOL-11-BRN', 'Mahogany Tan', 'Mahogany Tan', '#715B49', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- prod-12 variants
('var-12-1', 'prod-12', 'RRA-GTE-12-BLK', 'Stormproof Matte Black', 'Stormproof Matte Black', '#11110F', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('var-12-2', 'prod-12', 'RRA-GTE-12-OLV', 'Nordic Pine', 'Nordic Pine', '#555E48', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. Seed Product Images
DELETE FROM product_images WHERE product_id IN (
    'prod-1', 'prod-2', 'prod-3', 'prod-4', 'prod-5', 'prod-6',
    'prod-7', 'prod-8', 'prod-9', 'prod-10', 'prod-11', 'prod-12'
);

INSERT INTO product_images (id, product_id, image_url, display_order, created_at)
VALUES
('img-1-1', 'prod-1', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-1-2', 'prod-1', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),
('img-1-3', 'prod-1', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80', 2, CURRENT_TIMESTAMP),
('img-1-4', 'prod-1', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80', 3, CURRENT_TIMESTAMP),

('img-2-1', 'prod-2', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-2-2', 'prod-2', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),
('img-2-3', 'prod-2', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80', 2, CURRENT_TIMESTAMP),

('img-3-1', 'prod-3', 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-3-2', 'prod-3', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),
('img-3-3', 'prod-3', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80', 2, CURRENT_TIMESTAMP),

('img-4-1', 'prod-4', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-4-2', 'prod-4', 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),
('img-4-3', 'prod-4', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80', 2, CURRENT_TIMESTAMP),

('img-5-1', 'prod-5', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-5-2', 'prod-5', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),

('img-6-1', 'prod-6', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-6-2', 'prod-6', 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),

('img-7-1', 'prod-7', 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-7-2', 'prod-7', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),

('img-8-1', 'prod-8', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-8-2', 'prod-8', 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80', 1, CURRENT_TIMESTAMP),

('img-9-1', 'prod-9', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-10-1', 'prod-10', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-11-1', 'prod-11', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP),
('img-12-1', 'prod-12', 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1200&q=80', 0, CURRENT_TIMESTAMP);
