# RÓRA — Technical Cleanup & TypeScript Migration

## Purpose

This document defines the corrections required in the current RÓRA luxury bags e-commerce frontend.

The project is already migrated from Vite to:

- Next.js 16.3.8
- App Router
- React
- TypeScript
- Turbopack

This phase is a **technical cleanup and correction phase**, not a rebuild.

The existing RÓRA design, pages, routes, interactions, mock data, animations, and visual direction must be preserved.

---

# 1. Critical Rules

Before changing anything:

- Audit the current repository.
- Do not rebuild the application from scratch.
- Do not redesign the RÓRA website.
- Do not change the existing visual direction.
- Do not change the RÓRA color palette.
- Do not change typography.
- Do not replace product images.
- Do not remove working pages.
- Do not remove working commerce interactions.
- Do not add Spring Boot yet.
- Do not add PostgreSQL yet.
- Do not add authentication yet.
- Do not add payment integration yet.
- Do not add Supabase yet.
- Do not add production deployment yet.
- Do not add customer-facing AI features.
- Do not add an AI chatbot.
- Do not introduce generic AI/SaaS UI.

The goal is to make the existing codebase cleaner, more type-safe, and maintainable without changing how RÓRA looks or behaves.

---

# 2. Preserve the Working Foundation

The following parts are already working and must be preserved:

- Next.js App Router
- Next.js 16.3.8
- React
- Turbopack
- `src/app/`
- Existing storefront routes
- Dynamic routes
- Existing admin route
- GSAP
- ScrollTrigger
- Lenis
- Reduced-motion handling
- Existing repository/data-access boundary
- Existing mock data
- StoreContext functionality
- Existing responsive layouts
- Existing RÓRA visual design
- Existing premium/editorial experience

Do not unnecessarily refactor working systems.

---

# 3. Fix Incomplete TypeScript Migration

## Current Problem

The project is not yet a completely strict TypeScript codebase.

There are still:

- `.tsx` files
- `.ts` files
- `.jsx` files
- `.js` files

The current TypeScript configuration also permits JavaScript.

Current configuration includes:

```json
"allowJs": true
```

and:

```json
"strict": false
```

Therefore do not claim that the entire application is fully strict TypeScript until this is actually completed.

## Required Fix

Gradually migrate application code from:

```text
.js
.jsx
```

to:

```text
.ts
.tsx
```

Use this order:

1. Context/state
2. Domain/data types
3. Repositories
4. Common components
5. Storefront views
6. Admin components
7. Animation helpers
8. Utility modules

Do not blindly convert files.

Preserve all existing behavior.

---

# 4. Remove Duplicate StoreContext

The project currently contains both:

```text
src/context/StoreContext.jsx
src/context/StoreContext.tsx
```

## Required Fix

Keep:

```text
src/context/StoreContext.tsx
```

Remove the old `.jsx` implementation only after confirming all imports use the TypeScript version.

Then search the entire repository for:

```text
StoreContext.jsx
```

There must be no remaining imports.

The existing store behavior must remain unchanged.

---

# 5. Reduce Unnecessary `any`

The current TypeScript code contains broad `any` usage.

Examples include patterns such as:

```ts
createContext<any>
useState<any[]>
product: any
params: any
color: any
```

## Required Fix

Replace unnecessary `any` usage with the existing domain types.

Use:

- `Product`
- `ProductVariant`
- `ProductSpecification`
- `Category`
- `CartItem`
- `WishlistItem`
- `Customer`
- `Address`
- `Order`
- `OrderItem`
- `OrderTimelineEvent`
- `Review`
- `Coupon`
- `ContentEntry`
- `AdminUser`
- `AuditLog`

Do not create duplicate domain models if an existing type already covers the requirement.

For genuinely dynamic data, prefer appropriate safe types such as:

```ts
unknown
Record<string, unknown>
specific interfaces
union types
generic types
```

Do not change runtime behavior.

---

# 6. Enable Strict TypeScript Properly

Do not simply change:

```json
"strict": false
```

to:

```json
"strict": true
```

and leave errors unresolved.

Use this sequence:

1. Migrate JavaScript/JSX files.
2. Add correct types.
3. Remove unnecessary `any`.
4. Run TypeScript validation.
5. Fix errors.
6. Enable `strict: true`.
7. Fix strict-mode errors.
8. Set `allowJs: false`.
9. Run TypeScript validation again.

Final target:

```json
"strict": true,
"allowJs": false
```

with zero TypeScript errors.

---

# 7. Clean Inline Styles

The project still contains static inline styles such as:

```tsx
style={{ ... }}
```

Therefore do not claim "zero inline styles" unless this is actually verified.

## Required Fix

Audit all inline styles.

Move static presentation styles into the existing CSS architecture:

```text
src/styles/index.css
src/styles/components.css
src/styles/pages.css
src/styles/admin.css
```

Example:

Before:

```tsx
<div style={{ margin: '16px 0' }}>
```

After:

```tsx
<div className="account-divider">
```

CSS:

```css
.account-divider {
  margin: 16px 0;
}
```

## Important

Do not blindly remove every inline style.

Dynamic styles that are genuinely required for:

- calculated transforms
- dynamic dimensions
- runtime animation values
- GSAP-controlled values

may remain after review.

The goal is:

> Remove unnecessary static inline styles.

After cleanup, search the repository for:

```text
style={{
style=
```

and report the remaining legitimate dynamic occurrences.

---

# 8. Migrate Admin JSX

The admin area still contains JavaScript/JSX components.

Examples include:

```text
AdminDashboard.jsx
AdminProducts.jsx
AdminOrders.jsx
AdminCustomers.jsx
AdminUsers.jsx
AdminCMS.jsx
AdminRefunds.jsx
```

and other remaining admin `.jsx` files.

## Required Fix

Gradually migrate admin components to:

```text
.tsx
```

Use the existing domain models.

Do not redesign the admin UI.

Do not remove admin modules.

Do not replace mock functionality with backend functionality.

---

# 9. Preserve Repository Architecture

The repository pattern must remain.

The current mock repositories are intentional because the real backend is not implemented yet.

Do not remove the repository layer.

Do not connect PostgreSQL.

Do not create Spring Boot APIs in this phase.

The intended architecture is:

```text
UI
 ↓
Repository
 ↓
Future REST API
 ↓
Spring Boot
 ↓
PostgreSQL
```

For the current frontend phase:

```text
UI
 ↓
Repository
 ↓
Mock Data
```

Type repository inputs and outputs properly.

Keep mock data behind repository boundaries.

---

# 10. Review Navigation

The existing StoreContext contains a legacy navigation abstraction such as:

```text
navigate('shop')
navigate('product', ...)
navigate('category', ...)
```

Do not remove it immediately.

First audit its usage.

For static navigation, prefer:

```tsx
<Link href="/shop">
```

where appropriate.

For programmatic navigation:

```tsx
useRouter()
router.push(...)
```

For pathname detection:

```tsx
usePathname()
```

Only refactor navigation when it clearly improves the architecture without breaking existing behavior.

Do not perform a large navigation rewrite.

---

# 11. Review Client/Server Boundaries

Review `"use client"` usage.

Do not remove Client Components that genuinely need:

- useState
- useEffect
- context
- browser APIs
- cart interactions
- wishlist interactions
- filters
- GSAP
- Lenis
- interactive UI

Keep route/page shells server-renderable where practical.

Do not convert everything into Client Components.

The goal is a clean Next.js architecture without breaking functionality.

---

# 12. Fix and Verify Lint

Run:

```bash
npm run lint
```

Verify that the lint setup is compatible with the current Next.js version.

If:

```text
next lint
```

is unsupported for the installed Next.js version, update the ESLint setup and npm script to a supported configuration.

Do not ignore lint errors.

Final requirement:

```bash
npm run lint
```

must pass successfully.

---

# 13. Review Mock Business Copy

The frontend contains realistic mock commerce content.

Some content may mention:

- global shipping
- DHL Express
- shipment insurance
- SMS/email notifications
- refund/replacement promises

Treat these as mock content until actual RÓRA business policies and services are defined.

Do not invent real commercial guarantees.

Review such copy and make it clearly neutral/placeholder where necessary.

Do not implement email functionality.

Do not implement SMS functionality.

---

# 14. Preserve Motion

Do not remove or redesign:

- GSAP
- ScrollTrigger
- Lenis
- reduced-motion support

After cleanup verify:

- no hydration errors
- no animation runtime errors
- no ScrollTrigger errors
- no Lenis errors
- reduced-motion still works

---

# 15. Preserve RÓRA Visual Design

The following must remain consistent.

## Typography

- Cormorant Garamond
- Inter

## Visual Direction

- premium
- editorial
- minimal
- sophisticated
- warm
- product-focused
- human-designed

Do not introduce:

- generic SaaS UI
- AI-looking UI
- chatbot bubbles
- excessive gradients
- excessive glassmorphism
- random 3D effects
- unnecessary animations

Do not change the existing RÓRA visual identity during technical cleanup.

---

# 16. Required Verification

After all fixes, run:

```bash
npm run lint
```

Run TypeScript validation.

Run:

```bash
npm run build
```

Start the production application:

```bash
npm run start
```

Verify the major routes:

- Home
- Shop
- Category
- Product
- Search
- Wishlist
- Cart
- Checkout
- Confirmation
- Account
- Orders
- Journal
- FAQ
- Contact
- Returns
- Shipping
- Admin

Check:

- browser console
- hydration
- broken images
- broken navigation
- responsive layouts
- animations

---

# 17. Final Technical Requirements

The desired final state is:

```text
Next.js
App Router
TypeScript
strict: true
allowJs: false
```

with:

```text
0 TypeScript errors
0 build errors
0 lint errors
0 broken routes
0 unnecessary static inline styles
0 duplicate StoreContext implementations
```

---

# 18. Final Report Requirements

Do not report "100% complete" without evidence.

The final report must include:

## TypeScript

- `.ts` count
- `.tsx` count
- remaining `.js` count
- remaining `.jsx` count
- remaining unnecessary `any` usage
- `strict` status
- `allowJs` status

## CSS

- inline style count
- remaining legitimate dynamic inline styles

## Architecture

- duplicate context status
- repository status
- navigation status
- Client/Server component status

## Quality

- lint result
- typecheck result
- build result
- runtime result

## Visual

- desktop verification
- tablet verification
- mobile verification
- animation verification

## Final Status

Use:

```text
COMPLETE
```

only if all required fixes are actually verified.

Otherwise use:

```text
PARTIALLY COMPLETE
```

and list the remaining work.

---

# 19. Most Important Principle

Do not rebuild RÓRA.

Clean it.

Do not change the product.

Improve its technical foundation.

Do not replace working functionality.

Make the existing functionality type-safe, maintainable, and production-ready.

The final result should still look and behave like the current RÓRA website, but the underlying codebase should be cleaner and more reliable.
