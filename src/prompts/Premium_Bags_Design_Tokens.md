# Premium Bags E-Commerce — Design Tokens & UI Direction

> **Purpose:** Master visual/design-token specification for Antigravity implementation.
>
> **Design reference:** Piccollo — Backpack E-Commerce Landing Page (Dribbble), used only as visual inspiration.
>
> **Important:** Do NOT clone the reference. The final website must have its own brand identity, copy, layouts, components, product photography and interactions.
>
> **Core rule:** The website must feel like a professionally designed premium fashion/lifestyle e-commerce brand — **not AI-generated, not generic, not SaaS-like, and not template-like.**

---

## 1. Design Principles

1. Product photography is the visual hero.
2. Premium editorial composition over generic e-commerce grids.
3. Warm, earthy, neutral visual language.
4. Strong serif display typography + clean sans-serif UI typography.
5. Generous whitespace.
6. Thin dividers and restrained borders.
7. Minimal corner rounding.
8. Minimal shadows.
9. Motion should feel calm, intentional and physical.
10. Every decorative element must have a reason.
11. Never add UI just because it is trendy.
12. No visible AI-first visual language.
13. Mobile-first implementation.
14. Accessibility and readability always override decoration.
15. Performance is a first-class requirement.

---

# 2. Color Tokens

The reference direction and project document establish:
- Warm off-white
- Soft beige
- Charcoal / near-black
- Taupe
- Muted brown
- Olive / muted green as a selective accent

The following HEX values are the **implementation palette** derived from that visual direction. They are starting tokens and can be tuned after the final brand identity is approved.

## 2.1 Core Neutrals

| Token | HEX | Usage |
|---|---|---|
| `--color-ivory-50` | `#F7F4EE` | Main page background |
| `--color-ivory-100` | `#EEEAE1` | Secondary page sections |
| `--color-cream-200` | `#E6E0D5` | Cards / image backgrounds |
| `--color-beige-300` | `#D8CFC1` | Borders / subtle surfaces |
| `--color-taupe-400` | `#B9AD9D` | Muted UI / secondary text |
| `--color-taupe-500` | `#9A8D7D` | Secondary labels |
| `--color-brown-600` | `#715B49` | Supporting brand tone |
| `--color-brown-700` | `#594637` | Dark brown details |
| `--color-charcoal-900` | `#1E1D1A` | Primary text |
| `--color-black-950` | `#11110F` | Strong CTA / maximum contrast |

## 2.2 Accent

| Token | HEX | Usage |
|---|---|---|
| `--color-olive-500` | `#68705A` | Primary accent |
| `--color-olive-600` | `#555E48` | Hover / active accent |
| `--color-olive-700` | `#424A38` | Dark accent |
| `--color-sand-400` | `#C9B99F` | Optional highlight |

### Accent rule

Olive must be **selective**.

Do NOT make the whole website green.

Use olive for:
- selected filters
- small status indicators
- subtle category accents
- wishlist states
- small editorial details
- occasional CTA secondary states

Primary CTAs should generally remain charcoal/near-black.

---

# 3. Semantic Color Tokens

```css
:root {
  --background-primary: #F7F4EE;
  --background-secondary: #EEEAE1;
  --background-tertiary: #E6E0D5;

  --surface-primary: #FAF8F3;
  --surface-secondary: #F0ECE4;
  --surface-dark: #1E1D1A;

  --text-primary: #1E1D1A;
  --text-secondary: #715B49;
  --text-muted: #9A8D7D;
  --text-inverse: #F7F4EE;

  --border-subtle: #D8CFC1;
  --border-default: #C9BFB1;
  --border-strong: #9A8D7D;

  --accent-primary: #68705A;
  --accent-hover: #555E48;

  --success: #68705A;
  --warning: #A17C45;
  --error: #8A4D43;
  --info: #5F7070;
}
```

### Status colors

Status colors must be muted and sophisticated.

Avoid:
- neon green
- bright red
- electric blue
- saturated purple

Use status colors primarily for:
- order status
- stock status
- payment state
- form validation

---

# 4. Typography System

## 4.1 Typography direction

The project requires:

### Display
A premium editorial serif for:
- Hero headlines
- Page titles
- Editorial statements
- Brand storytelling
- Large promotional headings

### UI / Body
A clean modern sans-serif for:
- Navigation
- Product names
- Prices
- Filters
- Forms
- Buttons
- Checkout
- Admin UI
- Supporting text

## 4.2 Recommended font pairing

### Display Font

**Cormorant Garamond**

```css
font-family: "Cormorant Garamond", serif;
```

Use:
- `500`
- `600`

Do not use extremely thin weights for important text.

### UI / Body Font

**Inter**

```css
font-family: "Inter", sans-serif;
```

Use:
- `400`
- `500`
- `600`

Do not use excessive font weights.

> These are implementation recommendations. If the final brand identity selects another editorial serif and neutral sans-serif pairing, update the tokens globally rather than mixing fonts component-by-component.

---

# 5. Typography Tokens

```css
:root {
  --font-display: "Cormorant Garamond", serif;
  --font-body: "Inter", sans-serif;

  --text-display-xl: clamp(3.5rem, 7vw, 7rem);
  --text-display-lg: clamp(3rem, 5.5vw, 5.5rem);
  --text-display-md: clamp(2.4rem, 4vw, 4rem);

  --text-heading-xl: clamp(2.25rem, 4vw, 3.5rem);
  --text-heading-lg: clamp(1.9rem, 3vw, 2.75rem);
  --text-heading-md: clamp(1.5rem, 2vw, 2rem);

  --text-body-lg: 1.125rem;
  --text-body-md: 1rem;
  --text-body-sm: 0.875rem;

  --text-caption: 0.75rem;
  --text-label: 0.6875rem;
}
```

## Typography behavior

### Hero heading
- Serif
- Large
- Tight but readable line-height
- Maximum 2–4 lines
- Avoid giant text covering the entire screen

### Section heading
- Serif
- Medium/large
- Short and editorial

### Body
- Sans-serif
- Comfortable line height
- Maximum reading width

### Product name
- Sans-serif
- Medium weight
- Never overly bold

### Price
- Sans-serif
- Medium/semibold
- High contrast

### Labels
- Sans-serif
- Small
- Slight letter spacing
- Uppercase only when useful

---

# 6. Letter Spacing

```css
--tracking-display: -0.025em;
--tracking-heading: -0.015em;
--tracking-body: 0;
--tracking-label: 0.08em;
--tracking-uppercase: 0.12em;
```

Avoid exaggerated letter spacing.

---

# 7. Line Height

```css
--leading-display: 0.92;
--leading-heading: 1.05;
--leading-tight: 1.15;
--leading-normal: 1.5;
--leading-relaxed: 1.7;
```

Editorial headlines can be tight.

Body copy must remain comfortable to read.

---

# 8. Spacing System

Use a consistent 4px base system.

```css
--space-1: 4px;
--space-2: 8px;
--space-3: 12px;
--space-4: 16px;
--space-5: 20px;
--space-6: 24px;
--space-8: 32px;
--space-10: 40px;
--space-12: 48px;
--space-16: 64px;
--space-20: 80px;
--space-24: 96px;
--space-32: 128px;
--space-40: 160px;
```

## Section spacing

Desktop:
- Small section: `64–96px`
- Standard section: `96–128px`
- Major editorial section: `128–160px`

Mobile:
- Small section: `40–56px`
- Standard section: `56–80px`
- Major section: `80–104px`

Do not create excessive empty space that harms product discovery.

---

# 9. Layout / Grid

## Desktop

Maximum content width:

```css
--container-max: 1440px;
```

Recommended side padding:
- Large desktop: `48–64px`
- Laptop: `32–48px`

## Tablet

- Side padding: `24–32px`

## Mobile

- Side padding: `16–20px`

## Grid

Desktop product grid:
- 3–4 columns depending on context

Tablet:
- 2–3 columns

Mobile:
- 2 columns for product browsing
- 1 column for editorial content
- 1 column for checkout forms

Avoid oversized rounded product cards.

---

# 10. Border Radius

The reference direction is refined and editorial, not heavily rounded.

```css
--radius-none: 0px;
--radius-sm: 2px;
--radius-md: 4px;
--radius-lg: 8px;
--radius-pill: 999px;
```

### Rules

Use:
- `0–4px` for most cards and controls
- `8px` only where interaction benefits from softer corners
- Pill shape only for compact tags/statuses

Do NOT use:
- 16px–32px radius everywhere
- giant pill buttons
- overly rounded dashboard cards

---

# 11. Borders

Use thin, low-contrast borders.

```css
--border-width-thin: 1px;
--border-width-strong: 1px;
```

Preferred:

```css
border: 1px solid var(--border-subtle);
```

Borders should structure the page without becoming visually dominant.

---

# 12. Shadows

The website should feel flat, tactile and editorial.

Avoid floating SaaS-style cards.

```css
--shadow-none: none;
--shadow-soft: 0 8px 30px rgba(30, 29, 26, 0.06);
--shadow-medium: 0 16px 45px rgba(30, 29, 26, 0.10);
```

Use shadows only for:
- drawers
- dropdowns
- modals
- floating navigation when required

Product cards generally do not need shadows.

---

# 13. Buttons

## Primary Button

Appearance:
- Near-black/charcoal background
- Ivory text
- Square/slightly rounded corners
- Compact editorial proportions

```css
background: #1E1D1A;
color: #F7F4EE;
border-radius: 2px;
```

Hover:
- Slight background transition
- Subtle upward movement or underline treatment

Do NOT:
- use gradients
- use glowing effects
- use oversized pill buttons

## Secondary Button

- Transparent / ivory background
- Charcoal border
- Charcoal text

## Text Link

Use:
- underline on hover
- arrow/chevron when appropriate

Example:

`Explore Collection →`

---

# 14. Header

The header should be minimal.

### Desktop

Left:
- Brand logo / wordmark

Center:
- Shop
- Collections
- About
- Journal

Right:
- Search
- Account
- Wishlist
- Cart

### Mobile

Left:
- Menu

Center:
- Brand

Right:
- Search
- Cart

Do not overload the header.

Header should remain visually quiet so products remain the hero.

---

# 15. Hero Section

The hero is one of the most important brand areas.

## Structure

- Full-width editorial image/video
- Large serif headline
- Short supporting copy
- One primary CTA
- Optional secondary text link
- Minimal navigation
- Subtle motion

### Recommended composition

```text
[large lifestyle/product image]

small eyebrow
large serif headline
short supporting copy
[Explore Collection →]
```

### Rules

- Photography must feel premium and realistic.
- Do not use generic stock-looking imagery.
- Do not use abstract AI-style gradients.
- Do not place excessive floating UI elements over the image.
- Text must remain readable.
- Keep CTA count low.

---

# 16. Product Card

Product cards must feel like fashion/catalog presentation.

## Required

- Product image
- Product name
- Price
- Optional original price
- Optional color/variant indicator
- Wishlist control
- Optional small badge

## Interaction

Hover:
- image transition to second image when available
- subtle zoom, approximately `1.02–1.04`
- wishlist control appears or becomes emphasized

Do NOT:
- animate every element
- add excessive badges
- use glowing hover effects
- add fake AI recommendation labels

---

# 17. Product Image Treatment

Product photography is critical.

Preferred:
- warm neutral backgrounds
- natural shadows
- editorial lifestyle photography
- consistent product scale
- consistent image ratios

Recommended image ratios:

```css
--image-product: 4 / 5;
--image-editorial: 4 / 3;
--image-hero: 16 / 9;
--image-square: 1 / 1;
```

Do not stretch product images.

---

# 18. Product Details Page

Layout should feel editorial but remain practical.

### Desktop

Left:
- Large product gallery

Right:
- Product name
- Rating
- Price
- Color
- Size/capacity
- Quantity
- Add to Cart
- Buy Now
- Delivery information
- Return information

Below:
- Description
- Features
- Specifications
- Reviews
- Related products
- Lifestyle storytelling

### Mobile

Order:

1. Gallery
2. Product name
3. Price
4. Rating
5. Variant
6. Quantity
7. Add to Cart
8. Buy Now
9. Delivery/returns
10. Details
11. Reviews
12. Related products

---

# 19. Filters

Filters should be functional and visually quiet.

Categories:
- Bag Type
- Price
- Brand
- Color
- Material
- Capacity
- Size
- Rating
- Availability
- Use Case

Desktop:
- left filter rail

Mobile:
- filter drawer / bottom sheet

Do not permanently consume too much mobile screen space.

---

# 20. Cart

Cart should be clean and practical.

Each item:
- product thumbnail
- name
- variant
- price
- quantity
- remove
- move to wishlist

Summary:
- subtotal
- discount
- tax
- shipping
- total

CTA:
- Proceed to Checkout

Financial calculations must come from the backend.

---

# 21. Checkout

Checkout should deliberately become less decorative and more functional.

Steps:

```text
1. Shipping
2. Payment
3. Confirmation
```

Use:
- clear form labels
- strong hierarchy
- visible order summary
- simple payment choices
- validation messages
- secure payment messaging

Do not add unnecessary animations during checkout.

---

# 22. Account UI

Account pages should use a quiet editorial dashboard rather than a generic SaaS dashboard.

Navigation:
- Profile
- Addresses
- Orders
- Wishlist
- Reviews
- Returns & Refunds
- Settings
- Logout

Use thin dividers and simple lists.

---

# 23. Wishlist

Wishlist should prioritize product discovery.

Each item:
- image
- name
- price
- color
- availability
- remove
- Add to Cart

Include a restrained:
`Recently Viewed`

section where useful.

---

# 24. Order History

Use a clean chronological list.

Each order:
- order number
- date
- products
- amount
- status
- View Details

Status colors must remain muted.

---

# 25. About / Brand Story

This page should feel like a fashion/lifestyle editorial.

Suggested structure:

1. Large image hero
2. Brand statement
3. Origin story
4. Craftsmanship
5. Materials
6. Values
7. Lifestyle photography
8. Final CTA

Use large serif statements and generous whitespace.

---

# 26. Journal

The Journal is an editorial content surface.

Categories:
- Travel
- Materials
- Craft
- Styling
- Product stories
- Brand stories

Card style:
- large photography
- serif article title
- small metadata
- short excerpt

No AI-generated article badges or content language.

---

# 27. FAQ

Simple accordion layout.

Use:
- large serif page title
- category navigation
- thin horizontal separators
- plus/minus interaction

Do not put FAQ inside giant rounded cards.

---

# 28. Contact

Use a calm two-column desktop layout.

Left:
- introduction
- email
- phone
- address
- social links

Right:
- name
- email
- message
- submit

Keep form styling minimal.

---

# 29. Returns & Refunds

Present policies as readable information, not a dashboard.

Use:
- section headings
- numbered steps
- icons only when useful
- concise explanations
- contact support CTA

---

# 30. Footer

Footer should be spacious but restrained.

Columns:

### Shop
- All Bags
- Backpacks
- Laptop Bags
- Handbags
- Travel Bags
- Tote Bags

### About
- Our Story
- Journal
- Materials
- Contact

### Help
- FAQ
- Shipping
- Returns
- Privacy
- Terms

### Newsletter
- short invitation
- email field
- Subscribe

Bottom:
- copyright
- payment icons
- social links

---

# 31. Icons

Icon style:
- minimal
- thin stroke
- consistent stroke width
- no colorful emoji icons

Preferred:
- Lucide-style outline icons
- 1.5px–2px stroke

Use icons only where they improve recognition.

---

# 32. Motion Tokens

Motion must be sophisticated and restrained.

```css
--duration-fast: 180ms;
--duration-normal: 300ms;
--duration-slow: 600ms;
--duration-editorial: 900ms;

--ease-standard: cubic-bezier(0.2, 0.7, 0.2, 1);
--ease-smooth: cubic-bezier(0.16, 1, 0.3, 1);
```

## Use GSAP for

- hero reveal
- text reveal
- image reveal
- scroll storytelling
- section transitions
- product transitions
- editorial animations

## Use ScrollTrigger for

- image reveals
- subtle parallax
- storytelling sequences

## Use Lenis for

- smooth scrolling

## Motion limits

Do not:
- animate every card
- animate every text element
- use constant floating objects
- use infinite decorative motion
- delay basic shopping interactions
- create motion that harms accessibility

---

# 33. Hover / Interaction Tokens

Product image:

```css
transform: scale(1.02);
```

Recommended interaction duration:

```css
transition: 300ms cubic-bezier(0.2, 0.7, 0.2, 1);
```

Buttons:
- background transition
- subtle translateY(-1px) where appropriate

Wishlist:
- subtle heart/outline transition

Cart:
- short confirmation animation

---

# 34. 3D Rules

3D is optional and selective.

Use only when it improves:
- product presentation
- hero storytelling
- interactive product visualization

Avoid:
- 3D everywhere
- heavy scenes on mobile
- decorative 3D without purpose
- slow-loading hero scenes

On lower-powered devices:
- reduce or disable heavy 3D

---

# 35. Mobile-First Rules

Implementation order:

```text
Mobile
↓
Tablet
↓
Laptop
↓
Desktop
```

Mobile priorities:

1. Fast browsing
2. Thumb-friendly controls
3. Easy search
4. Simple filters
5. Product image clarity
6. Clear price
7. Easy cart access
8. Simple checkout
9. Readable typography
10. Performance

Do not simply shrink desktop layouts.

---

# 36. Responsive Breakpoints

Recommended starting tokens:

```css
--breakpoint-sm: 640px;
--breakpoint-md: 768px;
--breakpoint-lg: 1024px;
--breakpoint-xl: 1280px;
--breakpoint-2xl: 1440px;
```

These are implementation defaults and can be adjusted based on actual component behavior.

---

# 37. Accessibility

Required:

- WCAG-conscious contrast
- keyboard navigation
- visible focus states
- semantic HTML
- meaningful alt text
- accessible form labels
- touch targets approximately 44px or larger
- reduced-motion support
- no information conveyed only through color

Example:

```css
@media (prefers-reduced-motion: reduce) {
  /* Disable or simplify non-essential motion */
}
```

---

# 38. Anti-AI-Slop Rules

This section is **mandatory**.

### Never use

- AI chatbot UI
- AI shopping assistant
- AI recommendation labels
- “Ask AI”
- glowing gradients
- purple/blue AI gradients
- excessive glassmorphism
- generic floating dashboard cards
- random blobs
- excessive pills
- excessive rounded cards
- neon accents
- unnecessary 3D
- excessive animated text
- fake futuristic UI
- generic SaaS dashboard aesthetics
- decorative elements without purpose

### If a component looks AI-generated:

**Redesign it.**

Ask:

> Does this element improve shopping, storytelling, navigation, trust, or brand identity?

If the answer is no, remove it.

---

# 39. Design Quality Test

Before accepting any page, check:

### Brand
- Does it look like a premium bags/fashion brand?
- Does it have a distinct identity?
- Does it avoid template aesthetics?

### Product
- Is the bag the visual hero?
- Are images high quality?
- Is product information easy to understand?

### Layout
- Is whitespace intentional?
- Is hierarchy clear?
- Are sections visually balanced?

### Typography
- Is the serif used editorially?
- Is the sans-serif used for functional information?
- Is hierarchy consistent?

### Color
- Is the warm neutral palette dominant?
- Is olive used selectively?
- Are contrast levels accessible?

### Motion
- Does motion improve understanding?
- Is it smooth?
- Does it avoid slowing down shopping?

### Mobile
- Does the page work naturally with one hand?
- Are controls easy to tap?
- Is product browsing fast?

### AI Slop
- Does anything look like a generic AI-generated website?

If yes → redesign before approval.

---

# 40. Component Naming Convention

Use semantic component names.

```text
SiteHeader
HeroSection
CollectionGrid
ProductCard
ProductGallery
ProductInfo
FilterPanel
SearchOverlay
CartDrawer
CartItem
OrderSummary
CheckoutForm
ReviewList
ReviewCard
BrandStory
JournalCard
FaqAccordion
NewsletterSection
SiteFooter
```

Avoid meaningless names such as:

```text
CoolCard
ModernBox
AICard
MagicSection
GlassPanel
FancyHero
```

---

# 41. Design Token Implementation

Create one central token file.

Example:

```text
src/
  styles/
    tokens.css
    typography.css
    globals.css
```

All components must consume tokens.

Do not hard-code random colors throughout components.

Bad:

```css
color: #73634F;
```

Good:

```css
color: var(--text-secondary);
```

---

# 42. Final Visual Direction

The final website should communicate:

**Premium**
**Modern**
**Minimal**
**Editorial**
**Human**
**Functional**
**Travel-oriented**
**Fashion/lifestyle**
**High quality**

The product remains the hero.

The technology should remain invisible.

The website should feel like it was designed by a strong human product/design team — not generated from an AI template.

---

# 43. Implementation Priority

Antigravity should implement in this order:

```text
1. Design Tokens
2. Typography
3. Global Layout
4. Header / Navigation
5. Buttons / Inputs / Core Components
6. Homepage
7. Product Listing
8. Product Details
9. Search / Filters
10. Wishlist
11. Cart
12. Checkout
13. Account / Orders
14. Brand Story
15. Journal
16. FAQ / Contact / Policies
17. Admin UI
18. Responsive refinement
19. Motion
20. Performance
21. Accessibility
22. Final visual QA
```

Every stage must be tested before moving to the next.

---

# 44. Non-Negotiable Final Rule

> **Do not optimize for “wow, this looks AI-generated.”**
>
> Optimize for:
>
> **“This looks like a real premium bags brand.”**

The website should feel:
- intentional
- restrained
- editorial
- tactile
- premium
- original
- easy to shop
- memorable

and **never generic or AI-slop.**
