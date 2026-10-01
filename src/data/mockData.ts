// Master Product, Category, Content and Admin Mock Data (Currency: INR ₹)
// Cohesive with warm, earthy luxury brand identity "RÓRA"

export const CATEGORIES = [
  {
    id: 'backpacks',
    slug: 'backpacks',
    name: 'Backpacks',
    headline: 'Everyday & Travel Backpacks',
    description: 'Built for modern explorers, from daily commutes to weekend escapes with weatherproof materials and ergonomic support.',
    heroImage: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1600&q=80',
    count: 28
  },
  {
    id: 'laptop-bags',
    slug: 'laptop-bags',
    name: 'Laptop Bags',
    headline: 'Executive & Workspace Briefs',
    description: 'Padded laptop protection meeting sleek architectural silhouettes for the focused professional.',
    heroImage: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1600&q=80',
    count: 16
  },
  {
    id: 'handbags',
    slug: 'handbags',
    name: 'Handbags',
    headline: 'Sculptural & Daily Handbags',
    description: 'Handcrafted full-grain Italian leather with understated hardware and timeless proportions.',
    heroImage: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1600&q=80',
    count: 12
  },
  {
    id: 'sling-bags',
    slug: 'sling-bags',
    name: 'Sling Bags',
    headline: 'Compact Crossbody & Slings',
    description: 'Hands-free versatility designed for rapid city movement, essentials access, and lightweight carry.',
    heroImage: 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1600&q=80',
    count: 14
  },
  {
    id: 'travel-bags',
    slug: 'travel-bags',
    name: 'Travel Bags',
    headline: 'Weekender & Expedition Duffels',
    description: 'Spacious, water-repellent luggage engineered for smooth transitions from airport tarmac to mountain retreat.',
    heroImage: 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1600&q=80',
    count: 10
  },
  {
    id: 'tote-bags',
    slug: 'tote-bags',
    name: 'Tote Bags',
    headline: 'Structured & Market Totes',
    description: 'Generous volume, reinforced handles, and thoughtful internal organization for seamless all-day carry.',
    heroImage: 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1600&q=80',
    count: 8
  },
  {
    id: 'office-bags',
    slug: 'office-bags',
    name: 'Office Bags',
    headline: 'Workday & Meeting Folios',
    description: 'Refined presentation cases with modular document dividers and luggage trolley pass-through sleeves.',
    heroImage: 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1600&q=80',
    count: 6
  },
  {
    id: 'school-college-bags',
    slug: 'school-college-bags',
    name: 'Campus Bags',
    headline: 'Campus & Academy Packs',
    description: 'Heavy-duty recycled canvas engineered for heavy textbooks, tech accessories, and active days.',
    heroImage: 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1600&q=80',
    count: 7
  }
];

export const PRODUCTS = [
  {
    id: 'prod-1',
    name: 'The Nomad Backpack',
    slug: 'the-nomad-backpack',
    tagline: 'Adventure-ready. Everyday style.',
    category: 'backpacks',
    price: 4899,
    originalPrice: 5499,
    discount: 11,
    rating: 4.8,
    reviewCount: 304,
    badge: 'Best Seller',
    stock: 24,
    sku: 'RRA-NMD-01',
    material: 'Weather-resistant 900D Recycled Nylon & Full-grain Leather',
    capacity: '20L',
    size: 'One Size (H: 46cm, W: 30cm, D: 16cm)',
    weight: '0.85 kg',
    dimensions: '46 × 30 × 16 cm',
    colors: [
      { name: 'Olive Green', hex: '#555E48', image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Charcoal Black', hex: '#1E1D1A', image: 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Warm Taupe', hex: '#B9AD9D', image: 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Muted Brown', hex: '#715B49', image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'Built for modern explorers, the Nomad Backpack combines functionality with timeless design. Crafted from durable, water-resistant recycled materials, it offers the perfect balance of style, comfort and rugged everyday functionality.',
    story: 'Conceived on the train line between Zurich and Milan, the Nomad was engineered to withstand unpredictable alpine rain while remaining sharp enough for boardroom presentations.',
    features: [
      '20L versatile main volume with dual-access zipper',
      'Padded laptop compartment (fits up to 16" MacBook Pro)',
      'Water-repellent PU coated shell with YKK Aquaguard zippers',
      'Ergonomic padded shoulder straps with breathable mesh back panel',
      'Hidden passport and phone security pocket on lumbar back',
      'Luggage handle pass-through strap for effortless travel'
    ],
    specifications: {
      'Volume': '20 Liters',
      'Laptop Fit': 'Up to 16-inch laptops',
      'Outer Fabric': '900D Recycled Oxford Nylon',
      'Lining': '100% Recycled Poly Jacquard',
      'Hardware': 'Matte Gunmetal Alloy & YKK Zippers',
      'Origin': 'Handcrafted in Porto, Portugal'
    }
  },
  {
    id: 'prod-2',
    name: 'The Classic Leather Tote',
    slug: 'the-classic-leather-tote',
    tagline: 'Understated elegance for city and studio.',
    category: 'tote-bags',
    price: 3899,
    originalPrice: 4499,
    discount: 13,
    rating: 4.9,
    reviewCount: 182,
    badge: 'Popular',
    stock: 18,
    sku: 'RRA-TOT-02',
    material: 'Full-Grain Vegetable Tanned Italian Leather',
    capacity: '16L',
    size: 'Standard (H: 38cm, W: 42cm, D: 14cm)',
    weight: '0.72 kg',
    dimensions: '38 × 42 × 14 cm',
    colors: [
      { name: 'Cognac Brown', hex: '#715B49', image: 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Noir Black', hex: '#1E1D1A', image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Sand Taupe', hex: '#D8CFC1', image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'A minimalist carry-all crafted from supple Tuscan vegetable-tanned leather that develops a rich, personal patina over years of faithful use.',
    story: 'Designed with zero visible exterior seams, focusing purely on raw material integrity and precise hand-burnished edge paint.',
    features: [
      'Spacious main compartment holds water bottle, umbrella, tablet, and books',
      'Interior zippered safety pocket with brass key leash',
      'Dual magnetic snap closure with leather reinforced tabs',
      'Reinforced base panel with subtle metal feet to protect leather'
    ],
    specifications: {
      'Volume': '16 Liters',
      'Laptop Fit': 'Fits 14" laptop horizontally or 16" vertically',
      'Outer Leather': 'Full-grain Vegetable Tanned Cowhide',
      'Handle Drop': '25 cm (comfortable on coats)',
      'Origin': 'Florence, Italy'
    }
  },
  {
    id: 'prod-3',
    name: 'The Urban Sling',
    slug: 'the-urban-sling',
    tagline: 'Minimalist hands-free city companion.',
    category: 'sling-bags',
    price: 2899,
    originalPrice: 3299,
    discount: 12,
    rating: 4.7,
    reviewCount: 96,
    badge: 'Essential',
    stock: 32,
    sku: 'RRA-SLG-03',
    material: 'Structured Cordura Nylon & Weatherproof Zips',
    capacity: '4.5L',
    size: 'Compact (H: 18cm, W: 32cm, D: 9cm)',
    weight: '0.38 kg',
    dimensions: '18 × 32 × 9 cm',
    colors: [
      { name: 'Obsidian Black', hex: '#11110F', image: 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Olive Drab', hex: '#555E48', image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Stone Grey', hex: '#9A8D7D', image: 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'An asymmetrical crossbody sling engineered for rapid access to phone, keys, sunglasses, and travel credentials without bulk.',
    story: 'Streamlined for cyclists and urban pedestrians who value freedom of movement without sacrificing refined aesthetics.',
    features: [
      'Quick-release magnetic Fidlock V-buckle strap',
      'Self-compressing gusset expanding from 2L to 4.5L',
      'Soft micro-fleece lined sunglasses pocket',
      'Concealed rear zip for passport and transit cards'
    ],
    specifications: {
      'Volume': '4.5 Liters',
      'Strap Range': '78cm to 135cm',
      'Outer Fabric': '500D Ballistic Cordura',
      'Buckles': 'Fidlock Magnetic System'
    }
  },
  {
    id: 'prod-4',
    name: 'The Weekend Duffle',
    slug: 'the-weekend-duffle',
    tagline: 'Spacious getaway luggage with vintage soul.',
    category: 'travel-bags',
    price: 6499,
    originalPrice: 7299,
    discount: 11,
    rating: 4.9,
    reviewCount: 142,
    badge: 'Staff Pick',
    stock: 12,
    sku: 'RRA-DUF-04',
    material: 'Heavy 18oz Waxed Cotton Canvas & Bridle Leather',
    capacity: '42L',
    size: 'Cabin Approved (H: 30cm, W: 54cm, D: 26cm)',
    weight: '1.45 kg',
    dimensions: '54 × 30 × 26 cm',
    colors: [
      { name: 'Safari Olive', hex: '#555E48', image: 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Earth Brown', hex: '#715B49', image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Charcoal Grey', hex: '#1E1D1A', image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'An heirloom-quality duffle crafted from heavy Scottish waxed canvas. Meets major international airline carry-on dimensions while accommodating 3-5 days of travel apparel.',
    story: 'Built to gain character through airports, road trips, and train journeys. Naturally waterproofed with non-toxic beeswax.',
    features: [
      '42L volume with wide U-shaped doctor-bag style opening',
      'Dedicated ventilated shoe compartment with water-resistant lining',
      'Detachable padded leather shoulder strap',
      'Solid antique brass rivets and hardware'
    ],
    specifications: {
      'Volume': '42 Liters',
      'Airline Status': 'Carry-on compliant (IATA)',
      'Canvas': '18oz Halley Stevensons Waxed Cotton',
      'Leather': 'Vegetable Tanned Bridle Leather'
    }
  },
  {
    id: 'prod-5',
    name: 'The Executive Laptop Brief',
    slug: 'the-executive-laptop-brief',
    tagline: 'Tailored architecture for modern work.',
    category: 'laptop-bags',
    price: 4499,
    originalPrice: 4999,
    discount: 10,
    rating: 4.8,
    reviewCount: 118,
    badge: 'New Arrival',
    stock: 15,
    sku: 'RRA-LAP-05',
    material: 'Matte Technical Canvas with Italian Calfskin Trims',
    capacity: '14L',
    size: 'Slim (H: 29cm, W: 40cm, D: 8.5cm)',
    weight: '0.92 kg',
    dimensions: '40 × 29 × 8.5 cm',
    colors: [
      { name: 'Matte Charcoal', hex: '#1E1D1A', image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Espresso Brown', hex: '#594637', image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'A razor-sharp silhouette designed for professionals carrying laptops, tablets, chargers, and documents in pristine order.',
    story: 'Constructed around a lightweight structural frame that stands upright when placed on meeting tables or floors.',
    features: [
      'Dual-cushioned 15.6" laptop and 12.9" tablet sleeves',
      'Accordion document divider for contracts and notebooks',
      'Magnetic quick-grab phone and badge exterior pocket',
      'Luggage trolley pass-through on back panel'
    ],
    specifications: {
      'Volume': '14 Liters',
      'Max Laptop': 'Up to 15.6 inches (38 × 26 cm)',
      'Outer Fabric': 'Recycled Poly with DWR Coating'
    }
  },
  {
    id: 'prod-6',
    name: 'The Minimal Studio Tote',
    slug: 'the-minimal-studio-tote',
    tagline: 'Pure form, daily utility.',
    category: 'tote-bags',
    price: 3299,
    originalPrice: 3699,
    discount: 10,
    rating: 4.6,
    reviewCount: 79,
    badge: '',
    stock: 22,
    sku: 'RRA-STD-06',
    material: 'Natural Heavy Canvas & Vegetable Leather Straps',
    capacity: '18L',
    size: 'Standard (H: 40cm, W: 36cm, D: 12cm)',
    weight: '0.55 kg',
    dimensions: '36 × 40 × 12 cm',
    colors: [
      { name: 'Natural Sand', hex: '#D8CFC1', image: 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Olive Green', hex: '#555E48', image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Charcoal Black', hex: '#1E1D1A', image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'An architectural daily tote stripped of ornamentation. Built from unbleached organic cotton canvas with saddle-stitched leather handles.',
    story: 'Inspired by Japanese utility bags from Kyoto artisan workshops.',
    features: [
      'Wide open access with internal zip organizer',
      'Key ring hook and bottle holder sleeve',
      'Reinforced box-stitched handle anchor points'
    ],
    specifications: {
      'Volume': '18 Liters',
      'Material': '100% GOTS Certified Organic Cotton'
    }
  },
  {
    id: 'prod-7',
    name: 'The Campus Explorer',
    slug: 'the-campus-explorer',
    tagline: 'Engineered for lectures, libraries, and weekend escapes.',
    category: 'school-college-bags',
    price: 3999,
    originalPrice: 4499,
    discount: 11,
    rating: 4.8,
    reviewCount: 210,
    badge: 'Popular',
    stock: 28,
    sku: 'RRA-CMP-07',
    material: 'Rugged Cordura with Reinforced Leather Base',
    capacity: '24L',
    size: 'Standard (H: 48cm, W: 31cm, D: 18cm)',
    weight: '0.88 kg',
    dimensions: '48 × 31 × 18 cm',
    colors: [
      { name: 'Olive Green', hex: '#68705A', image: 'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Navy Taupe', hex: '#9A8D7D', image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Earth Brown', hex: '#715B49', image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1546938576-6e6a64f317cc?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'Designed for student schedules and active lifestyles with dedicated spaces for laptops, lunch containers, water bottles, and stationery.',
    story: 'Stress-tested with 15kg load cycles to guarantee season-after-season dependability.',
    features: [
      'High-density EVA foam shoulder pads',
      'Dual exterior expandable water bottle holders',
      'Fleece-lined top quick-access stash pocket',
      'Reflective subtle trim on zipper pulls'
    ],
    specifications: {
      'Volume': '24 Liters',
      'Laptop Capacity': '16-inch laptops',
      'Weight': '880g'
    }
  },
  {
    id: 'prod-8',
    name: 'The City Crossbody Handbag',
    slug: 'the-city-crossbody-handbag',
    tagline: 'Structured luxury in a compact silhouette.',
    category: 'handbags',
    price: 4299,
    originalPrice: 4899,
    discount: 12,
    rating: 4.9,
    reviewCount: 88,
    badge: 'Craft Series',
    stock: 14,
    sku: 'RRA-HND-08',
    material: 'Box Calf Leather with Gold-Tone Hardware',
    capacity: '6L',
    size: 'Petite (H: 20cm, W: 26cm, D: 10cm)',
    weight: '0.48 kg',
    dimensions: '26 × 20 × 10 cm',
    colors: [
      { name: 'Cognac Leather', hex: '#715B49', image: 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1000&q=80' },
      { name: 'Noir Black', hex: '#11110F', image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80' }
    ],
    images: [
      'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=1200&q=80',
      'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=1200&q=80'
    ],
    description: 'An elegant structured day-to-evening bag with detachable adjustable crossbody strap and magnetic clasp flap.',
    story: 'Handmade by master artisans in Spain, utilizing ancient edge-creasing techniques.',
    features: [
      'Detachable and reversible leather strap',
      'Three interior card slots & zip coin pocket',
      'Flawless hand-painted edge finishing'
    ],
    specifications: {
      'Volume': '6 Liters',
      'Leather': '100% Spanish Box Calf',
      'Lining': 'Suede Microfiber'
    }
  }
];

export const JOURNAL_ARTICLES = [
  {
    id: 'art-1',
    slug: '5-must-have-features-in-a-travel-bag',
    title: '5 Must-Have Features in an Intentional Travel Bag',
    category: 'Travel & Mobility',
    excerpt: 'Navigating international transit requires deliberate geometry: why water-resistance, hidden security pockets, and balanced weight distribution matter most.',
    date: 'April 25, 2026',
    readTime: '5 min read',
    image: 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=1000&q=80',
    content: `
      Traveling with a single, well-crafted bag changes your entire relationship with journeys. Instead of fighting oversized rolling luggage on European cobblestones or crowded subway platforms, an ergonomically balanced bag keeps your hands free and your mind focused on the horizon.

      ### 1. Dual-Access Architecture
      When you are in security lines, digging through layers of clothing to retrieve a laptop is the fastest way to cause friction. A dedicated perimeter zipper allows instant access to electronics without exposing personal items.

      ### 2. High-Denier Weather Repellency
      Sudden rain showers in Tokyo or morning mist in the Scottish Highlands shouldn't threaten your sketchbook or camera gear. Dense weaves treated with hydrophobic coatings create natural bead-and-roll defense without toxic fluorochemicals.

      ### 3. Concealed Security Geography
      Placing high-value documents—passports, boarding passes, currency—against your lumbar back eliminates opportunist theft in crowded market squares.
    `
  },
  {
    id: 'art-2',
    slug: 'the-art-of-minimal-packing',
    title: 'The Art of Minimal Packing: Traveling Lighter for Longer',
    category: 'Philosophy & Lifestyle',
    excerpt: 'Why packing less is the ultimate luxury on the road, and how modular organization creates boundless freedom.',
    date: 'April 18, 2026',
    readTime: '4 min read',
    image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=1000&q=80',
    content: `
      True luxury is unencumbered movement. When you carry only what is essential, you eliminate decision fatigue and create room for spontaneous detours.

      Start by auditing every item: if it does not serve at least two distinct purposes, it stays behind. Natural fibers like merino wool and linen breathe effortlessly and resist odor across multiple days, halving your wardrobe requirements.
    `
  },
  {
    id: 'art-3',
    slug: 'sustainable-materials-in-modern-bags',
    title: 'Sustainable Materials: The Evolution of Recycled Cordura & Leather',
    category: 'Craftsmanship & Materials',
    excerpt: 'How circular manufacturing, vegetable tannins, and ocean-bound plastics are redefining high-end luggage standards.',
    date: 'April 12, 2026',
    readTime: '6 min read',
    image: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=1000&q=80',
    content: `
      For generations, the luxury industry treated synthetic nylon and virgin animal hides as disposable markers of status. Today, engineering advancements allow us to spin discarded fishing nets into 900D fabrics that outperform virgin nylon in tensile tear tests.

      Combined with certified vegetable-tanned leathers utilizing mimosa and chestnut extracts, modern bags can age with grace while leaving minimal environmental debt.
    `
  },
  {
    id: 'art-4',
    slug: 'best-bags-for-your-next-weekend-getaway',
    title: 'Curating the Perfect Weekend Carry: Form Meets Function',
    category: 'Product Stories',
    excerpt: 'A comparative guide between the structured duffle and the expandable travel backpack for 72-hour escapes.',
    date: 'April 3, 2026',
    readTime: '5 min read',
    image: 'https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=1000&q=80',
    content: `
      Whether taking an evening express train to the coast or driving up into the pines for three days of quiet, your carry piece dictates your rhythm. We compare the tactile joy of waxed canvas against high-performance technical ripstop.
    `
  }
];

export const FAQS = [
  {
    category: 'General & Craftsmanship',
    items: [
      {
        q: 'Where are RÓRA bags designed and manufactured?',
        a: 'Our design studio is based in Copenhagen, where prototypes are drafted and stress-tested. Production is carried out in family-owned heritage workshops across Portugal and Northern Italy that meet the highest ethical labor and environmental certifications.'
      },
      {
        q: 'What materials do you use in your products?',
        a: 'We use certified vegetable-tanned Tuscan leather, 100% GOTS organic canvas, recycled 900D ballistic nylon, solid brass hardware, and waterproof YKK Aquaguard zippers.'
      },
      {
        q: 'Are RÓRA bags water-resistant?',
        a: 'Yes. All our canvas and nylon styles are treated with non-toxic, PFC-free durable water repellent (DWR) coatings. Our zippers feature weather seals to keep contents dry in heavy downpours.'
      }
    ]
  },
  {
    category: 'Shipping & Delivery',
    items: [
      {
        q: 'How long does shipping take and what does it cost?',
        a: 'Standard Shipping (3–5 business days) is complimentary across India on all orders over ₹1,999. Express shipping (1–2 business days) is available at checkout for ₹199.'
      },
      {
        q: 'Do you offer nationwide and international shipping?',
        a: 'Yes, we deliver pan-India with express carbon-neutral couriers. All duties and GST are transparently included in the final price.'
      },
      {
        q: 'How can I track my package once dispatched?',
        a: 'Once your order is packed, you will receive an email and SMS with live tracking link. You can also view real-time status in your Account under My Orders.'
      }
    ]
  },
  {
    category: 'Returns & Warranty',
    items: [
      {
        q: 'What is your return policy?',
        a: 'We offer 30-day hassle-free returns on all unused items in original packaging. Return pickup is complimentary and arranged directly from your doorstep.'
      },
      {
        q: 'Do your bags come with a warranty?',
        a: 'Every RÓRA bag carries our Lifetime Craftsmanship Guarantee. If any seam, zipper, or buckle fails under normal use, we will repair or replace it free of charge.'
      },
      {
        q: 'How do I initiate an exchange for a different color?',
        a: 'Simply visit the Returns & Exchanges portal in your account or contact our concierge at hello@rorabags.com with your order number.'
      }
    ]
  }
];

export const REVIEWS = [
  {
    id: 'rev-1',
    author: 'Elena Rostova',
    role: 'Architect & Traveler',
    rating: 5,
    title: 'The cleanest backpack I have ever owned',
    content: 'The Nomad backpack has accompanied me through three countries and daily site visits. The olive tone is stunning in person, and the leather trims have aged beautifully.',
    date: '2 weeks ago',
    verified: true,
    productName: 'The Nomad Backpack'
  },
  {
    id: 'rev-2',
    author: 'Marcus Vance',
    role: 'Creative Director',
    rating: 5,
    title: 'Exceptional craftsmanship and restraint',
    content: 'No loud logos or gimmicks. Just incredible leather, heavy brass zippers, and well-thought-out pockets for my laptop and notebooks. Worth every rupee.',
    date: '1 month ago',
    verified: true,
    productName: 'The Classic Leather Tote'
  },
  {
    id: 'rev-3',
    author: 'Sophie Lindqvist',
    role: 'Photographer',
    rating: 5,
    title: 'Hands-free perfection for city shoots',
    content: 'The Urban Sling holds my mirrorless camera, spare lens, passport, and phone securely. The magnetic buckle is deeply satisfying to use.',
    date: '1 month ago',
    verified: true,
    productName: 'The Urban Sling'
  }
];

export const MOCK_ORDERS = [
  {
    id: 'RRA-89241',
    orderNumber: '#RRA89241',
    date: 'April 28, 2026',
    status: 'Delivered',
    total: 8798,
    items: [
      { id: 'prod-1', name: 'The Nomad Backpack', color: 'Olive Green', price: 4899, quantity: 1, image: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=80' },
      { id: 'prod-2', name: 'The Classic Leather Tote', color: 'Cognac Brown', price: 3899, quantity: 1, image: 'https://images.unsplash.com/photo-1591561954557-26941169b49e?auto=format&fit=crop&w=600&q=80' }
    ],
    shippingAddress: {
      fullName: 'Sarah Johnson',
      street: '142 Bandra West, Hill Road',
      city: 'Mumbai',
      state: 'MH',
      postalCode: '400050',
      country: 'India'
    },
    paymentMethod: 'UPI / Card ending in 4242',
    timeline: [
      { step: 'Order Placed', time: 'Apr 28, 09:14 AM', completed: true },
      { step: 'Payment Verified', time: 'Apr 28, 09:15 AM', completed: true },
      { step: 'Dispatched from Hub', time: 'Apr 29, 02:30 PM', completed: true },
      { step: 'Out for Delivery', time: 'May 01, 08:45 AM', completed: true },
      { step: 'Delivered', time: 'May 01, 01:20 PM', completed: true }
    ]
  },
  {
    id: 'RRA-89105',
    orderNumber: '#RRA89105',
    date: 'April 14, 2026',
    status: 'Shipped',
    total: 6499,
    items: [
      { id: 'prod-4', name: 'The Weekend Duffle', color: 'Safari Olive', price: 6499, quantity: 1, image: 'https://images.unsplash.com/photo-1520006403909-838d6b92c22e?auto=format&fit=crop&w=600&q=80' }
    ],
    shippingAddress: {
      fullName: 'Sarah Johnson',
      street: '142 Bandra West, Hill Road',
      city: 'Mumbai',
      state: 'MH',
      postalCode: '400050',
      country: 'India'
    },
    paymentMethod: 'UPI / Google Pay',
    timeline: [
      { step: 'Order Placed', time: 'Apr 14, 11:20 AM', completed: true },
      { step: 'Payment Verified', time: 'Apr 14, 11:21 AM', completed: true },
      { step: 'Dispatched from Hub', time: 'Apr 15, 04:10 PM', completed: true },
      { step: 'Out for Delivery', time: 'Pending', completed: false },
      { step: 'Delivered', time: 'Pending', completed: false }
    ]
  }
];

export const MOCK_COUPONS = [
  { code: 'RORA10', discountPercent: 10, description: '10% off your entire order', minCart: 1999, uses: 142, expiry: '31 Dec 2026', status: 'Active' },
  { code: 'WELCOME15', discountPercent: 15, description: '15% off first purchase', minCart: 2999, uses: 389, expiry: '30 Nov 2026', status: 'Active' },
  { code: 'JOURNEY20', discountPercent: 20, description: '20% off travel collection', minCart: 4999, uses: 86, expiry: '15 Oct 2026', status: 'Active' },
  { code: 'ARCHITECT25', discountPercent: 25, description: '25% VIP architectural event code', minCart: 7999, uses: 24, expiry: '31 Dec 2026', status: 'Active' }
];

export const MOCK_CUSTOMERS = [
  {
    id: 'cust-1',
    name: 'Sarah Johnson',
    email: 'sarah.j@example.com',
    phone: '+91 98201 44521',
    city: 'Mumbai',
    ordersCount: 5,
    lifetimeValue: 28450,
    tier: 'VIP',
    joinedDate: 'Jan 15, 2025',
    lastOrder: 'April 28, 2026'
  },
  {
    id: 'cust-2',
    name: 'Arjun Mehta',
    email: 'arjun.mehta@designstudio.in',
    phone: '+91 98450 11234',
    city: 'Bengaluru',
    ordersCount: 3,
    lifetimeValue: 16890,
    tier: 'VIP',
    joinedDate: 'Mar 10, 2025',
    lastOrder: 'April 14, 2026'
  },
  {
    id: 'cust-3',
    name: 'Priya Sundaram',
    email: 'priya.s@techventures.co',
    phone: '+91 97110 88921',
    city: 'Delhi NCR',
    ordersCount: 2,
    lifetimeValue: 9798,
    tier: 'Regular',
    joinedDate: 'Jul 22, 2025',
    lastOrder: 'Mar 30, 2026'
  },
  {
    id: 'cust-4',
    name: 'Rohan Kapoor',
    email: 'rohan.k@kapoorarchitects.com',
    phone: '+91 99882 33410',
    city: 'Hyderabad',
    ordersCount: 1,
    lifetimeValue: 6499,
    tier: 'New',
    joinedDate: 'Apr 02, 2026',
    lastOrder: 'Apr 02, 2026'
  },
  {
    id: 'cust-5',
    name: 'Ananya Deshmukh',
    email: 'ananya.d@deshmukhlaw.com',
    phone: '+91 98190 66733',
    city: 'Pune',
    ordersCount: 4,
    lifetimeValue: 21400,
    tier: 'VIP',
    joinedDate: 'Feb 18, 2025',
    lastOrder: 'Apr 20, 2026'
  }
];

export const MOCK_PAYMENTS = [
  {
    id: 'pay-9021',
    orderNumber: '#RRA89241',
    customer: 'Sarah Johnson',
    amount: 8798,
    method: 'UPI / HDFC Bank',
    gatewayRef: 'UPI-98234812391',
    status: 'Captured',
    date: 'Apr 28, 2026 09:15 AM'
  },
  {
    id: 'pay-9020',
    orderNumber: '#RRA89105',
    customer: 'Arjun Mehta',
    amount: 6499,
    method: 'Credit Card (Visa •••• 4242)',
    gatewayRef: 'CARD-TXN-88123',
    status: 'Captured',
    date: 'Apr 14, 2026 11:21 AM'
  },
  {
    id: 'pay-9019',
    orderNumber: '#RRA88940',
    customer: 'Priya Sundaram',
    amount: 4899,
    method: 'UPI / Google Pay',
    gatewayRef: 'UPI-7719234811',
    status: 'Captured',
    date: 'Mar 30, 2026 04:40 PM'
  },
  {
    id: 'pay-9018',
    orderNumber: '#RRA88612',
    customer: 'Ananya Deshmukh',
    amount: 5499,
    method: 'NetBanking / ICICI',
    gatewayRef: 'NB-ICICI-66120',
    status: 'Refunded',
    date: 'Mar 15, 2026 02:10 PM'
  }
];

export const MOCK_SHIPMENTS = [
  {
    id: 'ship-441',
    orderNumber: '#RRA89241',
    customer: 'Sarah Johnson',
    courier: 'Bluedart Express',
    awbNumber: 'BLU-88239014',
    origin: 'Mumbai Central Studio',
    destination: 'Bandra West, Mumbai',
    status: 'Delivered',
    dispatchDate: 'Apr 29, 2026',
    deliveryDate: 'May 01, 2026'
  },
  {
    id: 'ship-440',
    orderNumber: '#RRA89105',
    customer: 'Arjun Mehta',
    courier: 'Delhivery Surface',
    awbNumber: 'DEL-99120481',
    origin: 'Mumbai Central Studio',
    destination: 'Indiranagar, Bengaluru',
    status: 'In Transit',
    dispatchDate: 'Apr 15, 2026',
    deliveryDate: 'Apr 18, 2026 (Est)'
  },
  {
    id: 'ship-439',
    orderNumber: '#RRA88940',
    customer: 'Priya Sundaram',
    courier: 'DTDC Priority Air',
    awbNumber: 'DTD-10924822',
    origin: 'Mumbai Central Studio',
    destination: 'Vasant Vihar, New Delhi',
    status: 'Delivered',
    dispatchDate: 'Mar 31, 2026',
    deliveryDate: 'Apr 02, 2026'
  }
];

export const MOCK_RETURNS = [
  {
    id: 'ret-104',
    orderNumber: '#RRA88612',
    customer: 'Ananya Deshmukh',
    item: 'The Executive Briefcase (Chestnut Brown)',
    reason: 'Size / Laptop fit requirement changed',
    requestDate: 'Mar 18, 2026',
    inspectionStatus: 'Passed (Pristine Condition)',
    status: 'Approved & Refunded',
    amount: 5499
  },
  {
    id: 'ret-105',
    orderNumber: '#RRA89012',
    customer: 'Kavita Roy',
    item: 'The Minimalist Crossbody (Taupe)',
    reason: 'Color tone preference',
    requestDate: 'Apr 22, 2026',
    inspectionStatus: 'Awaiting Hub Delivery',
    status: 'Under Review',
    amount: 2899
  }
];

export const MOCK_REFUNDS = [
  {
    id: 'ref-801',
    returnRef: 'ret-104',
    orderNumber: '#RRA88612',
    customer: 'Ananya Deshmukh',
    amount: 5499,
    method: 'Source Bank (ICICI NetBanking)',
    transactionRef: 'REF-ICICI-99231',
    status: 'Completed',
    date: 'Mar 20, 2026'
  },
  {
    id: 'ref-800',
    returnRef: 'ret-099',
    orderNumber: '#RRA87440',
    customer: 'Vikram Seth',
    amount: 3899,
    method: 'UPI / PhonePe',
    transactionRef: 'REF-UPI-440129',
    status: 'Completed',
    date: 'Feb 12, 2026'
  }
];

export const MOCK_ADMIN_USERS = [
  {
    id: 'usr-1',
    name: 'Sarah Jenkins',
    email: 'sarah.jenkins@rorastudios.com',
    role: 'Super Admin',
    lastActive: 'Just now',
    status: 'Active'
  },
  {
    id: 'usr-2',
    name: 'Kabir Verma',
    email: 'kabir.v@rorastudios.com',
    role: 'Store Manager',
    lastActive: '14 mins ago',
    status: 'Active'
  },
  {
    id: 'usr-3',
    name: 'Meera Rao',
    email: 'meera.r@rorastudios.com',
    role: 'Customer Support Lead',
    lastActive: '1 hour ago',
    status: 'Active'
  },
  {
    id: 'usr-4',
    name: 'David Chen',
    email: 'david.c@rorastudios.com',
    role: 'Content & CMS Editor',
    lastActive: 'Yesterday',
    status: 'Active'
  }
];

export const MOCK_ROLES = [
  {
    id: 'role-1',
    name: 'Super Admin',
    description: 'Full unrestricted access to all store catalog, financials, admin accounts, and settings.',
    usersCount: 1,
    permissions: ['Products (Full)', 'Orders (Full)', 'Financials (Full)', 'Settings (Full)', 'Users (Full)']
  },
  {
    id: 'role-2',
    name: 'Store Manager',
    description: 'Manage catalog, inventory restocks, order dispatches, promotions, and customer relations.',
    usersCount: 2,
    permissions: ['Products (Full)', 'Orders (Full)', 'Inventory (Full)', 'Coupons (Full)', 'Analytics (View)']
  },
  {
    id: 'role-3',
    name: 'Customer Support',
    description: 'Order tracking, returns & refunds processing, customer concierge inquiries, and review moderation.',
    usersCount: 3,
    permissions: ['Orders (View/Update)', 'Returns (Process)', 'Reviews (Moderate)', 'Customers (View)']
  },
  {
    id: 'role-4',
    name: 'Content & CMS Editor',
    description: 'Editorial journal publishing, homepage banners, brand stories, and product copywriting.',
    usersCount: 2,
    permissions: ['Journal (Full)', 'CMS Banners (Full)', 'Product Content (Edit)']
  }
];

export const MOCK_SETTINGS = {
  storeName: 'RÓRA Studios',
  tagline: 'Thoughtfully Designed Bags for Modern Journeys',
  currency: 'INR (₹)',
  supportEmail: 'concierge@rorastudios.com',
  supportPhone: '+91 22 6944 8000',
  warehouseAddress: 'Studio 4B, Mathuradas Mills Compound, Lower Parel, Mumbai 400013, India',
  freeShippingThreshold: 1999,
  standardShippingFee: 199,
  taxRate: '18% GST (Included in MRP)',
  orderPrefix: 'RRA',
  inventoryAlertThreshold: 5
};

export const MOCK_CMS = {
  announcementBar: {
    enabled: true,
    text: 'Complimentary shipping across India on orders over ₹1,999 • Handcrafted with certified recycled textiles'
  },
  heroBanner: {
    eyebrow: 'Architectural Carry • Autumn 2026',
    title: 'Engineered for the Modern Journey',
    subtitle: 'Tactile Japanese recycled nylon and full-grain Italian leather, crafted in small artisanal batches with zero compromise.',
    primaryButtonText: 'Explore Collection',
    secondaryButtonText: 'Read the Journal'
  },
  craftsmanshipFeature: {
    heading: 'Architectural Restraint Meets Uncompromising Craft',
    paragraph1: 'Every RÓRA silhouette begins as a mathematical exercise in volume, balance, and tactile reduction. We eliminate unnecessary ornamentation to let premium materials and ergonomic geometry shine.',
    paragraph2: 'Hand-burnished leather edges, custom anodized matte hardware, and weatherproof stormproof zippers ensure a lifetime of faithful companion carry.'
  }
};

export const MOCK_AUDIT_LOGS = [
  { id: 1, action: 'Product Price Updated', user: 'Sarah Jenkins (Super Admin)', entity: 'The Nomad Backpack (₹4,899)', timestamp: '12 mins ago', severity: 'Info' },
  { id: 2, action: 'Order Dispatched', user: 'Logistics Service', entity: 'Order #RRA89241 via Bluedart', timestamp: '1 hour ago', severity: 'Success' },
  { id: 3, action: 'Coupon Code Created', user: 'Kabir Verma (Store Manager)', entity: 'JOURNEY20 (20% Off)', timestamp: '3 hours ago', severity: 'Info' },
  { id: 4, action: 'Inventory Restocked', user: 'Warehouse Manager', entity: '+50 The Classic Leather Tote', timestamp: 'Yesterday 04:15 PM', severity: 'Success' },
  { id: 5, action: 'Return Approved', user: 'Meera Rao (Support Lead)', entity: 'Return #RET-104 (₹5,499)', timestamp: '2 days ago', severity: 'Warning' },
  { id: 6, action: 'Review Published', user: 'David Chen (Content Editor)', entity: '5★ Review by Elena Rostova', timestamp: '3 days ago', severity: 'Info' },
  { id: 7, action: 'Store Setting Changed', user: 'Sarah Jenkins (Super Admin)', entity: 'Free Shipping Threshold set to ₹1,999', timestamp: '5 days ago', severity: 'Warning' }
];

export const MOCK_SALES_OVERVIEW = {
  monthlyRevenue: '₹28,45,900',
  monthlyGrowth: '+18.4%',
  ordersThisMonth: 1428,
  ordersGrowth: '+12.1%',
  activeCustomers: 3890,
  customersGrowth: '+8.5%',
  averageOrderValue: '₹3,420',
  aovGrowth: '+4.2%',
  categoryBreakdown: [
    { category: 'Backpacks', percent: 38, revenue: '₹10,81,442' },
    { category: 'Travel Bags', percent: 24, revenue: '₹6,83,016' },
    { category: 'Laptop Bags', percent: 18, revenue: '₹5,12,262' },
    { category: 'Tote & Handbags', percent: 12, revenue: '₹3,41,508' },
    { category: 'Slings & Campus', percent: 8, revenue: '₹2,27,672' }
  ]
};

