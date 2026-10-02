# RÓRA — Luxury Bags & Carry Essentials
## Project Implementation Progress & Architecture State

**Last Updated:** October 2, 2026  
**Architecture:** Next.js 16 (App Router) Frontend + Spring Boot 3.4+ (Java 21 LTS) + PostgreSQL 18.4 + Maven + Docker  
**Git Repository:** `https://github.com/sojalrajurkar-blip/BAGS_ECommerce.git` (Branch: `main`)  
**Authoritative Master Specifications:**
- Master Prompt: [`prompts/RORA_Antigravity_Backend_Implementation_Prompt.md`](file:///d:/ProjectFolder/RORA/prompts/RORA_Antigravity_Backend_Implementation_Prompt.md)
- Software Requirements Specification: [`prompts/RORA_Backend_SRS.md`](file:///d:/ProjectFolder/RORA/prompts/RORA_Backend_SRS.md)

**Backend Status:** Phases 0 through 12 Completed (182/182 tests passing, 100% pass rate). Ready for Phase 13 (Admin Platform).

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
  - `GET /api/v1/admin/inventory/summary` — Key KPI health metrics.
  - `GET /api/v1/admin/inventory` — Paginated inventory search by keyword, status, and category.
  - `GET /api/v1/admin/inventory/low-stock` — Real-time stream of low-stock alerts.
  - `GET /api/v1/admin/inventory/{idOrSku}` — Single inventory item details.
  - `POST /api/v1/admin/inventory/adjust` — Atomic stock adjustment with automated movement ledger logging.
  - `POST /api/v1/admin/inventory/batch-adjust` — Transactional multi-SKU batch adjustments.
  - `PUT /api/v1/admin/inventory/{idOrSku}/threshold` — Update low stock alerts threshold and warehouse bin locations.
  - `GET /api/v1/admin/inventory/movements` — Global paginated movement audit ledger.
  - `GET /api/v1/admin/inventory/{idOrSku}/movements` — Per-item SKU movement audit trail.
- Flyway migration `V7__seed_inventory_and_movements.sql` with full master inventory ledger.

#### ✅ Phase 8: Local Mock Payments, Transactions Ledger & Refund Engine
- `Payment` and `PaymentTransaction` JPA domain entities with comprehensive payment lifecycle management.
- Complete payment states: `INITIATED`, `PENDING`, `SUCCESS`, `FAILED`, `CANCELLED`, `REFUNDED`.
- Multi-rail payment simulation provider (`MockPaymentProvider`):
  - Supported methods: `CARD`, `UPI`, `NETBANKING`, `WALLET`, `COD`.
  - Realistic latency and idempotency handling via `idempotencyKey`.
  - Deterministic testing simulations: `APPROVE`, `DECLINE_INSUFFICIENT_FUNDS`, `DECLINE_EXPIRED_CARD`, `TIMEOUT`, `FRAUD_ALERT`.
- Automatic synchronization with `Order` and `OrderTimelineEvent`.
- Full refund engine: supports full and partial refunds with balance tracking, ledger audit logging, and automated order timeline status updates.
- Public & Customer Payment APIs (`/api/v1/payments`):
  - `POST /api/v1/payments/initiate` — Initiate checkout payment session.
  - `POST /api/v1/payments/process` — Process simulated payment attempt.
  - `GET /api/v1/payments/{paymentId}` — Retrieve payment and transaction ledger by payment ID.
  - `GET /api/v1/payments/order/{orderIdOrNumber}` — Lookup payment record for an order.
- Admin Payment Management & Analytics APIs (`/api/v1/admin/payments`):
  - `GET /api/v1/admin/payments/summary` — High-level payment financial KPIs.
  - `GET /api/v1/admin/payments` — Paginated search and filtering by status, method, order ID, and date range.
  - `POST /api/v1/admin/payments/{paymentId}/refund` — Admin refund execution with reason and transaction logging.
- Flyway migration `V8__seed_payments.sql` seeding historical payment transactions.

#### ✅ Phase 9: Shipping, Consignments, Carrier Tracking & Milestone Events Engine
- `Shipment` and `ShipmentEvent` JPA domain entities with complete delivery lifecycle tracking.
- Full consignment statuses: `CREATED`, `MANIFESTED`, `PICKED_UP`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `FAILED_DELIVERY`, `RETURNED_TO_ORIGIN`, `CANCELLED`.
- Dual tracking lookup engine: supports live lookup by AWB tracking number or Order number (`#RRA...` or raw digits) with automatic fallback synthesis from order timeline.
- Deep bidirectional synchronization with `Order` and `OrderTimelineEvent`.
- Public & Customer Shipping APIs (`/api/v1/shipments`):
  - `GET /api/v1/shipments/track/{trackingCodeOrOrderNumber}` — Real-time live tracking with milestone events and package contents.
  - `GET /api/v1/shipments/order/{orderIdOrNumber}` — List all shipments associated with an order.
  - `GET /api/v1/shipments/{id}` — Get single shipment consignment details.
  - `GET /api/v1/shipments/awb/{awbNumber}` — Lookup shipment by AWB code.
- Admin Consignments & Carrier Operations APIs (`/api/v1/admin/shipments`):
  - `GET /api/v1/admin/shipments/summary` — Shipping KPI metrics.
  - `GET /api/v1/admin/shipments` — Paginated search by AWB, order #, customer, courier partner, destination, and status filter.
  - `POST /api/v1/admin/shipments` — Create and dispatch new consignment for an order.
  - `POST /api/v1/admin/shipments/{id}/events` — Record new tracking milestone event.
  - `PUT /api/v1/admin/shipments/{id}/status` — Quick status transition.
- Flyway migration `V9__seed_shipments.sql` seeding historical consignments and realistic milestone scan events.

#### ✅ Phase 10: Returns, Refunds, Quality Inspection & Payment Linkage Engine
- `ReturnRequest`, `ReturnItem`, and `RefundRecord` JPA domain entities with robust return lifecycle tracking.
- Complete state machines: `ReturnStatus`, `InspectionStatus`, and `RefundStatus`.
- Direct linkage to `Payment` & `PaymentTransaction` double-entry ledger.
- Customer & Public Return APIs (`/api/v1/returns`):
  - `POST /api/v1/returns` — Submit return request for an order.
  - `GET /api/v1/returns/my-returns` — List authenticated customer return requests.
  - `GET /api/v1/returns/{id}` — Get single return request details.
  - `GET /api/v1/returns/order/{orderIdOrNumber}` — Lookup return requests for an order.
- Admin Returns & Refunds Operations APIs (`/api/v1/admin/returns`, `/api/v1/admin/refunds`):
  - `GET /api/v1/admin/returns/summary` — Returns & financial reimbursement KPI metrics.
  - `GET /api/v1/admin/returns` — Paginated search.
  - `GET /api/v1/admin/returns/{id}` — Full return details with item breakdown and linked refund.
  - `POST /api/v1/admin/returns/{id}/approve` — Approve return, set inspection grade, and trigger automatic banking refund.
  - `POST /api/v1/admin/returns/{id}/reject` — Reject return with policy failure rationale.
  - `PUT /api/v1/admin/returns/{id}/inspection` — Physical atelier inspection grade update.
  - `GET /api/v1/admin/refunds` — Paginated audit list of all refund financial transactions.
  - `POST /api/v1/admin/refunds` — Issue manual / goodwill financial reimbursement.
- Flyway migration `V10__seed_returns_and_refunds.sql` seeding historical return requests and refund records.

#### ✅ Phase 11: Reviews & Ratings Engine
- `Review` JPA domain entity with complete moderation lifecycle (`PUBLISHED`, `PENDING_MODERATION`, `FLAGGED`, `ARCHIVED`, `REJECTED`).
- Automated verified purchase badge detection via customer order history.
- Real-time bidirectional product rating and review count recalculation.
- Public & Customer Review APIs (`/api/v1/reviews`):
  - `POST /api/v1/reviews` — Submit customer product review with ratings, title, and comments.
  - `GET /api/v1/reviews/product/{productIdOrSlug}` — Paginated reviews for PDP.
  - `GET /api/v1/reviews/product/{productIdOrSlug}/summary` — Aggregate star breakdown (1..5 stars) and average score for PDP.
  - `GET /api/v1/reviews/featured` — Curated editorial customer reviews.
  - `GET /api/v1/reviews/my` — Current customer's authored reviews.
  - `GET /api/v1/reviews/{id}` — Single review lookup.
  - `POST /api/v1/reviews/{id}/helpful` — Upvote review helpfulness counter.
- Admin Review Moderation & Analytics APIs (`/api/v1/admin/reviews`):
  - `GET /api/v1/admin/reviews/summary` — Dashboard KPI metrics (total, published, pending, flagged, archived, average rating).
  - `GET /api/v1/admin/reviews` — Paginated search by text and status filter.
  - `GET /api/v1/admin/reviews/{id}` — Full review moderation view.
  - `PUT /api/v1/admin/reviews/{id}/moderate` — Moderate status, update editorial feature flag, and save moderation notes.
  - `PATCH /api/v1/admin/reviews/{id}/status` — Status transition shortcut.
  - `DELETE /api/v1/admin/reviews/{id}` — Remove review and adjust product metrics.
- Flyway migration `V11__seed_reviews.sql` seeding initial verified customer and editorial reviews (`rev-1`, `rev-2`, `rev-3`).
- Test suite passing: 153/153 tests passing.

#### ✅ Phase 12: CMS, Journal, FAQs & Store Operational Settings Engine
- `CmsContent`, `JournalArticle`, `FaqItem`, and `StoreSetting` JPA domain entities with complete editorial content and key-value configuration lifecycle.
- **Homepage CMS Engine:**
  - Dynamic announcement bar (toggleable status, marquee text, CTA link).
  - Editorial hero banner (eyebrows, headlines, sub-copy, dual CTAs, high-res background assets).
  - Atelier craftsmanship feature stories with structured JSONB mapping.
- **Editorial Journal Publishing Engine:**
  - Automated slug generation with Unicode normalization and collision prevention.
  - Multi-category article categorization (`Travel & Mobility`, `Philosophy & Lifestyle`, `Craftsmanship & Materials`, `Product Stories`).
  - Read-time estimation, Markdown/HTML content rendering, and article tagging.
  - Paginated search by category and keyword query.
- **Grouped Store FAQs Knowledge Base:**
  - Dynamic categorization (`General & Craftsmanship`, `Shipping & Delivery`, `Returns & Warranty`).
  - Dual DTO compatibility mapping `question`/`answer` and frontend shorthand `q`/`a`.
  - Display sorting, toggleable active visibility, and full admin CRUD.
- **Store Operational Settings Engine:**
  - Key-value configuration engine with type definitions (`STRING`, `NUMBER`, `BOOLEAN`, `JSON`).
  - Real-time resolution of critical e-commerce parameters: `freeShippingThreshold` (₹1,999), `standardShippingFee` (₹199), `currency` (INR ₹), `supportEmail`, `supportPhone`, `warehouseAddress`, `taxRate` (18% GST), `inventoryAlertThreshold` (5).
  - Single-key and batch update APIs protected by `SETTINGS_MANAGE` / `ROLE_ADMIN` RBAC.
- **Public & Storefront APIs (`/api/v1/cms`, `/api/v1/settings`):**
  - `GET /api/v1/cms/content` — Homepage hero banner, announcement bar, and craftsmanship story.
  - `GET /api/v1/cms/journal` — List journal articles with category filter.
  - `GET /api/v1/cms/journal/{slugOrId}` — Single editorial story view.
  - `GET /api/v1/cms/faqs` — Grouped FAQ categories for customer support.
  - `GET /api/v1/settings` & `GET /api/v1/settings/public` — Store operational parameters.
- **Admin CMS & Settings Operations APIs (`/api/v1/admin/cms`, `/api/v1/admin/settings`):**
  - `GET /api/v1/admin/cms/content`, `PUT /api/v1/admin/cms/content` — Homepage CMS backoffice management.
  - `GET /api/v1/admin/cms/journal`, `GET /api/v1/admin/cms/journal/{id}` — Editorial article review.
  - `POST /api/v1/admin/cms/journal`, `PUT /api/v1/admin/cms/journal/{id}`, `DELETE /api/v1/admin/cms/journal/{id}` — Article authoring and publishing.
  - `GET /api/v1/admin/cms/faqs`, `POST /api/v1/admin/cms/faqs`, `PUT /api/v1/admin/cms/faqs/{id}`, `DELETE /api/v1/admin/cms/faqs/{id}` — FAQ knowledge base management.
  - `GET /api/v1/admin/settings`, `PUT /api/v1/admin/settings` — Structured store settings configuration.
  - `GET /api/v1/admin/settings/all`, `GET /api/v1/admin/settings/{key}`, `PUT /api/v1/admin/settings/{key}` — Raw key-value setting updates.
- **Flyway Migration `V12__seed_cms_journal_faqs_settings.sql`:**
  - Creates `faqs` schema table with indexes.
  - Seeds canonical homepage editorial CMS content.
  - Seeds 4 luxury journal articles (`art-1` to `art-4`).
  - Seeds 9 categorized FAQ questions & answers (`faq-1` to `faq-9`).
  - Seeds master store settings (`set-1` to `set-11`).
- **Comprehensive Test Suite:** **182/182 tests passing (100% pass rate)**.

---

### 2. Canonical Roadmap (Phases 13 — 17)

1. **Phase 13: Admin Platform** — Consolidated admin dashboard, analytics KPIs, audit logs, user management, and granular permissions matrix.
2. **Phase 14: Frontend Repository Migration** — Wire Next.js repository layer to Spring Boot backend APIs, preserve all animations/UI.
3. **Phase 15: Full Local QA** — End-to-end customer and admin test matrix, failure scenarios, concurrency checks.
4. **Phase 16: Production Preparation** — Docker containerization, CI/CD pipelines, cloud PostgreSQL plan, secrets management.
5. **Phase 17: Production Deployment** — Deployment execution and live smoke test verification.

---

### 3. Git Commit History Summary

- `8be8df0` — *feat(backend): implement Phase 0 & Phase 1 backend foundation, database schema, entity models, and OpenAPI docs*
- `068b09e` — *feat(auth): implement Phase 2 JWT authentication, 5-role RBAC, and security endpoints*
- `29dadaf` — *feat(catalog): implement Phase 3 catalog domain, products, categories, variants, and admin CRUD*
- `b8a9582` — *feat(shopping): implement Phase 4 shopping cart, wishlist, and coupon engine*
- `fcd00d6` — *feat(order): implement Phase 5 checkout, orders, 5-step timeline tracking & admin fulfillment*
- `15a2c07` — *feat(customer): implement Phase 6 customer accounts, saved addresses, profile management & Admin Customer 360*
- `97dd17b` — *feat(inventory): implement Phase 7 inventory management, warehousing, stock movements ledger & low-stock alerts*
- `7ef613d` — *feat(payment): implement Phase 8 local mock payments, transactions ledger & refund workflows*
- `ffa184b` — *feat(shipping): implement Phase 9 shipments, carrier tracking, milestone events & admin dispatch operations*
- `28db7ac` — *feat(returns): implement Phase 10 returns, refunds, inspection workflows & payment linkage*
- `36af7c8` — *feat(reviews): implement Phase 11 customer reviews, rating summaries, helpful voting & admin moderation*
- `2c31162` — *feat(cms-settings): implement Phase 12 CMS homepage content, editorial journal, FAQs & store settings*

