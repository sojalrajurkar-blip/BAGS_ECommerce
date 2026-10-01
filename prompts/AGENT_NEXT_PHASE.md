# AGENT_NEXT_PHASE.md

# RÓRA Premium Bags E-Commerce Prototype
## Existing Project Audit, Fixes, Completion & QA Instructions

> **Purpose:** This document tells the Antigravity agent exactly how to continue the existing project safely.
>
> **Important:** Do NOT rebuild the project from scratch. First inspect the existing implementation, compare it against the existing prompt/design-token documents, then make only the required corrections and additions.

---

# 1. Primary Goal

The current project is a **frontend-only premium bags e-commerce prototype**.

The project already contains a substantial implementation. Your job is to:

1. Audit the existing implementation.
2. Identify mismatches against the existing project prompts and design tokens.
3. Fix the mismatches.
4. Complete missing prototype functionality.
5. Preserve the existing visual direction and working features.
6. Perform responsive, accessibility, and visual QA.
7. Report exactly what was changed and what remains.
8. Do NOT introduce backend, authentication, payments, Supabase, or production APIs in this phase.

---

# 2. Source of Truth

Before making changes, inspect these files/folders in the workspace:

- `prompts/`
- `desgin_reference/` (use the actual existing folder name)
- `Premium_Bags_Design_Tokens.md`
- `Antigravity_Bags_Ecommerce_Prototype_Prompt.md`
- Existing `README.md`
- Existing `src/` implementation

### Priority order

When instructions conflict, use this order:

1. Explicit requirements in the latest project prompt
2. `Premium_Bags_Design_Tokens.md`
3. Existing design references
4. Existing implementation
5. Your own implementation preference

Do NOT silently invent a new visual system.

---

# 3. NON-NEGOTIABLE RULES

## 3.1 Do not rebuild from scratch

The current project already contains working pages, components, data, context, navigation, cart, wishlist, and other functionality.

Do NOT delete and regenerate the project.

Do NOT replace the whole frontend with a new template.

Only refactor when there is a clear maintainability or correctness reason.

---

## 3.2 Do not change the brand direction

Preserve the existing premium/editorial direction:

- RÓRA temporary brand identity
- Warm neutral / earthy palette
- Editorial typography
- Cormorant Garamond for display typography
- Inter for body/UI typography
- Premium photography
- Restrained motion
- Strong whitespace
- Minimal UI
- No generic SaaS appearance
- No unnecessary gradients
- No excessive rounded cards
- No visual clutter
- No purple/blue AI-style visual language

---

## 3.3 Do not add backend functionality

This phase is frontend prototype only.

DO NOT add:

- Supabase
- PostgreSQL integration
- REST backend
- Authentication
- JWT
- OAuth
- Google Login
- Razorpay
- Stripe
- Real payment processing
- Real email services
- Production API integrations

Use mock/local data where necessary.

---

# 4. FIRST TASK — FULL AUDIT

Before editing code, inspect the complete project.

Check:

```text
src/
  components/
  context/
  data/
  pages/
  App.jsx
  index.css
  main.jsx
```

Also inspect:

- `package.json`
- `vite.config.*`
- `index.html`
- all prompt files
- design token files
- all design-reference files

Then create an internal checklist of:

- Existing pages
- Existing routes/navigation
- Existing components
- Existing design tokens
- Existing cart functionality
- Existing wishlist functionality
- Existing search
- Existing checkout
- Existing account/order flow
- Existing admin functionality
- Existing responsive behavior
- Existing accessibility support

Do not start coding until the audit is understood.

---

# 5. DESIGN TOKEN CONSISTENCY — HIGH PRIORITY

`Premium_Bags_Design_Tokens.md` is the visual source of truth.

Compare the documented tokens against the actual CSS implementation.

## 5.1 Typography

Make sure these values are consistent with the design-token document:

```css
--text-display-xl
--text-display-lg
--text-display-md
--leading-display
--leading-heading
--leading-tight
--leading-normal
```

Do not keep competing values in different files.

---

## 5.2 Radius

Make sure:

```css
--radius-lg
```

and all other radius tokens match the design-token document.

---

## 5.3 Motion

Centralize motion tokens.

The design system should support:

```css
--duration-fast
--duration-normal
--duration-slow
--duration-editorial

--ease-standard
--ease-smooth
```

Components should use these tokens rather than repeatedly inventing their own transition timings/easing.

---

## 5.4 Breakpoints

Keep responsive breakpoints consistent with the documented system:

```text
640px
768px
1024px
1280px
1440px
```

Do not create random breakpoints unless there is a real layout requirement.

---

## 5.5 Semantic colors

Avoid hard-coded semantic UI colors where tokens can be used.

Create/use semantic tokens for:

```css
--success
--warning
--error
--info
```

Use them for:

- Toasts
- Alerts
- Validation
- Status indicators

Do not introduce arbitrary colors without justification.

---

# 6. IMPORTANT FINANCIAL CALCULATION RULE

The design documentation may describe backend-authoritative financial calculations.

However, this project is currently a frontend prototype.

Therefore:

### Prototype phase

Frontend mock calculations are allowed for:

- subtotal
- shipping
- discounts
- coupon previews
- estimated totals

### Production phase

Backend must become authoritative for all financial calculations.

Do NOT implement production backend logic now.

Do not create contradictory documentation.

---

# 7. CART AND WISHLIST

Audit the existing cart and wishlist implementation.

Verify:

- Add to cart works
- Remove works
- Quantity update works
- Move to wishlist works
- Move back to cart works
- Wishlist add/remove works
- Cart total updates
- Shipping calculation updates
- Coupon calculation updates
- Empty cart state works
- Empty wishlist state works
- Refresh does not unexpectedly break the state
- localStorage persistence works

## Initial state

For a normal prototype experience:

```text
cart = []
wishlist = []
```

If demo seed data is required, keep it behind an explicit demo mechanism.

Do not silently show a pre-filled cart to every fresh visitor.

---

# 8. CUSTOMER PAGES

Audit and verify these pages/routes.

## Core shopping

- Home
- Shop
- Category
- Product detail
- Search
- Wishlist
- Cart
- Checkout
- Order confirmation

## Customer account

- Account
- Orders
- Order detail/tracking

## Content/support

- About
- Journal
- FAQ
- Contact
- Returns
- Shipping
- 404

Every visible navigation/footer link must:

- point to a valid route
- render the correct page
- not lead to a dead end
- work on mobile

---

# 9. ADD MISSING SHIPPING PAGE

If the project currently does not have a dedicated Shipping page, add:

```text
ShippingPage.jsx
```

Include:

- Shipping methods
- Estimated delivery times
- Free shipping rule if applicable
- Domestic shipping
- International shipping if applicable
- Order tracking
- Delivery delays
- Damaged/lost package guidance
- Support/contact CTA

Keep the page visually consistent with the existing brand.

---

# 10. SEARCH

Verify search functionality.

Required states:

- Search open
- Search input
- Search suggestions
- Recent searches
- Search results
- No results
- Clear search
- Product result navigation

Do not add AI search.

Keep search deterministic for the prototype.

---

# 11. PRODUCT EXPERIENCE

Verify the product page.

It should support:

- Product image gallery
- Product name
- Price
- Description
- Variant/option if applicable
- Quantity
- Add to cart
- Wishlist
- Product details
- Shipping/returns information
- Related products

Important:

Do not make the product page look like a generic marketplace.

Maintain the editorial/premium visual language.

---

# 12. ADMIN — HIGH PRIORITY

The existing admin implementation is incomplete.

Do NOT throw away the existing admin page.

Refactor/extend it.

The prototype should cover these areas:

### Dashboard

- Revenue mock metric
- Orders mock metric
- Customers mock metric
- Products/mock inventory metric
- Recent orders
- Basic sales overview

### Catalog

- Products
- Categories
- Inventory

### Orders

- Orders
- Payments
- Shipments
- Returns
- Refunds

### Customer

- Customers
- Reviews

### Marketing

- Coupons

### Content

- CMS/content management

### Administration

- Users
- Roles & Permissions
- Settings
- Audit Logs

These can be prototype views/tables.

They do NOT need real backend APIs.

---

# 13. ADMIN ARCHITECTURE

Do not keep all admin logic in one giant component if it becomes difficult to maintain.

Prefer a structure such as:

```text
src/
  pages/
    admin/
      AdminPage.jsx
      AdminDashboard.jsx
      AdminProducts.jsx
      AdminCategories.jsx
      AdminInventory.jsx
      AdminOrders.jsx
      AdminCustomers.jsx
      AdminPayments.jsx
      AdminShipments.jsx
      AdminReturns.jsx
      AdminRefunds.jsx
      AdminCoupons.jsx
      AdminReviews.jsx
      AdminCMS.jsx
      AdminUsers.jsx
      AdminRoles.jsx
      AdminSettings.jsx
      AdminAuditLogs.jsx

  components/
    admin/
      AdminSidebar.jsx
      AdminHeader.jsx
      AdminTable.jsx
      AdminStat.jsx
      AdminStatusBadge.jsx
      AdminModal.jsx
```

You may use a slightly different structure if the existing architecture has a good reason, but keep responsibilities separated.

Do not over-engineer the prototype.

---

# 14. ADMIN LOGIN

Because this is a frontend prototype:

Do NOT implement real authentication.

If an Admin Login screen is required by the project prompt, create a **visual/demo login flow only**.

Example:

```text
Admin Login
Email
Password
Sign In
```

The login may transition to the admin dashboard using mock state.

Clearly keep this prototype-only.

---

# 15. IMAGE MANAGEMENT

The project currently uses external image URLs.

Do not replace all images unnecessarily.

However, centralize image references where practical.

Prefer:

```text
src/data/imageAssets.js
```

or an equivalent centralized file.

Example:

```js
export const IMAGE_ASSETS = {
  hero: "...",
  product1: "...",
  product2: "...",
};
```

This makes future replacement with real brand photography easier.

---

# 16. GRADIENT RULE

Do not remove legitimate image readability overlays.

Allowed:

```text
Image
+
subtle dark/light overlay
+
text
```

Not allowed:

- decorative AI-style gradients
- neon gradients
- purple-blue startup gradients
- excessive gradient cards

Use gradients only when they serve a clear visual/readability purpose.

---

# 17. ROUTING

The current prototype may use lightweight/custom routing.

Do not migrate to React Router just for the sake of changing it.

If the current routing works correctly, preserve it.

Only introduce a routing library if the current approach becomes difficult to maintain because of the growing number of pages.

---

# 18. RESPONSIVE QA

Test at minimum:

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

- Header
- Navigation
- Hero
- Product grids
- Product detail
- Cart
- Checkout
- Tables
- Admin sidebar
- Admin tables
- Modals
- Search
- Footer
- Typography
- Images

No:

- horizontal overflow
- clipped text
- broken buttons
- inaccessible controls
- overlapping content
- unusable admin tables

---

# 19. ACCESSIBILITY

Verify:

- semantic HTML
- buttons are real buttons
- links are real links
- form labels exist
- keyboard navigation works
- focus states are visible
- modal focus behavior is reasonable
- image alt text exists
- sufficient text contrast
- reduced-motion behavior is respected

Add:

```css
@media (prefers-reduced-motion: reduce) {
  ...
}
```

if not already present.

---

# 20. PERFORMANCE

Check:

- unnecessary re-renders
- very large components
- duplicated data
- unnecessarily large images
- missing lazy loading where appropriate
- excessive animation
- unnecessary dependencies

Do not optimize prematurely.

Only make safe improvements that do not change the visual result.

---

# 21. ERROR / EMPTY / LOADING STATES

Every important flow should have a meaningful state for:

### Empty

Examples:

- Empty cart
- Empty wishlist
- No search results
- No orders
- Empty admin table

### Error

Examples:

- Invalid form
- Failed mock action
- Invalid coupon
- Missing product

### Loading

Where a realistic async-looking interaction is demonstrated.

Do not create fake long loading delays.

---

# 22. CODE QUALITY

While modifying the project:

- Reuse components
- Avoid duplicated JSX
- Avoid duplicated constants
- Avoid magic numbers where a token exists
- Keep component names clear
- Keep data separate from presentation
- Keep admin code separated
- Keep context focused
- Remove dead code only when safe

Do not rewrite working components without reason.

---

# 23. DO NOT CHANGE THESE WITHOUT JUSTIFICATION

Do not randomly change:

- Brand name
- Typography family
- Main palette
- Product names
- Page hierarchy
- Existing successful interactions
- Existing working cart behavior
- Existing working wishlist behavior
- Existing visual reference direction

If you believe a change is necessary, explain why in the final report.

---

# 24. IMPLEMENTATION ORDER

Follow this exact order.

## Phase 1 — Audit

Do not modify code initially.

Inspect:

- prompts
- design tokens
- design references
- source code
- routes
- components
- data
- context

Then identify mismatches.

---

## Phase 2 — Design system correction

Fix:

- typography tokens
- line heights
- radius
- motion
- breakpoints
- semantic colors

Make the implementation match the design-token source of truth.

---

## Phase 3 — Customer experience

Verify/fix:

- navigation
- footer links
- shipping page
- search
- product page
- cart
- wishlist
- checkout
- confirmation
- account
- orders
- content/support pages

---

## Phase 4 — Admin completion

Complete the admin prototype:

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
- Roles
- Settings
- Audit Logs

---

## Phase 5 — Responsive QA

Test:

```text
360
390
430
768
1024
1280
1440
```

Fix issues found.

---

## Phase 6 — Accessibility and UX QA

Verify:

- keyboard navigation
- focus
- labels
- alt text
- semantic elements
- reduced motion
- contrast
- empty states
- error states

---

## Phase 7 — Final visual QA

Compare the implementation against:

- design-token document
- design references
- original prompt

Look specifically for:

- inconsistent spacing
- inconsistent typography
- inconsistent radius
- inconsistent buttons
- inconsistent cards
- excessive shadows
- unnecessary borders
- inconsistent image ratios
- visual clutter
- generic UI patterns

---

# 25. VALIDATION COMMANDS

After changes, run the available project checks.

At minimum:

```bash
npm install
npm run build
```

If available:

```bash
npm run lint
```

If tests exist:

```bash
npm test
```

Do not modify package scripts just to make a command pass.

If a command fails, fix the actual issue and report it.

---

# 26. FINAL CHECKLIST

Before saying the task is complete, verify:

### Design

- [ ] Design tokens match implementation
- [ ] Typography is consistent
- [ ] Colors are consistent
- [ ] Radius is consistent
- [ ] Motion is consistent
- [ ] Responsive breakpoints are consistent

### Customer

- [ ] Home works
- [ ] Shop works
- [ ] Category works
- [ ] Product works
- [ ] Search works
- [ ] Wishlist works
- [ ] Cart works
- [ ] Checkout works
- [ ] Confirmation works
- [ ] Account works
- [ ] Orders work
- [ ] Tracking works
- [ ] About works
- [ ] Journal works
- [ ] FAQ works
- [ ] Contact works
- [ ] Returns works
- [ ] Shipping works
- [ ] 404 works

### Admin

- [ ] Admin dashboard
- [ ] Products
- [ ] Categories
- [ ] Inventory
- [ ] Orders
- [ ] Customers
- [ ] Payments
- [ ] Shipments
- [ ] Returns
- [ ] Refunds
- [ ] Coupons
- [ ] Reviews
- [ ] CMS
- [ ] Users
- [ ] Roles & permissions
- [ ] Settings
- [ ] Audit logs

### UX

- [ ] Empty states
- [ ] Error states
- [ ] Loading states
- [ ] Toasts
- [ ] Form validation
- [ ] Mobile navigation

### Accessibility

- [ ] Keyboard navigation
- [ ] Focus states
- [ ] Alt text
- [ ] Form labels
- [ ] Semantic HTML
- [ ] Reduced motion
- [ ] Contrast

### Technical

- [ ] No unnecessary backend
- [ ] No authentication
- [ ] No payment gateway
- [ ] No Supabase
- [ ] No production API
- [ ] No broken imports
- [ ] No console errors
- [ ] Build succeeds

---

# 27. IMPORTANT: DO NOT STOP AFTER PARTIAL IMPLEMENTATION

If one phase reveals another existing issue, document it and fix it when it is directly related to this phase.

Do not say:

> "The project already works, so no changes are needed."

You must compare the implementation against the documented requirements.

At the same time, do not invent new requirements that are not present in the project documents.

---

# 28. FINAL REPORT FORMAT

After completing the work, provide a concise report in this structure:

```text
## Audit Summary

Existing project status:
...

## Fixed

1. ...
2. ...
3. ...

## Added

1. ...
2. ...
3. ...

## Refactored

1. ...
2. ...

## Validation

Build:
PASS / FAIL

Lint:
PASS / FAIL / NOT AVAILABLE

Tests:
PASS / FAIL / NOT AVAILABLE

## Remaining Issues

1. ...
2. ...

## Files Changed

- ...
- ...
- ...

## Important Notes

...
```

Be honest.

Do not claim something was tested if it was not tested.

---

# 29. STOP CONDITION

After completing:

1. Audit
2. Token correction
3. Customer missing features
4. Admin completion
5. Responsive QA
6. Accessibility QA
7. Build validation

STOP.

Do not proceed to:

- backend
- authentication
- Supabase
- deployment
- production payments

Those are separate future phases.

---

# 30. Success Criteria

This phase is successful only when:

> The existing prototype follows the documented design system, all major customer pages work, the admin prototype is complete enough to demonstrate the intended information architecture, the application is responsive, and the build passes without introducing backend/production dependencies.

The goal is **not more code**.

The goal is:

> **A polished, consistent, responsive, demo-ready premium bags e-commerce prototype.**
