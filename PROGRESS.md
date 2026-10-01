# RÓRA — Luxury Bags & Carry Essentials
## Project Implementation Progress & Architecture State

**Last Updated:** October 1, 2026  
**Architecture:** Next.js 16 (App Router) Frontend + Spring Boot 3.4+ (Java 21 LTS) + PostgreSQL 18.4 + Maven + Docker  
**Git Repository:** `https://github.com/sojalrajurkar-blip/BAGS_ECommerce.git` (Branch: `main`)  
**Backend Status:** 7 Phases 100% Completed, Tested (88/88 tests passing), and Verified. Ready for Phase 8.

---

### 1. Backend Implementation Progress by Phase

#### ✅ Phase 0: Audit & Architecture Foundation
- Audited Next.js 16 frontend contracts (`StoreContext`, `repositories`, `domain.ts`).
- Created Spring Boot Maven project structure, `pom.xml` with Spring Boot 3.4.3, Java 21 LTS, Spring Security, Spring Data JPA, Flyway, PostgreSQL driver, and JWT support (`jjwt-api`).
- Configured dev & test application profiles (`application-dev.yml`, `application-test.yml`).

#### ✅ Phase 1: Database Architecture & Core System
- Configured PostgreSQL 18.4 connection with HikariCP connection pooling (`RoraHikariCP`).
- Defined complete Base Entity hierarchy (`BaseEntity` with UUID keys, `createdAt`, `updatedAt`).
- Implemented global `ApiResponse<T>`, unified error handling (`GlobalExceptionHandler`), and custom exceptions (`ResourceNotFoundException`, `BadRequestException`, `UnauthorizedException`).
- Integrated Springdoc OpenAPI 3 / Swagger (`/swagger-ui.html` and `/v3/api-docs`).
- Created Flyway migration `V1__initial_schema.sql` defining 16 normalized tables:
  - `users`, `roles`, `permissions`, `user_roles`, `role_permissions`
  - `categories`, `products`, `product_variants`, `product_images`
  - `coupons`, `coupon_usages`, `cart_items`
  - `orders`, `order_items`, `order_timeline_events`
  - `customers`, `addresses`, `reviews`, `audit_logs`, `store_settings`, `cms_content`

#### ✅ Phase 2: Authentication, JWT & 5-Role RBAC Security
- Implemented stateless JWT engine (`JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`).
- Seeded canonical roles (`ROLE_CUSTOMER`, `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_PRODUCT_MANAGER`, `ROLE_ORDER_MANAGER`) and granular permissions via `V2__seed_roles_and_admin.sql`.
- Built authentication endpoints:
  - `POST /api/v1/auth/register` — Customer self-registration with password hashing (BCrypt 12).
  - `POST /api/v1/auth/login` — Email/password login with JWT token issuance.
  - `POST /api/v1/auth/refresh` — Token refresh rotation.
  - `GET /api/v1/auth/me` — Current authenticated user profile with roles & permissions.

#### ✅ Phase 3: Catalog Domain (Categories, Products, Search & Admin CRUD)
- Entities & Repositories: `Category`, `Product`, `ProductVariant`, `ProductImage`.
- Multi-criteria search and filter engine (`ProductSpecification`):
  - Filter by category slug, search query, price ranges (min/max), stock availability, badges (`Best Seller`, `New Arrival`, `Curated`, `Featured`).
  - Sort by `featured`, `price-low-to-high`, `price-high-to-low`, `newest`, `rating`.
- Public & Admin Catalog APIs:
  - `GET /api/v1/categories`, `GET /api/v1/categories/{slug}`
  - `GET /api/v1/products`, `GET /api/v1/products/{idOrSlug}`, `GET /api/v1/products/featured`, `GET /api/v1/products/new-arrivals`
  - `POST /api/v1/admin/products`, `PUT /api/v1/admin/products/{id}`, `DELETE /api/v1/admin/products/{id}`, `PATCH /api/v1/admin/products/{id}/stock`
- Seeded comprehensive luxury catalog via `V3__seed_categories_and_products.sql` with real pricing and high-res assets.

#### ✅ Phase 4: Shopping Experience (Persistent Cart, Wishlist & Coupons)
- **Cart Engine:** Supports authenticated users and anonymous guest sessions (`X-Session-ID` / cookie headers) with seamless login merge.
  - `GET /api/v1/cart`, `POST /api/v1/cart/items`, `PUT /api/v1/cart/items/{id}`, `DELETE /api/v1/cart/items/{id}`, `DELETE /api/v1/cart`
- **Wishlist Engine:** Customer-authenticated wishlist toggle and retrieval.
  - `GET /api/v1/wishlist`, `POST /api/v1/wishlist/{productId}`, `DELETE /api/v1/wishlist/{productId}`
- **Coupons Engine:** Multi-rule validation (percentage/fixed discounts, minimum spend, expiry, usage limits, per-user limits).
  - `POST /api/v1/cart/apply-coupon`, `POST /api/v1/coupons/validate`, `GET /api/v1/coupons/active`, Admin CRUD endpoints.
  - Seeded `RORA10`, `WELCOME15`, `BESPOKE500` via `V4__seed_coupons.sql`.

#### ✅ Phase 5: Checkout, Orders & Fulfilment Engine
- Complete checkout pipeline supporting both direct "Buy Now" requests and cart-based checkout.
- Automated inventory deduction and coupon usage recording.
- Dynamic shipping calculation: free shipping for orders >= ₹1,999; ₹199 standard fee otherwise.
- 5-step interactive fulfillment timeline (`Order Placed` -> `Payment Verified` -> `Dispatched from Hub` -> `Out for Delivery` -> `Delivered`).
- Public tracking: `GET /api/v1/orders/track/{orderNumber}` (supports `#RRA...` or raw digits).
- Customer & Admin Order APIs:
  - `POST /api/v1/checkout/place-order`
  - `GET /api/v1/orders/my-orders`, `GET /api/v1/orders/{idOrNumber}`, `PUT /api/v1/orders/{idOrNumber}/cancel`
  - `GET /api/v1/admin/orders`, `PUT /api/v1/admin/orders/{id}/status`, `PUT /api/v1/admin/orders/{id}/tracking`
- Seeded historical luxury orders (`#RRA89241`, `#RRA89105`, `#RRA88940`) via `V5__seed_orders.sql`.

#### ✅ Phase 6: Customer Accounts, Addresses, Profiles & Admin Customer 360
- `Customer` and `CustomerAddress` JPA domain entities with multi-address management.
- Customer Account Endpoints:
  - `GET /api/v1/account/profile` — Authenticated profile with order history metrics and tier info.
  - `PUT /api/v1/account/profile` — Update personal profile, name, phone.
  - `PUT /api/v1/account/password` — Secure password change with BCrypt verification.
  - `GET /api/v1/account/addresses` — List saved shipping & billing addresses.
  - `POST /api/v1/account/addresses` — Save new address with automatic default handling.
  - `PUT /api/v1/account/addresses/{id}` — Update address.
  - `DELETE /api/v1/account/addresses/{id}` — Delete address with auto-fallback for default.
  - `PUT /api/v1/account/addresses/{id}/default` — Set primary address.
- Admin Customer 360:
  - `GET /api/v1/admin/customers` — Paginated search by name, email, phone, and VIP tier.
  - `GET /api/v1/admin/customers/{id}` — Full 360 customer profile with lifetime value, orders, and addresses.
  - `PUT /api/v1/admin/customers/{id}/tier` — Upgrade/assign customer VIP tiers.
- Seeded client profiles and addresses across major cities via `V6__seed_customers.sql`.

#### ✅ Phase 7: Inventory Management, Warehousing & Stock Movements Engine
- `Inventory` and `InventoryMovement` JPA domain entities with multi-facility warehousing support (`warehouseLocation`, `binLocation`).
- Audit-proof stock movement ledger recording `RESTOCK`, `SALE`, `RETURN`, `ADJUSTMENT`, `DAMAGE`, `RESERVATION`, and `RELEASE_RESERVATION`.
- Dynamic low-stock threshold alert system and real-time synchronization with `Product` and `ProductVariant` catalog stock.
- Integration with `OrderService` for automatic sale movement recording on checkout and restoral movements on order cancellation.
- Admin Warehousing & Stock Management APIs (`/api/v1/admin/inventory`):
  - `GET /api/v1/admin/inventory/summary` — Key KPI health metrics (total SKUs, available units, reserved units, low-stock count, out-of-stock count, recent movements).
  - `GET /api/v1/admin/inventory` — Paginated inventory search by keyword, status (`IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK`), and category.
  - `GET /api/v1/admin/inventory/low-stock` — Real-time stream of low-stock alerts.
  - `GET /api/v1/admin/inventory/{idOrSku}` — Single inventory item details.
  - `POST /api/v1/admin/inventory/adjust` — Atomic stock adjustment with automated movement ledger logging.
  - `POST /api/v1/admin/inventory/batch-adjust` — Transactional multi-SKU batch adjustments.
  - `PUT /api/v1/admin/inventory/{idOrSku}/threshold` — Update low stock alerts threshold and warehouse bin locations.
  - `GET /api/v1/admin/inventory/movements` — Global paginated movement audit ledger.
  - `GET /api/v1/admin/inventory/{idOrSku}/movements` — Per-item SKU movement audit trail.
- Flyway migration `V7__seed_inventory_and_movements.sql` with full master inventory ledger and baseline restock logs.
- Full test suite passing: **88/88 tests passing (100% pass rate)**.

---

### 2. Next Session Plan: Phase 8 & Beyond

1. **Phase 8: Store Operations & Fulfillment Subsystems**
   - Payments ledger (`PaymentRecord` with mock UPI/Card/NetBanking simulator).
   - Shipments tracking (`ShipmentRecord` with carrier dispatching).
   - Returns & Refunds workflow (`ReturnRecord`, `RefundRecord` with inspection lifecycle).
2. **Phase 9: Reviews, Ratings & Social Proof Engine**
   - Product reviews with verified buyer badges and moderation workflow.
3. **Phase 10: Editorial CMS, Journal & Store Settings**
   - CMS content management (journal articles, FAQ, hero banners).
   - Store settings key-value store (tax rates, free shipping thresholds, contact metadata).
4. **Phase 11: Tamper-Evident Security Audit Logging & Admin User RBAC Management**
   - Structured audit log recording admin mutations with actor ID, IP address, and payload diffs.
5. **Phase 12: Next.js Frontend Integration & End-to-End Verification**
   - Wire Next.js repository layer to Spring Boot backend API.
   - Verify SSR/CSR hydration, checkout flow, and admin console against live PostgreSQL database.

---

### 3. Git Commit History Summary

- `8be8df0` — *feat(backend): implement Phase 0 & Phase 1 backend foundation, database schema, entity models, and OpenAPI docs*
- `068b09e` — *feat(auth): implement Phase 2 JWT authentication, 5-role RBAC, and security endpoints*
- `29dadaf` — *feat(catalog): implement Phase 3 catalog domain, products, categories, variants, and admin CRUD*
- `b8a9582` — *feat(shopping): implement Phase 4 shopping cart, wishlist, and coupon engine*
- `fcd00d6` — *feat(order): implement Phase 5 checkout, orders, 5-step timeline tracking & admin fulfillment*
- `15a2c07` — *feat(customer): implement Phase 6 customer accounts, saved addresses, profile management & Admin Customer 360*
- `97dd17b` — *feat(inventory): implement Phase 7 inventory management, warehousing, stock movements ledger & low-stock alerts*
