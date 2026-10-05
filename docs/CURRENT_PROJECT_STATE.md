# RÓRA Luxury Leather Goods — Current Project State

**Document Status:** Authoritative & Verified  
**Date of Audit & Certification:** October 5, 2026  
**Build & Verification Status:** 100% Passing (Backend 205/205 Tests, Frontend TS/ESLint 0 errors, Next.js 18/18 Routes Generated)

---

## 1. Authoritative Architecture & Stack

### Frontend
- **Framework:** Next.js 16 App Router (React 19, TypeScript)
- **Styling:** Vanilla CSS luxury editorial design system with bespoke typography and custom layout grid
- **Animations:** GSAP 3.12 + Lenis Smooth Scroll with reduced-motion support and memory-leak lifecycle cleanup
- **API Client:** Axios client (`src/data/apiClient.ts`) with Bearer token authentication, X-Session-ID guest tracking, and global response interception

### Backend
- **Framework:** Spring Boot 3.4.3 (Java 21)
- **Security:** Spring Security 6 with stateless JWT authentication, password hashing via BCrypt, and method-level `@PreAuthorize` RBAC
- **Data Access:** Spring Data JPA / Hibernate with transactional consistency
- **Database Migrations:** Flyway (V1 through V14 baseline migrations)

### Database
- **Engine:** PostgreSQL 18.x / 16-alpine (Containerized & Native compatible)
- **Connection Pool:** HikariCP with connection validation and leak detection

---

## 2. Production Mock Fallback Removal

All runtime business fallback logic that previously created fake orders, simulated payments, or mock authenticated sessions on backend failure has been strictly eliminated:
- **Order Placement:** `orderRepository.placeOrder` delegates directly to `/api/v1/checkout/place-order`. On failure, it throws genuine server errors allowing the UI toast/error boundary to present clear error states and retry options.
- **Authentication:** `authRepository.login` and `authRepository.register` communicate strictly with `/api/v1/auth/login` and `/api/v1/auth/register`. No simulated JWT tokens or phantom user sessions are created on network or credentials failure.
- **Reviews, Categories, Coupons, Content:** Strict separation between live backend data and explicit developer mock mode (`NEXT_PUBLIC_USE_MOCK_DATA=true`). In production (`NEXT_PUBLIC_USE_MOCK_DATA=false`), real database endpoints are always enforced.

---

## 3. Canonical 5-Role RBAC Model

The system enforces a strict 5-role Role-Based Access Control model across the database, JWT claims, Spring Security authorities, and frontend admin guards:

| Role Identifier | Description | Allowed Privileges |
|---|---|---|
| `ROLE_ADMIN` | Executive Super Administrator | Full unconstrained access across all 14 administrative modules, roles, audit logs, and settings |
| `ROLE_MANAGER` | General Operations Manager | Operations, catalog management, inventory adjustments, orders, shipments, customer management |
| `ROLE_PRODUCT_MANAGER` | Catalog & Content Specialist | Product catalog CRUD, categories, media, inventory stock updates, CMS content |
| `ROLE_ORDER_MANAGER` | Fulfillment & Logistics Specialist | Order lifecycle, tracking numbers, shipping consignments, returns, refunds processing |
| `ROLE_CUSTOMER` | Registered Patron / Client | Catalog browsing, personal cart, wishlist, placing orders, writing reviews, submitting returns |

---

## 4. Reconciled Checkout & Financial Logic

All prices, shipping tiers, discounts, taxes, and final totals are calculated and validated authoritatively on the Spring Boot backend (`OrderService.java`):
- **Pricing:** Server-side retrieval of unit prices from the database; client-submitted price tampering is completely ignored.
- **Coupons:** Authoritative coupon validation (`CouponService.java`) with start/end date enforcement, usage limits, and minimum cart thresholds.
- **Shipping Rules:** Free standard shipping on orders ≥ ₹1,999; flat ₹199 shipping below threshold.
- **Taxes:** Inclusive GST structure matching Indian luxury retail standards.
- **Example Reconciled Checkout (1x The Campus Explorer with WELCOME15):**
  - Subtotal: ₹3,999.00
  - Coupon Discount (15%): ₹599.85
  - Shipping Fee (Subtotal ≥ ₹1,999): ₹0.00
  - Total Payable: **₹3,399.15** (Verified via automated JUnit test `OrderServiceTest.java`)

---

## 5. 14 Verified Admin Modules

All 14 backoffice modules are backed by real JPA repositories, Spring Security `@PreAuthorize` controllers, and automated audit logging:
1. **Executive Analytics Dashboard** (`/api/v1/admin/dashboard/overview`)
2. **Product Catalog CRUD** (`/api/v1/admin/products`)
3. **Category Management** (`/api/v1/admin/categories`)
4. **Inventory & Stock Movements** (`/api/v1/admin/inventory`)
5. **Customer Management** (`/api/v1/admin/customers`)
6. **Order Processing & Statuses** (`/api/v1/admin/orders`)
7. **Payment Ledger & Audit** (`/api/v1/admin/payments`)
8. **Shipment & AWB Logistics** (`/api/v1/admin/shipments`)
9. **Returns & Physical Inspection** (`/api/v1/admin/returns`)
10. **Refunds & Financial Settlements** (`/api/v1/admin/refunds`)
11. **Promotional Coupons** (`/api/v1/admin/coupons`)
12. **Review Moderation** (`/api/v1/admin/reviews`)
13. **CMS Content & Banners** (`/api/v1/admin/cms`)
14. **Settings, Operators & Immutable Audit Trail** (`/api/v1/admin/settings`, `/api/v1/admin/users`, `/api/v1/admin/audit-logs`)

---

## 6. Frontend Routes & Dynamic Rendering

Next.js 16 App Router statically prerenders and dynamically serves all luxury customer and admin routes:
- `/` — Flagship Editorial Homepage
- `/shop` — Filterable Product Catalog
- `/product/[slug]` — Dynamic Product Detail Page with Color Variants & Stock Validation
- `/category/[slug]` — Curated Category Showcase
- `/search` — Full-text debounced product search
- `/cart` — Cart management & Coupon application
- `/checkout` — Multi-step checkout with server price validation
- `/confirmation` — Verified Order Confirmation & Logistics Tracking
- `/account` — Customer Profile & Saved Addresses
- `/orders` & `/orders/[orderId]` — Customer Order History & Real-time Live Tracking
- `/wishlist` — Customer Wishlist with 1-click cart migration
- `/returns` — Self-service Return & Refund Request Portal
- `/about`, `/journal`, `/journal/[slug]`, `/faq`, `/contact`, `/shipping` — Editorial storytelling & customer service
- `/admin` — Unified 14-module RBAC Backoffice

---

## 7. Secrets & Security Configuration

- **Environment Placeholders:** All `.env.example` files sanitized with explicit `CHANGE_ME_...` placeholders.
- **Production Secret Injection:** In production profile (`application-prod.yml`), `JWT_SECRET` must be injected via runtime environment variables.
- **API Defense:** SQL injection protected via Hibernate Parameterized Queries, XSS prevented by React JSX automatic escaping, CORS configured strictly for authorized origins.

---

## 8. Test & Build Verification Summary

| Gate | Tool / Command | Result | Verification Notes |
|---|---|---|---|
| Backend Unit & Integration Tests | `mvn clean test` | **205 / 205 Passed (0 Failures, 0 Errors, 0 Skipped)** | Full coverage of RBAC, Checkout, Inventory, Refunds, Smoke tests |
| Backend Production Package | `mvn package -DskipTests=true` | **BUILD SUCCESS** | Generates executable fat JAR with nested dependencies |
| Frontend TypeScript Check | `npx.cmd tsc --noEmit` | **0 Errors** | Strict TypeScript compilation across all models and views |
| Frontend Linting | `npx.cmd eslint src/` | **0 Errors / 0 Warnings** | Clean code formatting and import structure |
| Frontend Production Build | `npm.cmd run build` | **18 / 18 Routes Built** | Next.js 16 Turbopack optimized static page generation in 827ms |
| Live 14-Module REST Mutations | PowerShell Automation Script | **14 / 14 Verified 100%** | Real REST mutations with audit trail verification |
