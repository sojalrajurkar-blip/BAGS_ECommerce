# RÓRA — Antigravity Frontend Implementation Agent Guide

## Purpose

This document is the implementation guide for building the RÓRA premium bags e-commerce frontend with Antigravity.

The goal is to convert the approved RÓRA product requirements, design direction, prototype findings, production architecture, and motion architecture into a clean, production-oriented Next.js frontend.

This phase is **frontend-first**.

Do not implement the backend, authentication, payments, Supabase, deployment, or production API integration yet.

---

# 1. SOURCE OF TRUTH

Before writing code, inspect and respect these project documents/files:

1. RÓRA current conversation / project requirements document
2. `ecommerce.md`
3. `motion.md`
4. Existing design-token document
5. Existing prototype
6. Existing `prompts/` files
7. Existing `design_reference/` files
8. Existing assets
9. Existing source code and routes

Priority:

```text
Approved RÓRA requirements
        ↓
Design Tokens
        ↓
Prototype / Design References
        ↓
ecommerce.md
        ↓
motion.md
        ↓
Implementation decisions
```

Do not invent a new visual direction when an approved design token or reference already exists.

---

# 2. IMPORTANT WORKING RULE

## DO NOT START CODING IMMEDIATELY

First perform a complete repository audit.

Inspect:

- folder structure
- package.json
- framework/version
- existing Next.js setup
- existing React components
- existing routes
- existing layouts
- design tokens
- CSS/Tailwind configuration
- image/assets
- fonts
- prototype
- reusable components
- existing mock data
- existing state management
- existing animation code
- existing accessibility patterns
- existing responsive behavior

Then produce a short implementation report containing:

- Current state
- What can be reused
- What needs refactoring
- What is missing
- Architecture risks
- Implementation order

Only after this audit should implementation begin.

---

# 3. CURRENT IMPLEMENTATION SCOPE

Build:

- Customer-facing storefront
- Reusable UI/component system
- Responsive layouts
- Product discovery experience
- Product detail experience
- Wishlist UI
- Cart UI
- Checkout UI
- Account UI
- Orders UI
- Content pages
- Admin-ready frontend architecture
- CMS-ready structures
- Future API-ready data boundaries
- SEO-ready page structure
- Accessibility
- Performance
- Premium motion system

Do NOT build yet:

- Spring Boot backend
- PostgreSQL integration
- Supabase
- Google OAuth
- JWT/session authentication
- Payment gateway
- Real order processing
- Real email service
- Production deployment
- Real AI APIs

Use realistic local/mock data with clean typed interfaces.

The frontend must later be able to replace mock data with Spring Boot REST APIs without rebuilding the UI.

---

# 4. TARGET TECHNOLOGY

Use:

- Next.js
- React
- TypeScript
- App Router
- Modern CSS/Tailwind only where appropriate
- Semantic HTML
- Server Components by default
- Client Components only when interaction requires them
- GSAP for advanced animation
- GSAP ScrollTrigger for meaningful scroll animation
- Lenis for smooth scrolling
- CSS transitions for micro-interactions

Do not introduce unnecessary libraries.

If an additional dependency is genuinely required, explain why before adding it.

---

# 5. ARCHITECTURE PRINCIPLE

Use this architecture:

```text
Next.js App
│
├── App Router
│
├── Layout / Navigation
│
├── Design System
│
├── Shared Components
│
├── Feature Components
│
├── Page Routes
│
├── Domain Models / Types
│
├── Mock Data / Repository Layer
│
├── Animation System
│
├── SEO / Metadata
│
└── Accessibility / Performance
```

The frontend should have clear boundaries between:

```text
UI
↓
Domain Models
↓
Data Access Boundary
↓
Future REST API
```

Do not couple UI components directly to future backend implementation details.

---

# 6. RECOMMENDED PROJECT STRUCTURE

Adapt this structure to the existing repository rather than blindly replacing it.

```text
src/
├── app/
│   ├── layout.tsx
│   ├── page.tsx
│   ├── shop/
│   ├── category/
│   ├── product/
│   ├── search/
│   ├── wishlist/
│   ├── cart/
│   ├── checkout/
│   ├── confirmation/
│   ├── account/
│   ├── orders/
│   ├── about/
│   ├── journal/
│   ├── faq/
│   ├── contact/
│   ├── returns/
│   ├── shipping/
│   ├── admin/
│   └── not-found.tsx
│
├── components/
│   ├── layout/
│   ├── navigation/
│   ├── product/
│   ├── commerce/
│   ├── forms/
│   ├── content/
│   ├── feedback/
│   └── admin/
│
├── features/
│   ├── catalog/
│   ├── search/
│   ├── wishlist/
│   ├── cart/
│   ├── checkout/
│   ├── account/
│   └── admin/
│
├── data/
│   ├── mock/
│   └── repositories/
│
├── types/
│
├── lib/
│   ├── utils/
│   ├── seo/
│   └── accessibility/
│
├── animations/
│   ├── lenis/
│   ├── gsap/
│   ├── scroll-trigger/
│   ├── reveal/
│   ├── product/
│   └── modal/
│
├── styles/
│
└── assets/
```

If the existing project already has a good structure, improve it incrementally instead of replacing it.

---

# 7. DESIGN SYSTEM

The approved RÓRA design tokens are the source of truth.

Preserve:

- Typography
- Font families
- Font weights
- Font sizes
- Line heights
- Colors
- Semantic colors
- Spacing
- Container widths
- Breakpoints
- Border radius
- Shadows
- Motion tokens

RÓRA should feel:

- Premium
- Editorial
- Minimal
- Warm
- Sophisticated
- Calm
- Product-focused

Typography direction:

- Cormorant Garamond for editorial/display moments
- Inter for body/UI

Do not randomly change:

- colors
- typography
- spacing
- radius
- breakpoints
- button styles

If a token appears incorrect, verify it against the approved design-token document before changing it.

---

# 8. IMAGE / ASSET ARCHITECTURE

Do not scatter direct image URLs throughout components.

Create a centralized image/asset registry or typed asset structure.

Example concept:

```text
Product
 ├── heroImage
 ├── gallery[]
 ├── thumbnail
 └── lifestyleImages[]
```

The UI should consume typed product data instead of hardcoded URLs.

Product imagery is central to the RÓRA experience.

---

# 9. IMPLEMENTATION ORDER

Follow this sequence unless the audit shows a strong reason to change it.

```text
1. Repository Audit
        ↓
2. Next.js Foundation
        ↓
3. Design Token Integration
        ↓
4. Global Layout
        ↓
5. Header / Navigation
        ↓
6. Footer
        ↓
7. Motion Foundation
        ↓
8. Homepage
        ↓
9. Shop / Category / Listing
        ↓
10. Search
        ↓
11. Product Details
        ↓
12. Wishlist
        ↓
13. Cart
        ↓
14. Checkout UX
        ↓
15. Confirmation
        ↓
16. Account
        ↓
17. Orders / Order Details
        ↓
18. Content Pages
        ↓
19. Admin-ready Architecture
        ↓
20. SEO / Accessibility / Performance
        ↓
21. Testing / Validation
        ↓
22. Final Refinement
```

Do not build everything in one giant change.

After each meaningful phase:

```text
Implement
→ Run checks
→ Inspect visually
→ Fix issues
→ Report result
→ Continue
```

---

# 10. PAGE IMPLEMENTATION REQUIREMENTS

## Homepage

Build a premium editorial commerce homepage.

Include appropriate sections such as:

- Hero
- Featured products
- Editorial storytelling
- Category discovery
- Brand story
- Promotional content
- Reviews/social proof
- Newsletter
- Footer

Motion should guide attention, not delay access.

---

## Shop / Category

Include:

- Product grid
- Sorting
- Filters
- Categories
- Product cards
- Wishlist interaction
- Responsive layout
- Empty states
- Loading states
- Error states

The user must be able to compare products easily.

Do not over-animate product cards.

---

## Product Detail Page

The PDP is a major brand experience.

Include:

- Product gallery
- Image zoom
- Product title
- Price
- Description
- Material
- Dimensions
- Capacity
- Closure
- Strap information
- Interior information
- Care
- Availability
- Variant selection
- Quantity
- Add to cart
- Wishlist
- Shipping
- Returns
- Recommendations
- Related products

Product information must remain clear even when motion is reduced.

---

## Wishlist

Include:

- Product listing
- Remove action
- Add-to-cart
- Empty state
- Responsive behavior
- Feedback

---

## Cart

Include:

- Cart items
- Quantity controls
- Remove
- Price summary
- Promotional/coupon UI
- Continue shopping
- Checkout CTA
- Empty state

Add-to-cart feedback must be clear.

---

## Checkout

Build frontend UX only.

Include:

- Contact information
- Shipping information
- Delivery options
- Order summary
- Coupon UI
- Payment UI placeholder
- Validation
- Error states
- Confirmation flow

Do not integrate a real payment provider yet.

---

## Account / Orders

Prepare UI for:

- Profile
- Addresses
- Orders
- Order details
- Returns
- Wishlist
- Account settings

Use mock/local data for now.

---

## Content Pages

Implement:

- About
- Journal
- FAQ
- Contact
- Returns
- Shipping

Use reusable content components where possible.

---

# 11. ADMIN ARCHITECTURE

Create modular frontend architecture for:

- Dashboard
- Products
- Categories
- Inventory
- Orders
- Payments
- Shipments
- Returns
- Refunds
- Customers
- Reviews
- Coupons
- CMS
- Users
- Roles
- Settings
- Audit

The Admin must NOT be one monolithic component.

Use reusable:

- tables
- filters
- forms
- cards
- dialogs
- status badges
- navigation
- data views

Backend authorization will be added later.

---

# 12. DATA ARCHITECTURE

Use realistic mock data.

Create strong TypeScript domain models for:

```text
Product
Category
Variant
CartItem
WishlistItem
Customer
Address
Order
OrderItem
Review
Coupon
ContentEntry
AdminUser
```

Create a data-access boundary such as:

```text
UI
 ↓
Repository / Data Function
 ↓
Mock implementation
 ↓
Future REST implementation
```

Do not make components depend directly on mock-data files.

---

# 13. MOTION ARCHITECTURE

Motion is part of the RÓRA design system.

Primary stack:

```text
Lenis
   ↓
Smooth Scrolling
   ↓
GSAP / ScrollTrigger
   ↓
Purposeful Motion
   ↓
Premium Commerce Experience
```

Use:

- GSAP
- GSAP ScrollTrigger
- Lenis
- CSS transitions

Do not introduce another animation library unless there is a genuine architectural reason.

---

# 14. MOTION HIERARCHY

## Level 1 — Micro Interaction

Prefer CSS transitions for:

- Button hover
- Button press
- Link hover
- Icon hover
- Input focus
- Card hover
- Image hover
- Wishlist interaction
- Quantity controls
- Toggle states

Keep these fast and subtle.

## Level 2 — Component Animation

Use GSAP or structured CSS for:

- Cart drawer
- Search panel
- Mega menu
- Filter panel
- Quick view
- Product gallery
- Variant transitions
- Toasts
- Recommendation transitions

## Level 3 — Page / Section Motion

Use GSAP + ScrollTrigger for:

- Hero storytelling
- Major section reveals
- Product showcases
- Editorial sections
- Category storytelling
- Featured product presentation

## Level 4 — Signature RÓRA Motion

Use selectively for:

- Hero entrance
- Product reveal
- Brand transition
- Premium campaign storytelling

The goal is NOT more animation.

The goal is:

```text
better interaction
+
stronger product storytelling
+
premium brand perception
+
smooth UX
+
production performance
```

---

# 15. LENIS REQUIREMENTS

Lenis must:

- feel natural
- work with GSAP
- stay synchronized with ScrollTrigger
- preserve normal scrolling semantics
- support mouse wheel
- support trackpad
- support touch
- work on desktop/tablet/mobile
- support scroll locking for modals/drawers
- preserve keyboard scrolling
- respect reduced motion

Do not create scroll-jacking.

---

# 16. GSAP / SCROLLTRIGGER RULES

Every scroll animation must have a UX purpose.

Good use:

- image reveal
- editorial storytelling
- product reveal
- section entrance
- controlled parallax
- meaningful product transformation

Avoid:

- excessive parallax
- constant movement
- large vertical shifts
- animation on every card
- endless stagger effects
- long transitions
- effects that slow browsing
- effects that interfere with purchasing

---

# 17. PRODUCT CARD MOTION

Product cards may use:

- subtle image transition
- subtle image zoom
- small elevation
- quick-action reveal
- wishlist feedback
- add-to-cart feedback
- variant feedback

Do not make every card perform a large animation.

---

# 18. PRODUCT PAGE MOTION

The PDP can have stronger motion than listing pages.

Possible motion:

- gallery transitions
- thumbnail transitions
- image zoom
- variant changes
- information reveal
- sticky purchase area transition
- add-to-cart feedback
- wishlist feedback
- recommendation reveal
- related product transitions
- carefully selected scroll storytelling

The product itself must remain the visual focus.

Never let animation compete with:

- product name
- price
- availability
- purchase controls
- important product information

---

# 19. HERO MOTION

Hero may use:

- headline reveal
- supporting text reveal
- CTA entrance
- product/visual reveal
- subtle image movement
- layered depth
- controlled scroll response

Do NOT use:

- bouncing text
- random rotations
- excessive zoom
- huge parallax
- multiple simultaneous effects

The hero motion should feel specific to RÓRA.

---

# 20. CART / CHECKOUT MOTION

Motion should communicate state.

Examples:

- Add-to-cart confirmation
- Cart count update
- Drawer entrance
- Product insertion
- Quantity update
- Item removal
- Coupon feedback
- Checkout step transition

Never hide important commerce information behind animation.

---

# 21. RESPONSIVE MOTION

Do not blindly use identical motion on all devices.

Mobile should generally:

- reduce animation complexity
- avoid expensive parallax
- avoid excessive pinned sections
- avoid long sequences
- prioritize touch
- preserve scroll performance
- avoid gesture conflicts
- keep drawers/sheets natural

---

# 22. PERFORMANCE

Prefer animating:

```text
transform
opacity
```

Avoid unnecessary animation of:

```text
width
height
top
left
margin
padding
```

Avoid:

- layout recalculation loops
- DOM measurement loops
- huge GSAP timelines
- excessive simultaneous animations
- high-frequency React state updates during scroll

Do not sacrifice Core Web Vitals for visual effects.

---

# 23. NEXT.JS + ANIMATION COMPATIBILITY

Animation code must be compatible with production Next.js.

Rules:

- Browser-only APIs must not execute during server rendering.
- Use Client Components only where needed.
- Keep most components server-renderable.
- Do not make the entire application a Client Component.
- Initialize Lenis/GSAP correctly on the client.
- Prevent hydration issues.
- Clean up GSAP contexts/timelines on unmount.
- Do not initialize duplicate Lenis systems.
- Do not initialize duplicate ScrollTrigger systems.

---

# 24. ACCESSIBILITY

Must support:

- semantic HTML
- keyboard navigation
- visible focus states
- accessible buttons
- accessible forms
- accessible dialogs
- correct labels
- appropriate ARIA where required
- color contrast
- screen-reader usability
- reduced motion

Respect:

```text
prefers-reduced-motion
```

When reduced motion is enabled:

- reduce decorative motion
- reduce scroll effects
- reduce parallax
- reduce long transitions
- preserve functional feedback
- preserve usability

Accessibility has priority over decorative animation.

---

# 25. MOTION TOKENS

Do not scatter random animation values.

Create reusable values for:

- duration
- delay
- easing
- stagger
- distance
- scale
- opacity
- transition intensity

Keep motion values aligned with the RÓRA design-token architecture.

---

# 26. LOADING EXPERIENCE

Do not create a long cinematic loading screen.

Priority:

```text
1. Content availability
2. Perceived performance
3. Navigation readiness
4. Product discovery
5. Visual enhancement
```

The user should never feel forced to wait for an animation.

Use skeletons/loading states where useful.

---

# 27. AI-READY FRONTEND

The architecture may expose future extension points for:

- AI recommendations
- AI search suggestions
- Smart filters
- Product comparison assistance
- Review summaries
- Personalized discovery

But do not implement real AI services in this phase.

Do not create a generic floating AI chatbot just to claim AI functionality.

AI should eventually feel embedded into the commerce experience.

---

# 28. SEO / AEO / GEO READINESS

Prepare the frontend for:

- metadata
- page titles
- descriptions
- canonical URLs
- Open Graph
- structured content
- product information
- semantic headings
- crawlable content
- clean URLs

Do not sacrifice semantic HTML for visual effects.

---

# 29. ERROR / EMPTY / LOADING STATES

Every important feature should have:

- loading state
- empty state
- error state
- success feedback

Examples:

- Empty cart
- Empty wishlist
- No search results
- Product unavailable
- Invalid quantity
- Checkout validation error
- Order not found
- Admin empty table

Do not design only the happy path.

---

# 30. TESTING / VALIDATION

After meaningful implementation:

### Build

Run:

```bash
npm install
npm run build
```

Fix all build errors.

### Type Safety

Run the project's configured TypeScript/type checks.

### Lint

Run the configured lint command.

### Browser Verification

Manually test:

```text
Home
→ Shop
→ Category
→ Search
→ Product
→ Wishlist
→ Cart
→ Checkout
→ Confirmation
→ Account
→ Orders
→ Admin
```

Verify:

- navigation
- buttons
- links
- cart
- wishlist
- quantity
- forms
- checkout
- empty states
- error states
- loading states
- responsive behavior
- keyboard navigation
- focus states
- reduced motion
- animation behavior
- admin navigation

---

# 31. RESPONSIVE VERIFICATION

Verify at minimum:

```text
360px
390px
430px
768px
1024px
1280px
1440px
```

Check:

- layout
- typography
- product grids
- navigation
- drawers
- forms
- checkout
- images
- spacing
- motion
- touch interactions

---

# 32. IMAGE / VISUAL VERIFICATION

For every major page verify:

- image aspect ratios
- cropping
- responsive image behavior
- loading behavior
- visual hierarchy
- whitespace
- typography
- CTA placement
- product prominence

Do not use placeholder images where final project assets already exist.

---

# 33. DO NOT OVER-ENGINEER

Do not add:

- unnecessary state libraries
- unnecessary animation libraries
- unnecessary abstractions
- unnecessary dependencies
- unnecessary client components
- unnecessary API layers
- unnecessary backend assumptions

Production quality does not mean unnecessary complexity.

Prefer the smallest clean architecture that can evolve.

---

# 34. DO NOT DO THESE THINGS

Never:

1. Rebuild the entire project blindly.
2. Delete working features without reason.
3. Replace approved design tokens with arbitrary values.
4. Add random colors.
5. Add random fonts.
6. Add random border radii.
7. Overuse animations.
8. Add a long loading animation.
9. Make every component a Client Component.
10. Hardcode product data directly inside UI components.
11. Scatter image URLs everywhere.
12. Build a monolithic Admin page.
13. Add backend integration during this phase.
14. Add authentication during this phase.
15. Add payment integration during this phase.
16. Add deployment configuration prematurely.
17. Add AI just for marketing purposes.
18. Copy generic SaaS/AI dashboard motion.
19. Sacrifice accessibility for visuals.
20. Sacrifice performance for animation.

---

# 35. IMPLEMENTATION CHECKPOINTS

Work in checkpoints.

## Checkpoint 1 — Audit

Deliver:

- repository analysis
- current architecture
- reuse plan
- gap list
- proposed implementation order

Do not make major code changes until this is clear.

## Checkpoint 2 — Foundation

Implement:

- Next.js setup/fixes
- design tokens
- global styles
- fonts
- layout
- basic component system

Verify build.

## Checkpoint 3 — Motion Foundation

Implement:

- Lenis
- GSAP
- ScrollTrigger
- motion tokens
- reduced-motion support
- animation utilities

Verify:

- desktop
- mobile
- keyboard
- modal scroll lock

## Checkpoint 4 — Core Commerce

Implement:

- Header
- Homepage
- Shop
- Category
- Search
- Product
- Wishlist
- Cart

Verify all flows.

## Checkpoint 5 — Checkout / Account

Implement:

- Checkout
- Confirmation
- Account
- Orders

Verify validation and states.

## Checkpoint 6 — Content / Admin

Implement:

- About
- Journal
- FAQ
- Contact
- Returns
- Shipping
- Admin-ready architecture

## Checkpoint 7 — Quality

Perform:

- accessibility audit
- responsive audit
- performance audit
- motion audit
- SEO audit
- browser walkthrough
- production build

---

# 36. REPORTING FORMAT

After every meaningful checkpoint, report:

```text
STATUS: COMPLETE / PARTIAL / BLOCKED

Implemented:
- ...

Changed:
- ...

Reused:
- ...

New Components:
- ...

New Routes:
- ...

Motion Added:
- ...

Tests:
- Build:
- Typecheck:
- Lint:
- Browser:

Responsive:
- ...

Accessibility:
- ...

Known Issues:
- ...

Next Step:
- ...
```

Do not simply say "done".

Provide evidence of verification.

---

# 37. FINAL QUALITY BAR

Before declaring the frontend complete, verify:

### Product

- Does it feel like a premium bag brand?
- Is the product always visually important?
- Is the experience editorial but still easy to shop?

### UX

- Can users discover products quickly?
- Can users understand product details?
- Can users add/remove products easily?
- Are empty/error states handled?

### Design

- Are approved tokens respected?
- Is typography consistent?
- Is spacing consistent?
- Are colors semantic?
- Are images high quality?

### Motion

- Is Lenis smooth?
- Is GSAP used only where valuable?
- Is ScrollTrigger purposeful?
- Is motion subtle and premium?
- Is mobile motion appropriate?
- Does reduced motion work?

### Performance

- Are animations efficient?
- Are unnecessary client components avoided?
- Are there unnecessary renders?
- Are images optimized?
- Does the app remain responsive?

### Accessibility

- Keyboard navigation works.
- Focus states work.
- Forms are accessible.
- Dialogs/drawers are accessible.
- Reduced motion works.

### Architecture

- Components are reusable.
- Domain models are typed.
- Data access is separated from UI.
- Mock data can later be replaced by REST APIs.
- Admin is modular.
- No major rewrite should be required for Spring Boot integration.

---

# 38. FINAL IMPLEMENTATION PRINCIPLE

Build RÓRA as if it will become a real commercial e-commerce product.

Do not make it generic because AI is writing the code.

Do not sacrifice UX for technical complexity.

Do not sacrifice architecture for visual speed.

Do not sacrifice performance for animation.

Do not sacrifice maintainability for shortcuts.

The final result should be:

```text
Premium
+
Editorial
+
Responsive
+
Accessible
+
Performant
+
Motion-rich but restrained
+
Commerce-ready
+
API-ready
+
Maintainable
```

The core principle is:

> Build the smallest complete commerce system that feels like a real premium brand.

---

# 39. FIRST COMMAND TO ANTIGRAVITY

After reading this document, do NOT immediately start coding.

First:

1. Audit the complete repository.
2. Read all referenced project documents.
3. Inspect the existing prototype.
4. Inspect the design tokens.
5. Inspect assets.
6. Inspect routes and components.
7. Identify what can be reused.
8. Identify what must be refactored.
9. Identify what is missing.
10. Prepare the implementation plan.
11. Wait for approval before beginning major implementation.

Only after approval should the implementation proceed checkpoint by checkpoint.
