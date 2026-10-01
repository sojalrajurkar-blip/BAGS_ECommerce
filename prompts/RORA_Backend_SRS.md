# RÓRA — Backend Software Requirements Specification (SRS)

**Project:** RÓRA — Luxury Bags & Carry Essentials  
**Version:** 1.0  
**Strategy:** Local-first, production-ready  
**Backend:** Java + Spring Boot + PostgreSQL  
**Frontend:** Existing Next.js 16 + React 19 + TypeScript

## 1. Purpose

Build a real backend for the existing RÓRA e-commerce application without unnecessarily rebuilding the existing frontend.

The backend must replace the current mock/in-memory repository behavior with persistent PostgreSQL data and secure REST APIs.

Development order:

```text
Local Backend → Local Database → API Verification → Frontend Integration
→ Full Local QA → Production Preparation → Production
```

## 2. Existing Frontend Baseline

The existing application already contains:

- Next.js 16 App Router
- React 19
- Strict TypeScript
- GSAP / Lenis motion system
- Storefront pages
- Admin panel
- Typed domain models
- Repository abstraction
- Mock data repositories

Important domain concepts include:

- Product
- ProductVariant
- Category
- CartItem
- WishlistItem
- Customer
- Address
- Order
- OrderItem
- Review
- Coupon
- PaymentRecord
- ShipmentRecord
- ReturnRecord
- RefundRecord
- AdminUser
- AdminRole
- StoreSettings
- CMSContent
- AuditLog

The backend must map to these existing concepts rather than inventing an unrelated domain.

## 3. Target Architecture

```text
Next.js Frontend
       |
       | REST / JSON
       v
Spring Boot Backend
       |
       +-- Security
       +-- Controllers / DTOs
       +-- Services / Business Rules
       +-- Repositories
       +-- Validation
       +-- Transactions
       +-- Audit
       |
       v
PostgreSQL
```

Use a modular monolith. Do not introduce microservices unless explicitly approved.

## 4. Technology Requirements

Recommended stack:

- Java LTS
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Bean Validation
- Flyway
- OpenAPI / Swagger
- JUnit
- Mockito
- Testcontainers where useful

## 5. Backend Structure

Create a separate `/backend` project.

Suggested packages:

```text
backend/
├── pom.xml
├── README.md
├── .env.example
├── src/main/java/com/rora/
│   ├── config/
│   ├── security/
│   ├── common/
│   ├── auth/
│   ├── user/
│   ├── customer/
│   ├── category/
│   ├── product/
│   ├── inventory/
│   ├── cart/
│   ├── wishlist/
│   ├── coupon/
│   ├── checkout/
│   ├── order/
│   ├── payment/
│   ├── shipment/
│   ├── return/
│   ├── refund/
│   ├── review/
│   ├── cms/
│   ├── settings/
│   └── audit/
└── src/main/resources/
    ├── application.yml
    └── db/migration/
```

Keep Controller, DTO, Service, Repository, Entity, Mapper and validation concerns organized.

## 6. Database Requirements

PostgreSQL is the authoritative database.

Core entities/tables should cover:

### Identity
- users
- roles
- permissions
- user_roles
- role_permissions

### Customer
- customers
- addresses

### Catalog
- categories
- products
- product_variants
- product_images
- product_attributes

### Inventory
- inventory
- inventory_movements

### Shopping
- carts
- cart_items
- wishlists
- wishlist_items

### Commerce
- coupons
- coupon_usage
- orders
- order_items

### Payments/Fulfillment
- payments
- payment_transactions
- shipments
- shipment_events

### Returns
- returns
- return_items
- refunds

### Reviews
- reviews
- review moderation data

### Content/Operations
- cms_content
- store_settings
- seo metadata where needed
- audit_logs

Use migrations, foreign keys, indexes, unique constraints, timestamps and transactions.

## 7. Authentication and RBAC

Support:

- Registration
- Login
- Logout/session strategy
- Password hashing
- Protected customer endpoints
- Protected admin endpoints
- Password reset architecture
- Email verification architecture where required

Possible roles:

```text
CUSTOMER
ADMIN
MANAGER
PRODUCT_MANAGER
ORDER_MANAGER
```

Possible permissions:

```text
PRODUCT_CREATE
PRODUCT_UPDATE
PRODUCT_DELETE
PRODUCT_VIEW
ORDER_VIEW
ORDER_UPDATE
CUSTOMER_VIEW
INVENTORY_VIEW
INVENTORY_UPDATE
CMS_MANAGE
USER_MANAGE
ROLE_MANAGE
REPORT_VIEW
SETTINGS_MANAGE
AUDIT_VIEW
```

Backend authorization is mandatory.

## 8. Product and Category Requirements

Product APIs must support:

- Create/read/update/archive
- Listing
- Pagination
- Search
- Filtering
- Sorting
- Slugs
- Categories
- Variants
- SKU
- Images
- Attributes
- Price
- Availability
- Publish state

Backend is authoritative for price, SKU, inventory and availability.

Categories must support listing, lookup, slug and management.

## 9. Search

Initial search may use PostgreSQL.

Support:

- Keyword search
- Category filtering
- Price filtering
- Availability filtering
- Sorting
- Pagination

A dedicated search engine may be added later if justified.

## 10. Inventory

Support:

- SKU
- Available quantity
- Reserved quantity
- Sold quantity where required
- Low-stock threshold
- Stock adjustments
- Stock movement history
- Reservation/release
- Return-related stock changes

Inventory must be concurrency-safe.

## 11. Cart and Wishlist

Cart must support:

- Get/create
- Add item
- Remove item
- Update quantity
- Clear
- Availability validation
- Coupon
- Subtotal
- Discount
- Tax/shipping/final total as applicable

Wishlist must support add, remove, view and move-to-cart behavior where applicable.

Backend must recalculate financial values. Never trust frontend price, discount, tax, shipping or total.

## 12. Coupons and Pricing

Support:

- Percentage discounts
- Fixed discounts
- Minimum order value
- Maximum discount
- Start/end dates
- Usage limits
- Per-user limits
- Product/category restrictions where required
- Activation/deactivation

All coupon validation must happen on the backend.

## 13. Checkout and Orders

Checkout must validate:

1. Customer
2. Cart
3. Products/variants
4. Prices
5. Inventory
6. Coupon
7. Address
8. Shipping
9. Payment method
10. Final amount

Order lifecycle may include:

```text
PLACED
CONFIRMED
PROCESSING
SHIPPED
OUT_FOR_DELIVERY
DELIVERED
```

Alternative states:

```text
CANCELLED
PAYMENT_FAILED
RETURN_REQUESTED
RETURNED
REFUND_INITIATED
REFUNDED
```

The backend controls legal state transitions.

## 14. Payments

Payment is separate from Order.

Possible states:

```text
INITIATED
PENDING
SUCCESS
FAILED
CANCELLED
REFUNDED
PARTIALLY_REFUNDED
```

First local implementation should use a mock payment provider.

Real gateway integration comes later.

## 15. Shipping

Support:

- Shipment creation
- Tracking reference
- Shipment status
- Shipment events
- Order tracking

Real provider integration is later.

## 16. Returns and Refunds

Support:

- Return request
- Reason
- Approval/rejection
- Return shipment
- Product received
- Refund initiation
- Refund completion

Possible states:

```text
RETURN_REQUESTED
RETURN_APPROVED
RETURN_REJECTED
RETURN_PICKUP
RETURN_RECEIVED
REFUND_INITIATED
REFUNDED
```

Refunds must be linked to payment transactions.

## 17. Reviews

Support:

- Rating
- Written review
- Verified purchase
- Moderation
- Admin review actions

## 18. CMS and Settings

Support APIs for:

- Homepage content
- Journal
- FAQ
- Static content
- Store settings
- SEO metadata where required

CMS controls content; the frontend remains responsible for its professional layout and interaction.

## 19. Admin APIs

Support the existing admin areas:

```text
Dashboard
Products
Categories
Inventory
Orders
Customers
Payments
Shipments
Returns
Refunds
Coupons
Reviews
CMS
Users
Roles
Settings
Audit Logs
```

Sensitive operations require permissions.

## 20. Audit Logging

Important admin/business actions should record:

- Actor
- Action
- Entity type
- Entity ID
- Timestamp
- Relevant metadata
- Result/status

Never log secrets.

## 21. API Design

Use REST/JSON and preferably:

```text
/api/v1/...
```

Example:

```text
GET    /api/v1/products
GET    /api/v1/products/{id}
POST   /api/v1/products
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}

GET    /api/v1/categories
GET    /api/v1/orders
POST   /api/v1/orders
GET    /api/v1/orders/{id}

GET    /api/v1/cart
POST   /api/v1/cart/items
PATCH  /api/v1/cart/items/{id}
DELETE /api/v1/cart/items/{id}
```

Document the final API in OpenAPI.

## 22. DTOs and Validation

Do not expose JPA entities directly by default.

Use:

```text
Request DTO → Controller → Service → Entity → Repository
Database → Entity → Service → Response DTO → Controller
```

Validate required fields, email, password, quantity, price rules, address, coupon and product data.

Use a consistent API error response.

## 23. Transactions

Multi-record operations must be transactional where needed.

Example:

```text
Place Order
  ├─ Validate Cart
  ├─ Validate Inventory
  ├─ Calculate authoritative totals
  ├─ Create Order
  ├─ Create Order Items
  ├─ Reserve/Reduce Inventory
  └─ Create Payment Record
```

The database must remain consistent when a step fails.

## 24. Frontend Integration

Do not rebuild the existing frontend.

Eventually replace:

```text
Mock Repository
       ↓
API Repository
       ↓
Spring Boot API
       ↓
PostgreSQL
```

Preserve existing UI/UX and domain behavior.

## 25. Local Environment

Recommended:

```text
Frontend   http://localhost:3000
Backend    http://localhost:8080
PostgreSQL localhost:5432
```

Provide:

- `.env.example`
- README
- Database setup
- Startup instructions
- API documentation
- Test instructions
- Frontend integration instructions

## 26. Testing

Required:

### Unit
- Services
- Business rules
- Coupons
- Order transitions
- Inventory

### Integration
- Database
- Repositories
- Authentication
- APIs

### Security
- Unauthorized access
- Forbidden access
- Role restrictions
- Invalid authentication
- Validation

### Failure scenarios
- Out-of-stock
- Invalid coupon
- Duplicate request
- Payment failure
- Invalid order transition
- Return/refund edge cases

## 27. Security

Never commit:

- Passwords
- API keys
- JWT secrets
- Database credentials
- Payment secrets
- Production tokens

Use password hashing, authorization, validation, CORS configuration, secure configuration, rate limiting strategy where appropriate, secure headers where appropriate, audit logging and safe error handling.

## 28. AI Readiness

AI is a later layer.

First establish deterministic APIs for products, search, orders, shipping, returns, FAQs and policies.

Future capabilities may include:

- AI search
- Shopping assistant
- Recommendations
- Product content generation
- SEO content
- Review analysis
- Sentiment analysis
- Admin insights
- Customer support

AI must never invent authoritative business facts.

## 29. Production Readiness

Production comes only after local QA passes.

Before production:

- Tests pass
- Frontend integration works
- Migrations work
- Security checks pass
- API docs are complete
- Logging is verified
- Configuration is separated
- Secrets are secure
- Deployment is repeatable
- Backup/recovery is documented

## 30. Mandatory Phase Gate

A phase is complete only when:

- Implementation is complete
- Code compiles
- Tests pass
- Relevant APIs work
- Error cases are tested
- Security checks pass
- Documentation is updated
- Existing frontend is not unnecessarily broken
- Verification evidence is available
- Known issues are documented

**Completion of one phase NEVER authorizes the next phase.**

At the end of every phase, stop and ask the user for explicit permission.

## 31. Implementation Phases

### Phase 0 — Repository and Architecture Audit
Inspect the existing frontend, domain models, repositories, mock data, StoreContext, storefront, admin modules and configuration.

Produce:

- Architecture proposal
- Frontend → backend domain mapping
- API plan
- Database plan
- Risks

**STOP FOR APPROVAL.**

### Phase 1 — Backend Foundation
Build Spring Boot, Maven, configuration, PostgreSQL, migrations, base structure, error handling, validation, OpenAPI and health endpoint.

**STOP FOR APPROVAL.**

### Phase 2 — Authentication and RBAC
Build users, roles, permissions, registration, login, password hashing, authentication and authorization.

**STOP FOR APPROVAL.**

### Phase 3 — Categories, Products and Variants
Build categories, products, variants, SKUs, images, product APIs and search/filter foundation.

**STOP FOR APPROVAL.**

### Phase 4 — Inventory
Build inventory, movements, reservations, low stock and concurrency protection.

**STOP FOR APPROVAL.**

### Phase 5 — Customer, Cart and Wishlist
Build customer profiles, addresses, cart and wishlist.

**STOP FOR APPROVAL.**

### Phase 6 — Coupons and Pricing
Build coupon validation, usage limits and backend-authoritative pricing.

**STOP FOR APPROVAL.**

### Phase 7 — Checkout and Orders
Build checkout, order creation, order items, totals, lifecycle and cancellation.

**STOP FOR APPROVAL.**

### Phase 8 — Local Mock Payments
Build payment records, states and mock provider with success/failure scenarios.

**STOP FOR APPROVAL.**

### Phase 9 — Shipping
Build shipments, tracking and shipment events.

**STOP FOR APPROVAL.**

### Phase 10 — Returns and Refunds
Build return workflow and refund records.

**STOP FOR APPROVAL.**

### Phase 11 — Reviews
Build reviews, ratings, verified purchase and moderation.

**STOP FOR APPROVAL.**

### Phase 12 — CMS and Settings
Build CMS, FAQ, journal/static content and settings.

**STOP FOR APPROVAL.**

### Phase 13 — Admin Platform
Complete dashboard and admin APIs for products, inventory, orders, customers, payments, shipping, returns, refunds, coupons, reviews, CMS, users, roles, settings and audit.

**STOP FOR APPROVAL.**

### Phase 14 — Frontend Repository Migration
Replace mock repository implementations with API-backed implementations and verify all storefront/admin flows.

**STOP FOR APPROVAL.**

### Phase 15 — Full Local QA
Run unit, integration, API, security, browser/manual, database-consistency and failure-scenario testing.

**STOP FOR APPROVAL.**

### Phase 16 — Production Preparation
Only after successful local QA: Docker, CI/CD, cloud database planning, secrets, monitoring, backups and deployment documentation.

**STOP FOR APPROVAL.**

### Phase 17 — Production Deployment
Only after explicit deployment approval. Deploy and verify all critical flows.

## 32. Required Phase Report

At the end of every phase, report:

```text
PHASE X COMPLETE

Completed:
- ...

Files created:
- ...

Files modified:
- ...

Database changes:
- ...

API changes:
- ...

Tests executed:
- ...

Test results:
- ...

Verification:
- ...

Known issues:
- ...

Remaining work:
- ...

NEXT PHASE:
Phase X+1 — <name>

PERMISSION REQUIRED:
Please explicitly approve before I start the next phase.
```

Then stop.

## 33. Final Instruction

Start with **PHASE 0 ONLY**.

Do not implement Phase 1 or any later phase until the user explicitly approves Phase 0.
