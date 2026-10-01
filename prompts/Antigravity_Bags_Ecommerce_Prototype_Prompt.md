# Antigravity Prompt — Premium Bags E-Commerce Prototype

## 1. Role

You are the implementation agent for a **premium Bags E-Commerce website prototype**.

Build the prototype as a **real, polished, responsive web experience**, not as a generic AI-generated UI.

The project direction, page requirements and design-token specification are the source of truth.

---

# 2. Primary Goal

Build a **frontend-only working prototype** of the Premium Bags E-Commerce website.

The immediate goal is to validate:

- Visual identity
- Page layouts
- User experience
- Navigation
- Product browsing
- Product presentation
- Cart interactions
- Wishlist interactions
- Search/filter interactions
- Checkout experience
- Responsive behavior
- Motion
- Overall premium brand feel

Do **not** attempt to build the complete production backend in this prototype phase.

Use realistic mock data where backend data would normally be required.

The prototype must run locally.

---

# 3. Critical Design Requirement

## ZERO AI SLOP

This is a strict requirement.

The website must NOT look:

- AI-generated
- Generic
- Template-generated
- SaaS-like
- Dashboard-like
- Futuristic AI-like
- Over-designed
- Full of random animations
- Full of rounded cards
- Full of gradients
- Full of glassmorphism

Do NOT add:

- AI chatbot
- AI shopping assistant
- Ask AI
- AI recommendations
- AI search
- AI-generated content indicators
- AI badges
- AI dashboard widgets
- AI-style glowing gradients
- Purple/blue AI gradients
- Random blobs
- Excessive floating cards
- Excessive pill UI
- Unnecessary 3D

If any component looks like a typical AI-generated website component, redesign it.

The final result should feel like:

> **A real premium fashion/lifestyle bags brand designed by a professional human product/UI team.**

---

# 4. Design Reference

Use this as the primary visual inspiration:

**Piccollo — Backpack E-Commerce Landing Page — Dribbble**

Reference:
https://dribbble.com/shots/27161130-Piccollo-Backpack-E-Commerce-Landing-Page

Use the reference for:

- Editorial composition
- Large product photography
- Typography hierarchy
- Generous whitespace
- Neutral/earthy visual direction
- Product storytelling
- Minimal navigation
- Premium fashion/lifestyle feeling
- Subtle motion

DO NOT copy:

- Exact layout
- Exact branding
- Exact copy
- Exact logo
- Exact product presentation
- Exact components
- Exact page structure

Create an original bags brand experience.

---

# 5. Existing Design Token Specification

Use the project's `Premium_Bags_Design_Tokens.md` as the visual implementation specification.

Do not randomly create another visual system.

Use the defined:

- Colors
- Typography
- Spacing
- Grid
- Radius
- Shadows
- Buttons
- Forms
- Motion
- Responsive rules
- Accessibility rules
- Anti-AI-Slop rules

If the file exists in the project, read it before implementation.

---

# 6. Color Direction

The visual system must be based on:

- Warm Off-White
- Soft Beige
- Charcoal / Near Black
- Taupe
- Muted Brown
- Selective Muted Olive

Primary visual character:

> warm + earthy + premium + editorial + minimal

Do NOT turn the site into a green website.

Olive should be a restrained accent.

Primary CTAs should generally use charcoal/near-black.

---

# 7. Typography

Use the typography system defined in the design-token document.

Recommended prototype pairing:

### Display

Cormorant Garamond

Use for:

- Hero headlines
- Large editorial headings
- Brand statements
- Storytelling sections

### Body/UI

Inter

Use for:

- Navigation
- Product names
- Prices
- Filters
- Forms
- Buttons
- Checkout
- Supporting text

Typography must create a clear contrast between:

**Editorial brand storytelling**

and

**Functional shopping UI**

Do not use too many fonts.

---

# 8. Prototype Brand Identity

Create a temporary original premium bags brand identity for the prototype.

Do not use:

- Piccollo branding
- Existing luxury brand names
- Generic names such as "Bag Store"
- AI-looking futuristic brand names

Choose a simple, sophisticated temporary brand name and document it clearly so it can easily be replaced later.

The logo should be a restrained wordmark.

Avoid complicated logos.

---

# 9. Prototype Pages

Build the following pages.

## Customer Pages

### 1. Home

Sections:

1. Minimal header
2. Editorial hero
3. Explore Our Collection
4. Featured Collection
5. Best Sellers
6. Product storytelling section
7. Brand/lifestyle story
8. Reviews
9. Trust/service features
10. Final CTA
11. Footer

Hero should contain:

- Large lifestyle/product photography
- Editorial serif headline
- Short supporting text
- Primary CTA
- Minimal navigation
- Subtle motion

---

### 2. Shop / All Bags

Include:

- Page header
- Intro copy
- Search
- Category navigation
- Filters
- Sort
- Product grid
- Wishlist buttons
- Pagination or load-more behavior
- Editorial promotional section
- Footer

Product filters:

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

Use mock product data.

---

### 3. Category Page

Create a reusable category-page layout.

Initially demonstrate:

- Backpacks
- Laptop Bags
- Handbags
- Sling Bags
- Travel Bags
- Tote Bags
- Office Bags
- School/College Bags

The category page should support:

- category hero
- description
- filters
- product grid
- sorting
- pagination/load more

---

### 4. Search Results

Build a real interactive prototype search.

Include:

- Search input
- Search suggestions
- Autocomplete-style mock suggestions
- Recent searches
- Results
- Empty state
- Product filtering
- Clear search

This is traditional search.

NO AI search.

---

### 5. Product Details

Build a premium editorial product page.

Include:

- Product gallery
- Thumbnail navigation
- Product name
- Price
- Original price
- Discount
- Rating
- Review count
- Colors
- Size/capacity
- Quantity
- Add to Cart
- Buy Now
- Wishlist
- Delivery information
- Return information

Below:

- Product Story
- Features
- Specifications
- Reviews
- Related Products
- You May Also Like
- Frequently Bought Together

Recommendations must be mock curated/catalog-based.

Do NOT describe them as AI recommendations.

---

### 6. Wishlist

Include:

- Saved products
- Remove
- Add to Cart
- Availability
- Recently Viewed

Wishlist interactions must work locally.

---

### 7. Cart

Include:

- Cart items
- Product image
- Variant
- Quantity
- Unit price
- Discount
- Subtotal
- Shipping
- Estimated tax
- Total
- Remove
- Move to Wishlist

Implement local cart state.

For prototype purposes, calculations can use mock frontend data.

Clearly structure the code so backend-authoritative calculations can replace the mock logic later.

---

### 8. Checkout

Create a realistic frontend checkout flow.

Steps:

```text
Shipping
↓
Payment
↓
Confirmation
```

Include:

- Customer details
- Delivery address
- Billing address
- Shipping method
- Coupon input
- Order summary
- Payment method selection
- Place order
- Confirmation screen

Payment is a prototype only.

Do NOT connect a real payment gateway in this phase.

---

### 9. Order Confirmation

Show:

- Success state
- Order number
- Ordered products
- Delivery address
- Payment summary
- Estimated delivery
- Track Order
- Continue Shopping

---

### 10. Account

Include:

- Profile
- Addresses
- Orders
- Wishlist
- Reviews
- Returns & Refunds
- Account settings
- Logout

Use realistic mock account data.

---

### 11. Orders

Include:

- Order list
- Order status
- Order date
- Total
- Product preview
- View details

Statuses:

- Placed
- Confirmed
- Processing
- Shipped
- Out for Delivery
- Delivered
- Cancelled

---

### 12. Order Details / Tracking

Include:

- Order information
- Products
- Price
- Shipping address
- Payment status
- Order timeline
- Tracking status
- Return option when applicable

---

### 13. About / Brand Story

Create a highly editorial page.

Sections:

- Hero photography
- Brand statement
- Story
- Craftsmanship
- Materials
- Values
- Lifestyle photography
- Final CTA

This page should feel closer to a fashion/lifestyle editorial than a corporate About page.

---

### 14. Journal

Create:

- Journal hero
- Article grid
- Categories
- Featured article
- Article cards
- Newsletter CTA

Example editorial topics:

- Travel
- Craftsmanship
- Materials
- Bag care
- Styling
- Product stories
- Brand stories

Use professionally written mock content.

---

### 15. FAQ

Use an elegant accordion layout.

Categories:

- General
- Products
- Shipping
- Orders
- Returns
- Payments

Use thin dividers rather than large rounded cards.

---

### 16. Contact

Include:

- Intro
- Email
- Phone
- Address
- Contact form
- Message field
- Submit
- Social links

Use realistic mock contact details.

---

### 17. Returns & Refunds

Include:

- Return policy
- Refund process
- Exchange information
- Eligibility
- Steps to start a return
- Contact support

Keep it editorial and readable.

---

### 18. 404

Create a premium branded 404 page.

Do not use a generic developer-style 404.

Use:

- Large typography
- Product/lifestyle image
- Short message
- Back Home CTA
- Continue Shopping CTA

---

# 10. Admin Prototype

Create a separate admin area.

The admin should be functional visually, but data can be mock data.

Pages:

- Admin Login
- Dashboard
- Products
- Categories
- Inventory
- Orders
- Customers
- Payments
- Shipments
- Returns
- Refunds
- Coupons
- Reviews
- CMS
- Users
- Roles & Permissions
- Settings
- Audit Logs

The admin UI should be professional and functional.

It can be denser than the customer website, but it must NOT look like a generic AI dashboard.

No glowing charts.
No excessive gradients.
No random cards.

Use:

- clean tables
- restrained charts
- useful metrics
- clear hierarchy
- thin borders
- consistent typography

---

# 11. Navigation

Customer header:

Desktop:

```text
Brand
Shop
Collections
About
Journal

Search
Account
Wishlist
Cart
```

Mobile:

```text
Menu
Brand
Search
Cart
```

Use a responsive navigation system.

Do not overload the header.

---

# 12. Product Data

Create realistic mock products.

Each product should contain:

```text
id
name
slug
category
price
originalPrice
discount
images
colors
material
capacity
size
weight
dimensions
rating
reviewCount
stock
description
features
specifications
sku
```

Create enough products to make:

- Home
- Shop
- Category
- Search
- Wishlist
- Cart
- Product Details

feel realistic.

Do not repeat the same product image everywhere.

---

# 13. Images

Use premium bag/lifestyle imagery.

Preferred visual direction:

- earthy locations
- travel
- architecture
- natural landscapes
- craftsmanship
- leather/fabric details
- realistic product photography

Avoid:

- generic stock-photo appearance
- obvious AI-looking people
- surreal objects
- unrealistic product shapes
- excessive dramatic effects

If external image URLs are used for the prototype, keep image usage organized so they can later be replaced with real brand assets.

---

# 14. Interaction Requirements

Implement working frontend interactions for:

- Navigation
- Search
- Filters
- Sorting
- Product selection
- Product gallery
- Wishlist
- Add to Cart
- Remove from Cart
- Quantity update
- Move to Wishlist
- Coupon mock validation
- Checkout steps
- Form validation
- Order confirmation
- Account navigation
- FAQ accordion
- Mobile menu
- Cart drawer if implemented
- Toast notifications where useful

Do not make buttons that look functional but do nothing unless clearly marked as prototype-only.

---

# 15. Motion

Use the motion system from the design tokens.

Preferred:

- subtle page transitions
- hero reveal
- image reveal
- text reveal
- product image transitions
- smooth scrolling
- editorial scroll storytelling
- small hover interactions

GSAP can be used for advanced motion.

Lenis can be used for smooth scrolling.

Do not animate everything.

Do not delay basic interactions.

Support reduced motion.

---

# 16. 3D

3D is NOT required for the first prototype.

Only add 3D if it genuinely improves the product presentation.

Do not add 3D simply to make the prototype look technically impressive.

If 3D is added:

- keep it lightweight
- make it optional
- disable/reduce it on mobile or lower-powered devices

---

# 17. Responsive Requirements

Build mobile-first.

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

Test at minimum:

- 360px
- 390px
- 430px
- 768px
- 1024px
- 1280px
- 1440px

The site must not simply be a shrunken desktop version.

Mobile must have its own intentional layout decisions.

---

# 18. Performance

Even though this is a prototype:

- optimize image loading
- lazy-load below-the-fold images
- avoid huge assets
- avoid unnecessary JavaScript
- clean up animations
- avoid layout shifts
- use responsive image sizes
- keep 3D optional
- prioritize mobile performance

---

# 19. Accessibility

Implement:

- semantic HTML
- keyboard navigation
- visible focus states
- accessible form labels
- alt text
- sufficient color contrast
- touch targets around 44px+
- reduced-motion support
- meaningful button labels

---

# 20. Code Quality

Use:

- reusable components
- reusable design tokens
- clear folder structure
- semantic naming
- no duplicated components
- no giant monolithic page component
- no random inline styling everywhere
- no hard-coded colors when a token exists

Example:

Good:

```css
color: var(--text-primary);
```

Avoid:

```css
color: #1E1D1A;
```

when the semantic token already exists.

---

# 21. Suggested Architecture

If the project does not already have a finalized frontend architecture, use a modern web architecture compatible with the visual requirements and document the decision before implementation.

The prototype should support:

```text
components/
pages or app/
data/
hooks/
lib/
styles/
assets/
```

Keep the architecture easy to migrate to a production API later.

Do not build backend logic into UI components.

---

# 22. No Production Backend Yet

This phase is prototype-only.

Do NOT implement:

- PostgreSQL
- real authentication
- real JWT
- real RBAC
- real Razorpay
- real order persistence
- real inventory reservation
- real object storage
- real email service
- production deployment

Use mock/local state.

However, structure the frontend so these can be integrated later without rewriting the entire UI.

---

# 23. Prototype State Management

At minimum support local state for:

```text
cart
wishlist
search
filters
sort
checkout
mobile navigation
account mock state
```

Persist cart/wishlist locally if practical.

---

# 24. Error / Empty / Loading States

Every major page must have intentional states.

Examples:

### Search

- Results
- No results
- Loading

### Wishlist

- Products
- Empty wishlist

### Cart

- Products
- Empty cart

### Orders

- Orders
- No orders

### Product

- Loaded
- Loading
- Not found

Do not use generic browser alerts for normal UI feedback.

---

# 25. Visual QA Checklist

Before considering the prototype complete, inspect every page.

### Design

- Does it feel premium?
- Does it feel original?
- Does it feel like a real bags brand?
- Is the visual hierarchy clear?
- Is whitespace intentional?
- Are product images dominant?

### Typography

- Are serif headlines used appropriately?
- Is UI text readable?
- Are font sizes consistent?
- Is hierarchy consistent?

### Color

- Are warm neutrals dominant?
- Is charcoal used for strong contrast?
- Is olive restrained?
- Are status colors subtle?

### Components

- Are buttons consistent?
- Are cards consistent?
- Are inputs consistent?
- Are filters consistent?
- Are icons consistent?

### Motion

- Is animation purposeful?
- Is anything moving unnecessarily?
- Does motion slow down shopping?

### Mobile

- Does navigation work?
- Are filters usable?
- Are product cards readable?
- Is checkout comfortable?
- Are buttons thumb-friendly?

### Anti-AI-Slop

Ask:

> If I showed this website without telling someone it was AI-assisted, would it look like a real premium fashion brand?

If the answer is no, redesign the problematic sections.

---

# 26. Development Process

Do NOT build every page in one giant uncontrolled generation.

Work in phases.

## Phase 1 — Foundation

Build:

- project structure
- design tokens
- fonts
- global styles
- responsive system
- core components
- header
- footer
- buttons
- inputs
- product card

Test.

---

## Phase 2 — Homepage

Build the complete homepage.

Test:

- desktop
- tablet
- mobile
- navigation
- motion
- visual hierarchy

Do not continue until the homepage is visually strong.

---

## Phase 3 — Shopping Experience

Build:

- Shop
- Category
- Search
- Filters
- Product Details

Test product browsing.

---

## Phase 4 — Commerce Flow

Build:

- Wishlist
- Cart
- Checkout
- Confirmation

Test the complete local flow:

```text
Product
↓
Add to Cart
↓
Cart
↓
Checkout
↓
Confirmation
```

---

## Phase 5 — Customer Account

Build:

- Account
- Orders
- Order Details
- Tracking
- Returns
- Reviews

---

## Phase 6 — Brand Content

Build:

- About
- Journal
- FAQ
- Contact
- Returns & Refunds
- 404

---

## Phase 7 — Admin Prototype

Build the admin screens and navigation.

---

## Phase 8 — Polish

Review:

- spacing
- typography
- colors
- images
- responsive behavior
- accessibility
- motion
- performance
- empty states
- loading states
- error states

---

# 27. Git / Checkpoint Rule

After every completed phase:

1. Run the application.
2. Test the phase.
3. Fix errors.
4. Check responsive layouts.
5. Review visual consistency.
6. Create a Git commit.

Use meaningful commit messages such as:

```text
feat: create design system foundation
feat: build premium homepage prototype
feat: add product discovery experience
feat: add cart and checkout prototype
feat: add customer account pages
feat: add admin prototype
fix: improve mobile product layout
```

Do not move to the next major phase with known broken functionality.

---

# 28. Important Implementation Instruction

Before writing code:

1. Inspect the existing project.
2. Inspect `Premium_Bags_Design_Tokens.md`.
3. Inspect any existing PRD/SRS/project-direction files.
4. Determine the current frontend setup.
5. Do not delete useful existing work.
6. Do not introduce a second conflicting design system.
7. Document any architecture decision that was not previously finalized.

Then implement Phase 1.

---

# 29. What NOT To Do

Do not:

- build the entire production system now
- create fake backend APIs just for appearance
- add AI features
- use generic templates
- copy Piccollo
- use random colors
- use random fonts
- use excessive rounded cards
- use excessive shadows
- use excessive gradients
- overuse animations
- add 3D without purpose
- create meaningless dashboard widgets
- use lorem ipsum in final visible prototype
- leave broken buttons without indication
- ignore mobile
- ignore accessibility

---

# 30. Final Acceptance Criteria

The prototype is acceptable only when:

- all required pages exist
- navigation works
- major interactions work
- product browsing works with mock data
- cart works
- wishlist works
- checkout flow works locally
- responsive layouts work
- typography is consistent
- color system is consistent
- animations are purposeful
- mobile experience is intentional
- no visible AI features exist
- no obvious AI-generated UI patterns remain
- the site feels like a premium bags/fashion brand
- the design is original and not a Piccollo clone
- the code is organized for future production development

---

# 31. Start Now

Start with **Phase 1 — Foundation**.

Do not build the entire project in one step.

First:

1. Inspect the repository.
2. Read the design-token document.
3. Read the project direction document.
4. Inspect the existing frontend architecture.
5. Establish the design system.
6. Create the core reusable components.
7. Build the header and footer.
8. Build the ProductCard.
9. Build the base responsive layout.
10. Run the project locally.
11. Fix all errors.
12. Show the completed Phase 1 result.
13. Wait for approval before moving to Phase 2.

### Final principle

> **Build less, but build it beautifully.**
>
> The goal of this prototype is not to show how much code was generated.
>
> The goal is to prove that this can become a **real, premium, original Bags brand website**.

