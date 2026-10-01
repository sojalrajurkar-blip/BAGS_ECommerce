# RÓRA — Backend Architecture Proposal (Phase 0)

## 1. Executive Summary

This document establishes the architectural foundation for the **RÓRA — Luxury Bags & Carry Essentials** backend. The target is a production-grade, local-first **Java 21 + Spring Boot 3.4+ + PostgreSQL 18** modular monolith engineered to seamlessly integrate with the existing Next.js 16 + React 19 + TypeScript frontend.

---

## 2. Technology Stack

| Layer | Component | Version / Specification | Rationale |
|---|---|---|---|
| **Runtime & Language** | Java SE Development Kit | Java 21 LTS | Virtual threads, pattern matching, record classes, long-term stability |
| **Framework** | Spring Boot | 3.4.x | Robust enterprise ecosystem, native configuration, Actuator metrics |
| **Security** | Spring Security | 6.x + JJWT (0.12.x) | Stateless JWT authentication, Method-level RBAC (`@PreAuthorize`) |
| **Persistence** | Spring Data JPA / Hibernate | 6.x | Type-safe ORM, transaction management, pessimistic/optimistic locking |
| **Database** | PostgreSQL | 18.4 | ACID compliance, JSONB support for dynamic specs, transactional reliability |
| **Schema Migrations** | Flyway | 10.x | Deterministic, reproducible, versioned SQL schema changes |
| **Validation** | Jakarta Bean Validation | 3.0 (Hibernate Validator) | Strong request-layer contract validation |
| **API Documentation** | Springdoc OpenAPI | 2.8.x (Swagger UI) | Interactive API exploration and client generation contract |
| **Build & Tooling** | Apache Maven | 3.9.x | Strict dependency management and automated test execution |
| **Testing** | JUnit 5 + Mockito + AssertJ | Latest | High test coverage for unit, service, and integration tests |

---

## 3. High-Level Modular Monolith Architecture

```text
                               +----------------------------------------+
                               |     Next.js 16 Storefront & Admin      |
                               |    (React 19 / TypeScript / GSAP)      |
                               +-------------------+--------------------+
                                                   |
                                                   | HTTP / REST (JSON)
                                                   | Base: http://localhost:8080/api/v1
                                                   v
+--------------------------------------------------------------------------------------------------+
|                                    SPRING BOOT MODULAR MONOLITH                                  |
|                                                                                                  |
|  +-------------------+  +--------------------+  +----------------------+  +-------------------+  |
|  | Security & Auth   |  | Exception Handling |  | Request Validation   |  | OpenAPI / Swagger |  |
|  | JWT Filter / RBAC |  | GlobalExceptionHandler| Jakarta Bean Valid. |  | /swagger-ui.html  |  |
|  +-------------------+  +--------------------+  +----------------------+  +-------------------+  |
|                                                                                                  |
|  ====================================== BUSINESS MODULES ======================================  |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|  |  Auth & User    |  | Catalog & Search|  |    Inventory    |  | Cart & Wishlist |              |
|  |  Registration,  |  | Products,       |  | Stock counts,   |  | Session/User    |              |
|  |  Login, Roles   |  | Categories, SKU |  | Movements, Lock |  | Calculations    |              |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|  | Coupon & Promo  |  | Checkout & Order|  | Payments (Mock) |  | Shipping & Track|              |
|  | Code validation,|  | Authoritative   |  | Gateway ledger, |  | Tracking events,|              |
|  | Limits & Rules  |  | Totals, States  |  | Transactions    |  | Dispatch state  |              |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|  | Returns/Refunds |  | Customer Review |  |  CMS & Settings |  | Audit Logging   |              |
|  | Inspection flow,|  | Verified Buyer, |  | Journal, Hero,  |  | Tamper-evident  |              |
|  | Linked ledger   |  | Moderation      |  | Store config    |  | Admin Trails    |              |
|  +-----------------+  +-----------------+  +-----------------+  +-----------------+              |
|                                                                                                  |
|  ================================== DATA ACCESS & PERSISTENCE =================================  |
|  +--------------------------------------------------------------------------------------------+  |
|  | Spring Data JPA Repositories (Entity Mapping, Custom JPQL, Pessimistic / Optimistic Locks)  |  |
|  +--------------------------------------------------------------------------------------------+  |
+----------------------------------------------+---------------------------------------------------+
                                               |
                                               | JDBC / HikariCP
                                               v
                             +-----------------------------------+
                             |     PostgreSQL 18.4 Database      |
                             |   (Flyway Versioned Migrations)   |
                             +-----------------------------------+
```

---

## 4. Package Structure

The backend will reside cleanly in `/backend` with standard Maven conventions:

```text
backend/
├── pom.xml
├── README.md
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/com/rora/backend/
│   │   │   ├── RoraBackendApplication.java
│   │   │   ├── config/
│   │   │   │   ├── ApplicationProperties.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── WebMvcConfig.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   └── UserPrincipal.java
│   │   │   ├── common/
│   │   │   │   ├── ApiResponse.java
│   │   │   │   ├── PagedResponse.java
│   │   │   │   ├── BaseEntity.java
│   │   │   │   └── exception/
│   │   │   │       ├── GlobalExceptionHandler.java
│   │   │   │       ├── ResourceNotFoundException.java
│   │   │   │       ├── BadRequestException.java
│   │   │   │       ├── UnauthorizedException.java
│   │   │   │       └── BusinessRuleException.java
│   │   │   ├── auth/         (Controller, Service, DTOs)
│   │   │   ├── user/         (Entity, Repository, Service, Controller, DTOs)
│   │   │   ├── customer/     (Customer Profile, Address Entity, Repository, DTOs)
│   │   │   ├── category/     (Category Entity, Service, Controller, DTOs)
│   │   │   ├── product/      (Product, Variant, Images, Specs, Repositories, DTOs)
│   │   │   ├── inventory/    (Inventory, StockMovement, Locks, DTOs)
│   │   │   ├── cart/         (Cart, CartItem, Calculations, DTOs)
│   │   │   ├── wishlist/     (Wishlist, WishlistItem, DTOs)
│   │   │   ├── coupon/       (Coupon, CouponUsage, Validation Engine, DTOs)
│   │   │   ├── checkout/     (CheckoutService, PriceCalculator, DTOs)
│   │   │   ├── order/        (Order, OrderItem, Timeline, Lifecycle, DTOs)
│   │   │   ├── payment/      (Payment, Transaction, Mock Gateway Service, DTOs)
│   │   │   ├── shipment/     (Shipment, TrackingEvent, DTOs)
│   │   │   ├── returnitem/   (ReturnRequest, ReturnItem, DTOs)
│   │   │   ├── refund/       (RefundRecord, DTOs)
│   │   │   ├── review/       (Review, Moderation, DTOs)
│   │   │   ├── cms/          (CMSContent, JournalEntry, FAQ, DTOs)
│   │   │   ├── settings/     (StoreSettings, DTOs)
│   │   │   └── audit/        (AuditLog Entity, Service, Interceptor, DTOs)
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/migration/
│   │           ├── V1__initial_schema.sql
│   │           └── V2__seed_initial_data.sql
│   └── test/
│       ├── java/com/rora/backend/
│       └── resources/
│           └── application-test.yml
```

---

## 5. Security and RBAC Architecture

1. **Stateless JWT Tokens**:
   - Authorization Header: `Bearer <token>`
   - Signed with HMAC-SHA256 (256-bit+ secure secret).
   - Payload contains `userId`, `email`, and `roles`.
2. **Roles & Permissions**:
   - `ROLE_CUSTOMER`: Can manage personal cart, wishlist, profile, place orders, submit reviews.
   - `ROLE_ADMIN`: Full access across all administration modules, audit trails, and configuration.
   - `ROLE_MANAGER`: Can access operational dashboards, orders, inventory, and shipments.
   - `ROLE_PRODUCT_MANAGER`: Can create, update, and manage catalog, categories, and inventory.
   - `ROLE_ORDER_MANAGER`: Can update order states, manage shipments, process returns and refunds.
3. **Password Security**:
   - BCrypt password hashing with work factor 12.

---

## 6. Authoritative Pricing & Transaction Guardrails

- **Zero Trust Rule**: Under no circumstances will pricing, GST, discount amounts, shipping charges, or final totals sent from the browser/frontend be trusted.
- **Transactional Atomic Checkout**:
  1. Validate user session / customer identity.
  2. Verify cart items exist, products are published, variants match.
  3. Acquire database lock on inventory (`SELECT ... FOR UPDATE`).
  4. Validate stock availability $\ge$ requested quantity.
  5. Validate coupon rules (min spend, date range, total usage limit, per-user usage limit).
  6. Authoritatively compute:
     $$\text{Subtotal} = \sum (\text{Unit Price} \times \text{Quantity})$$
     $$\text{Discount} = \text{CalculateDiscount}(\text{Subtotal}, \text{Coupon})$$
     $$\text{ShippingFee} = (\text{Subtotal} \ge \text{Threshold}) ? 0 : \text{StandardFee}$$
     $$\text{Total} = \max(0, \text{Subtotal} - \text{Discount} + \text{ShippingFee})$$
  7. Persist `Order`, `OrderItem`s, and `OrderTimelineEvent`.
  8. Deduct inventory and write `InventoryMovement` audit log.
  9. Record `PaymentRecord` in `INITIATED` / `SUCCESS` state.
  10. Clear active cart.
  All steps execute within a single `@Transactional` boundary.
