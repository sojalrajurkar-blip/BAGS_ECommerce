# Antigravity — Complete E-Commerce Project Repair & Integration Prompt

## Role

Act as the **lead full-stack engineer, software architect, QA engineer, and e-commerce UX engineer** for this existing project.

**Do not rebuild the project from scratch.** First understand the current codebase, preserve correct existing work, and then systematically repair the functional, architectural, data-flow, authentication, admin, checkout, UI, and QA problems described below.

The final result must be a **fully working college-level e-commerce application with production-quality architecture and behavior**.

---

# 1. PROJECT GOAL

This is a **single-brand e-commerce website**.

The functional benchmark is a large e-commerce platform such as Flipkart:

- Home
- Categories
- Product listing
- Search
- Filters
- Sorting
- Product details
- Product variants
- Wishlist
- Cart
- Authentication
- Customer profile
- Addresses
- Checkout
- Payment flow
- Orders
- Order tracking
- Returns
- Refunds
- Reviews
- Customer account
- Admin dashboard
- Product management
- Inventory
- Coupons
- CMS
- Customers
- Users
- Roles
- Settings
- Audit logs

However:

**DO NOT copy Flipkart's visual design or branding.**

The final storefront must have its **own original brand identity, product photography, colors, typography, layout language, and visual personality**.

Target:

> **Flipkart-level commerce functionality + our own original single-brand visual identity.**

---

# 2. IMPORTANT CLARIFICATIONS

The following files/directories exist in the actual project even though they were not included in the ZIP used for external analysis:

- `.github/workflows/`
- `nginx/`
- `README.md`
- `.gitignore`

Therefore, **do not treat those as missing**. Preserve them and verify that they actually work.

Also:

```ts
paymentProvider: 'Razorpay'
```

is **not a current defect**.

Real Razorpay integration will be added later.

For the current college project:

- use the existing mock/sandbox payment provider
- support simulated UPI/Card/Net Banking/COD-style flows where appropriate
- simulate payment success/failure/decline/timeout
- keep the provider abstraction ready for Razorpay later
- do not pretend Razorpay is already integrated

---

# 3. FIRST TASK — FULL CODEBASE AUDIT

Before changing code:

1. Read the entire repository.
2. Read:
   - `CLAUDE.md`
   - `AGENTS.md`
   - `PROGRESS.md`
   - PRD/SRS/project documentation
3. Inspect:
   - frontend
   - backend
   - database migrations
   - repositories
   - API clients
   - contexts
   - components
   - pages/routes
   - admin
   - authentication
   - Docker
   - Nginx
   - CI/CD
   - environment configuration
   - tests
4. Compare documentation with actual implementation.
5. Do not blindly trust `PROGRESS.md`.
6. Treat source code and runtime behavior as the source of truth.

Create a complete internal defect checklist and fix **all identified issues**, not only the first errors you encounter.

---

# 4. CORE ARCHITECTURE

Use one clear architecture:

```text
Next.js Frontend
        ↓
Repository / API Client Layer
        ↓
Spring Boot REST API
        ↓
Service Layer
        ↓
JPA / Repository
        ↓
PostgreSQL
```

The backend must be the source of truth for:

- products
- categories
- variants
- inventory
- cart
- wishlist
- customers
- addresses
- coupons
- orders
- payments
- shipments
- returns
- refunds
- reviews
- CMS data
- admin data

---

# 5. REMOVE SILENT MOCK FALLBACKS

This is a critical requirement.

Do **not** use normal runtime patterns such as:

```ts
try {
  return await apiCall();
} catch {
  return MOCK_DATA;
}
```

This hides real backend failures.

Correct behavior:

```text
API success
   ↓
Render real data

API failure
   ↓
Show error state
   ↓
Retry
```

Mock data may remain only as an explicitly controlled development/demo mode.

If mock mode is required, use an explicit configuration such as:

```text
NEXT_PUBLIC_USE_MOCK_DATA=true
```

Normal application mode must use the backend.

---

# 6. FRONTEND ↔ BACKEND INTEGRATION

The backend already contains substantial functionality. **Use it.**

The frontend currently has several repository/API abstractions but important screens still use local state or mock data.

Fix the entire integration.

---

# 7. PRODUCT CATALOG

Frontend must use backend APIs for:

- products
- categories
- product details
- variants
- availability
- search
- filtering
- sorting
- pagination

Do not load the entire catalog and perform all business filtering only in React when the backend supports the operation.

---

# 8. SEARCH

Implement:

```text
Search input
    ↓
Debounced request
    ↓
productRepository.searchProducts()
    ↓
Spring Boot API
    ↓
PostgreSQL
    ↓
Paginated results
```

Support where applicable:

- keyword
- category
- price range
- color
- material
- rating
- availability
- sorting
- pagination

Reflect search/filter state in the URL where appropriate.

Example:

```text
/shop?search=backpack&category=backpacks&sort=price-low
```

---

# 9. CATEGORY FILTERING

Use one canonical category model:

```text
Category
 ├ id
 ├ name
 └ slug
```

Do not inconsistently compare category ID, slug, and name.

Verify that every category filter returns the correct products.

---

# 10. PRODUCT + VARIANT MODEL

Use a commerce-correct model:

```text
Product
   ↓
Product Variant
   ↓
SKU
   ↓
Inventory
   ↓
Price
   ↓
Images
```

Cart/order items should identify the actual variant/SKU.

Do not identify a variant only by a color name.

Example:

```text
Product
 ├ Black / 20L
 ├ Olive / 20L
 └ Taupe / 20L
```

Each variant should have a unique identity/SKU.

---

# 11. CART — BACKEND MUST BE AUTHORITATIVE

Replace the current localStorage/React-only primary cart flow.

Correct architecture:

```text
Guest
 ↓
Guest/session cart
 ↓
Backend
```

Then:

```text
Guest cart
 ↓
Login
 ↓
Cart merge
 ↓
Customer cart
```

Support:

- add item
- update quantity
- remove item
- clear cart
- variant selection
- stock validation
- price validation
- coupon application
- persistence
- loading
- error
- empty state

---

# 12. WISHLIST — BACKEND MUST BE AUTHORITATIVE

Replace the local-only wishlist as the primary implementation.

Use backend wishlist APIs.

Support:

- add
- remove
- list
- authenticated persistence
- loading
- error
- empty state

---

# 13. AUTHENTICATION

Implement a complete authentication flow:

```text
Register
 ↓
Login
 ↓
JWT/session
 ↓
Protected account
 ↓
Logout
```

Handle:

- invalid credentials
- expired authentication
- unauthorized responses
- logout
- protected routes
- session restoration
- loading/error states

The frontend must never assume authentication merely because local state says so.

Backend is authoritative.

---

# 14. ADMIN AUTHENTICATION — CRITICAL

Fix any hardcoded behavior such as:

```ts
isAdminLoggedIn = true
```

Admin must not be automatically logged in.

Correct flow:

```text
/admin
 ↓
Authentication check
 ↓
Not authenticated
 ↓
Admin login
 ↓
Backend authentication
 ↓
Role/permission validation
 ↓
Admin dashboard
```

Backend must enforce authorization.

---

# 15. ADMIN RBAC

Use the existing role/permission architecture.

Ensure permissions actually control admin access.

Do not display every admin module to every role.

---

# 16. CUSTOMER ACCOUNT

Replace hardcoded customer information in normal runtime mode.

Account must use backend data for:

```text
Profile
Orders
Wishlist
Addresses
Returns
Reviews
Security
Logout
```

Recommended structure:

```text
My Account

Overview
My Orders
Wishlist
Addresses
Returns
Reviews
Profile
Security
Logout
```

---

# 17. ADDRESSES

Use backend address APIs.

Support:

- list
- add
- edit
- delete
- default address
- validation
- checkout selection

Hardcoded demo addresses are allowed only as seed/demo data.

---

# 18. CHECKOUT — REAL END-TO-END FLOW

The UI can remain a multi-step checkout, but its operations must be real.

Required flow:

```text
Cart validation
      ↓
Address selection
      ↓
Shipping selection
      ↓
Backend price calculation
      ↓
Coupon validation
      ↓
Inventory validation
      ↓
Payment
      ↓
Payment result
      ↓
Order creation
      ↓
Inventory update
      ↓
Coupon usage
      ↓
Confirmation
```

Do not create a fake order only in React/local state.

---

# 19. ORDER CREATION — CRITICAL

The current local/mock `StoreContext.placeOrder()` behavior must not be the authoritative order flow.

Correct:

```text
Checkout UI
    ↓
Order/Checkout Repository
    ↓
Spring Boot
    ↓
Validate cart
    ↓
Validate price
    ↓
Validate stock
    ↓
Validate coupon
    ↓
Mock payment
    ↓
Create order
    ↓
Persist order
    ↓
Update inventory
    ↓
Persist payment
    ↓
Return real order number
```

Confirmation must display the actual backend-created order.

---

# 20. PAYMENT — CURRENT COLLEGE VERSION

Use the existing mock payment provider.

Support simulated:

- success
- failure
- declined
- timeout

Payment must be processed through the backend/provider abstraction.

Do **not** implement real Razorpay yet.

Keep the architecture ready for:

```text
MockPaymentProvider
        ↓
RazorpayPaymentProvider (later)
```

---

# 21. PRICE CALCULATION

Frontend may display estimates.

Backend must calculate the authoritative amount.

Do not blindly trust browser-submitted:

- subtotal
- discount
- shipping
- tax
- total

Required:

```text
Frontend estimate
       ↓
Backend recalculation
       ↓
Final authoritative total
       ↓
Payment
       ↓
Order
```

---

# 22. SHIPPING

Do not duplicate shipping business rules across multiple frontend files.

Use backend configuration/settings where appropriate for:

- shipping fee
- free shipping threshold
- delivery estimates
- shipping options

---

# 23. ORDERS

Replace mock orders in normal runtime mode.

Implement:

```text
My Orders
 ↓
GET /orders/my-orders
```

Support:

- order list
- order details
- items
- payment status
- shipping status
- address
- total
- tracking
- return eligibility

---

# 24. ORDER TRACKING

Use actual backend shipment/order status.

Example:

```text
Order Placed
     ↓
Payment Confirmed
     ↓
Processing
     ↓
Packed
     ↓
Shipped
     ↓
Out for Delivery
     ↓
Delivered
```

Do not invent the timeline only in frontend state.

---

# 25. RETURNS + REFUNDS

Connect customer returns to the backend.

Required:

```text
Delivered Order
      ↓
Return Item
      ↓
Reason
      ↓
Submit
      ↓
Review
      ↓
Approved / Rejected
      ↓
Pickup/Receipt
      ↓
Refund
```

Show actual status.

---

# 26. REVIEWS

Implement a proper review lifecycle:

```text
Delivered purchase
      ↓
Eligible
      ↓
Submit rating/review
      ↓
Backend verifies purchase
      ↓
Moderation if applicable
      ↓
Published
```

Do not create fake verified-purchase reviews.

---

# 27. ADMIN DASHBOARD — REAL DATA

Replace hardcoded/mock dashboard values.

Use backend-derived:

- revenue
- orders
- customers
- products
- inventory
- category sales
- payments
- returns
- refunds

Do not hardcode analytics such as:

```text
₹28,45,900
+18.4%
₹3,420
```

as runtime business data.

Backend should calculate them from database records.

---

# 28. ADMIN PRODUCT MANAGEMENT

Implement real CRUD:

```text
Create
Read
Update
Deactivate/Delete
```

Support:

- category
- variants
- SKU
- price
- discount
- stock
- images
- description
- specifications
- status

---

# 29. ADMIN INVENTORY

Use real backend inventory.

Support:

- current stock
- low stock
- out of stock
- stock adjustment
- variant-level inventory

Order placement must correctly update inventory.

---

# 30. ADMIN ORDERS / PAYMENTS / SHIPMENTS / RETURNS / REFUNDS

All admin operational screens must use backend data.

Do not keep screens that only look functional while reading mock constants.

---

# 31. ADMIN COUPONS

Customer:

```text
Enter coupon
 ↓
Backend validation
 ↓
Discount
```

Admin:

```text
Create
Update
Enable/Disable
Expiration
Usage limits
Minimum order
Discount rules
```

Backend is authoritative.

---

# 32. ADMIN CMS

Use the existing CMS architecture.

Where CMS APIs exist, connect storefront content to them.

Do not hardcode all CMS-controlled marketing content in React.

Use CMS for relevant:

- hero/marketing content
- announcements
- journal
- FAQ
- store settings

---

# 33. STYLING ARCHITECTURE

Clean up the inconsistent styling system.

There are currently custom CSS styles mixed with Tailwind-style classes.

Choose one deliberate styling approach based on the existing project.

Do not randomly add another CSS framework.

Do not leave classes such as:

```text
bg-card
text-foreground
border-border
text-muted-foreground
```

unless the styling system that defines them is actually configured and working.

Create consistent:

- design tokens
- typography
- spacing
- buttons
- inputs
- cards
- badges
- tables
- forms
- modals
- states

---

# 34. STOREFRONT UX

The homepage must feel like a real e-commerce store, not only a luxury editorial landing page.

Recommended structure:

```text
Announcement Bar
Header
Hero
Shop by Category
Featured Products
New Arrivals
Best Sellers
Promotional Banner
Why Choose Us
Customer Reviews
Brand Story
Journal
Newsletter
Footer
```

Keep the visual identity original.

---

# 35. SHOP PAGE

Implement:

- breadcrumb
- search
- filters
- sorting
- product count
- product grid
- pagination/load more
- loading state
- empty state
- error state

Desktop:

```text
Filters | Products
```

Mobile:

```text
[Filter] [Sort]
Products
```

---

# 36. PRODUCT CARD

Support:

- product image
- hover image if available
- wishlist
- badge
- product name
- rating
- review count
- price
- original price
- discount
- variant/color indicators
- stock state
- add-to-cart

Do not copy Flipkart's visual styling.

---

# 37. PRODUCT DETAIL PAGE

Support:

- image gallery
- product name
- rating
- reviews
- price
- discount
- variant selection
- SKU where appropriate
- stock
- quantity
- add to cart
- buy now
- wishlist
- delivery/pincode where applicable
- description
- specifications
- shipping
- returns
- reviews
- related products

---

# 38. CART UI

Build a complete commerce cart:

```text
Shopping Cart

Product
Variant
Price
Quantity
Subtotal
Remove

Coupon

Price Details
Subtotal
Discount
Shipping
Tax if applicable
Total

Proceed to Checkout
```

Handle:

- empty cart
- unavailable item
- quantity limits
- stock changes
- API failures
- loading

---

# 39. ERROR / LOADING / EMPTY STATES

Every important screen must have appropriate:

```text
Loading
Success
Empty
Error
```

Examples:

- no products
- empty cart
- empty wishlist
- no orders
- no returns
- no reviews
- backend unavailable
- payment failed
- invalid coupon
- out of stock

Never leave a blank page.

---

# 40. RESPONSIVE DESIGN

Test at:

```text
360px
390px
430px
768px
1024px
1280px
1440px+
```

The application must work properly on:

- mobile
- tablet
- desktop

Do not merely shrink desktop layouts.

---

# 41. PERFORMANCE

Check:

- image optimization
- lazy loading where appropriate
- unnecessary client components
- unnecessary API calls
- repeated requests
- unnecessary re-renders
- bundle size
- loading states
- caching where appropriate

---

# 42. SECURITY

Verify:

- JWT/session handling
- role authorization
- protected APIs
- admin authorization
- backend validation
- no secrets in frontend
- no hardcoded passwords
- safe environment variables
- CORS
- safe error responses

Never expose secrets through `NEXT_PUBLIC_*`.

---

# 43. ENVIRONMENT CONFIGURATION

Verify:

```text
.env
.env.example
backend/.env
backend/.env.example
```

Do not commit secrets.

Verify:

- database URL
- DB username/password
- JWT secret
- frontend API URL
- CORS
- payment configuration
- mock/production mode

---

# 44. DOCKER + NGINX

The actual project contains Docker/Nginx configuration.

Verify all referenced files exist and work together.

Check:

```text
Frontend
Backend
PostgreSQL
Nginx
Networks
Environment variables
Health checks
API routing
Frontend routing
```

Important:

Do not expose a Docker-internal hostname such as:

```text
http://backend:8080
```

as the browser-facing API URL.

Use either:

```text
https://api.yourdomain.com
```

or same-origin routing:

```text
https://yourdomain.com/api/
```

with Nginx proxying internally to:

```text
backend:8080
```

---

# 45. CI/CD

Inspect `.github/workflows/`.

Verify CI actually checks:

```text
Frontend install
Frontend lint/typecheck/build
Backend compile
Backend tests
```

Do not claim success without running it.

---

# 46. TESTING

Run the actual project commands.

Frontend, according to the project's scripts:

```text
npm ci
npm run lint
npm run build
```

plus existing frontend tests.

Backend:

```text
mvn test
```

or the project's configured equivalent.

Do not rely on old build artifacts or old documentation as proof.

---

# 47. END-TO-END CUSTOMER QA

Test this entire flow:

```text
Home
 ↓
Search
 ↓
Category
 ↓
Filter
 ↓
Sort
 ↓
Product
 ↓
Variant
 ↓
Wishlist
 ↓
Cart
 ↓
Login
 ↓
Cart Merge
 ↓
Address
 ↓
Coupon
 ↓
Checkout
 ↓
Mock Payment
 ↓
Backend Order
 ↓
Confirmation
 ↓
Orders
 ↓
Tracking
 ↓
Return
 ↓
Refund
 ↓
Review
```

Every step must actually work.

---

# 48. END-TO-END ADMIN QA

Test:

```text
Admin Login
 ↓
Dashboard
 ↓
Products
 ↓
Categories
 ↓
Inventory
 ↓
Orders
 ↓
Payments
 ↓
Shipments
 ↓
Returns
 ↓
Refunds
 ↓
Coupons
 ↓
Reviews
 ↓
CMS
 ↓
Customers
 ↓
Users
 ↓
Roles
 ↓
Settings
 ↓
Audit Logs
```

Every screen must use the appropriate real backend data.

---

# 49. NEGATIVE TESTING

Intentionally test:

- wrong password
- expired JWT
- customer accessing admin
- unauthorized admin role
- invalid product
- invalid variant
- out-of-stock item
- excessive quantity
- invalid coupon
- expired coupon
- empty-cart checkout
- invalid address
- payment failure
- payment timeout
- duplicate order submission
- backend unavailable
- malformed request

The application must show useful errors.

Never silently substitute mock data.

---

# 50. DATA CONSISTENCY

Verify:

```text
Product stock
    ↓
Cart
    ↓
Checkout
    ↓
Order
    ↓
Inventory
```

Also:

```text
Coupon
    ↓
Checkout
    ↓
Order
    ↓
Coupon usage
```

Also:

```text
Payment
    ↓
Order payment status
```

Also:

```text
Shipment
    ↓
Order status
```

Also:

```text
Return
    ↓
Refund
```

All state transitions must be consistent.

---

# 51. DEMO DATA

Seed/demo data is acceptable for the college project.

Use realistic seed data for:

- products
- categories
- variants
- customers
- addresses
- orders
- reviews
- coupons

But clearly separate:

```text
seed/demo data
```

from:

```text
runtime application data
```

---

# 52. DO NOT OVERENGINEER

This is a college project.

Do not introduce unnecessary:

- microservices
- Kafka
- Kubernetes
- Elasticsearch
- Redis
- complex cloud infrastructure

unless genuinely required.

The following is sufficient:

```text
Next.js
+
Spring Boot
+
PostgreSQL
+
Docker
+
REST
+
JWT/RBAC
```

---

# 53. PRESERVE WORKING FUNCTIONALITY

Before modifying any module:

1. Understand it.
2. Identify dependencies.
3. Fix the correct layer.
4. Preserve working backend APIs.
5. Reuse correct repository/service/domain implementations.
6. Remove obsolete code only after confirming it is unused.

Do not create duplicate implementations.

---

# 54. IMPLEMENTATION ORDER

Follow this order.

## Phase 1 — Full Audit

Understand the whole project.

## Phase 2 — Architecture Cleanup

Fix:

- duplicate logic
- state architecture
- repository usage
- mock fallbacks
- styling inconsistencies
- authentication state

## Phase 3 — Catalog

Fix:

- products
- categories
- search
- filters
- sorting
- pagination
- variants

## Phase 4 — Customer Commerce

Fix:

- authentication
- cart
- wishlist
- addresses
- coupons

## Phase 5 — Checkout

Fix:

- cart validation
- price calculation
- address
- shipping
- mock payment
- order creation

## Phase 6 — Orders

Fix:

- order history
- order details
- tracking
- returns
- refunds
- reviews

## Phase 7 — Admin

Connect every admin screen to real backend data.

## Phase 8 — CMS

Connect CMS-controlled content.

## Phase 9 — UI/UX

Only after functionality is stable.

## Phase 10 — QA

Perform full end-to-end testing.

## Phase 11 — Production Preparation

Verify:

- Docker
- Nginx
- CI/CD
- environment
- security
- build
- tests
- deployment configuration

---

# 55. FINAL UI/UX DIRECTION

After functional correctness is achieved, refine the UI to be:

- premium
- clean
- modern
- original
- professional
- conversion-focused
- responsive
- accessible

Again:

> **Do not clone Flipkart visually.**

Target:

```text
Flipkart-like shopping functionality
+
our own original brand design
```

The application should feel like a real commercial e-commerce website, not a CRUD college dashboard.

---

# 56. FINAL ACCEPTANCE CHECKLIST

## Customer

- [ ] Registration
- [ ] Login
- [ ] Logout
- [ ] Product browsing
- [ ] Categories
- [ ] Search
- [ ] Filters
- [ ] Sorting
- [ ] Pagination
- [ ] Product details
- [ ] Variants
- [ ] Wishlist
- [ ] Guest cart
- [ ] Customer cart
- [ ] Cart merge
- [ ] Addresses
- [ ] Coupons
- [ ] Checkout
- [ ] Mock payment
- [ ] Real backend order
- [ ] Inventory update
- [ ] Order history
- [ ] Order details
- [ ] Tracking
- [ ] Returns
- [ ] Refund status
- [ ] Reviews
- [ ] Account dashboard

## Admin

- [ ] Admin login
- [ ] RBAC
- [ ] Real dashboard data
- [ ] Product CRUD
- [ ] Categories
- [ ] Inventory
- [ ] Orders
- [ ] Payments
- [ ] Shipments
- [ ] Returns
- [ ] Refunds
- [ ] Coupons
- [ ] Reviews
- [ ] CMS
- [ ] Customers
- [ ] Users
- [ ] Roles
- [ ] Settings
- [ ] Audit logs

## Technical

- [ ] No silent mock fallback in normal mode
- [ ] No hardcoded business analytics
- [ ] No hardcoded authentication state
- [ ] No hardcoded customer runtime data
- [ ] Backend is source of truth
- [ ] Frontend repositories are actually used
- [ ] Authentication is correct
- [ ] Authorization is correct
- [ ] Environment variables are safe
- [ ] Docker works
- [ ] Nginx works
- [ ] CI/CD works
- [ ] Frontend build passes
- [ ] Backend tests pass
- [ ] Customer E2E flow passes
- [ ] Admin E2E flow passes
- [ ] Mobile QA passes
- [ ] Desktop QA passes

---

# 57. NO FAKE COMPLETION

Do not write:

```text
Completed
Working
Production Ready
100%
```

unless you actually verified it.

For every phase:

```text
Implement
 ↓
Run
 ↓
Test
 ↓
Verify
 ↓
Document
```

If something cannot be verified, explicitly report it.

---

# 58. FINAL DELIVERABLE

After completing the work, provide a concise report containing:

1. What was wrong.
2. What was fixed.
3. Which files/modules changed.
4. Which backend APIs are now connected.
5. Which mock/local implementations were removed or isolated.
6. Frontend build result.
7. Backend test result.
8. End-to-end QA result.
9. Remaining limitations, if any.
10. Exact commands to run the project locally.

Do not claim full completion until the application has actually been tested.

---

# 59. FINAL PRINCIPLE

**Do not stop after making the UI look better.**

The final application must be:

```text
FUNCTIONALLY CORRECT
+
BACKEND CONNECTED
+
DATA CONSISTENT
+
SECURE
+
RESPONSIVE
+
TESTED
+
PROFESSIONALLY DESIGNED
```

Most important rule:

> **Never make a fake UI appear functional when the underlying operation is not actually working.**

If an API exists, use it.

If a backend service exists, connect it.

If a database record should exist, persist it.

If a business rule exists, enforce it on the backend.

If something fails, show the real error instead of silently returning mock data.
