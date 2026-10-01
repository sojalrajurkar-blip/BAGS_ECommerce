# RÓRA — Antigravity Backend Implementation Master Prompt

## Mission

You are the senior engineer implementing the backend for the existing **RÓRA — Luxury Bags & Carry Essentials** e-commerce project.

Read this prompt together with `RORA_Backend_SRS.md`.

Your job is to build the backend **locally first**, integrate it with the existing frontend, fully test it, and only then prepare production deployment.

---

# 1. HARD RULE: ONE PHASE AT A TIME

You MUST work on only one phase at a time.

You MUST NOT automatically continue to the next phase.

After each phase:

1. Stop implementation.
2. Run verification.
3. Report what was completed.
4. Report files created/modified.
5. Report database/API changes.
6. Report tests and results.
7. Report known issues.
8. Report remaining work.
9. Ask the user for explicit permission to start the next phase.
10. STOP.

No exceptions.

## Valid approval examples

Continue only if the user clearly says:

```text
APPROVED
```

```text
START PHASE 2
```

```text
GO AHEAD WITH PHASE 2
```

If the user is ambiguous, ask for confirmation.

Do not treat vague phrases such as "okay", "nice", "looks good", or "fine" as permission unless their meaning clearly authorizes the next phase.

---

# 2. FIRST ACTION

Before coding:

1. Read this entire prompt.
2. Read `RORA_Backend_SRS.md`.
3. Inspect the existing repository.
4. Inspect `PROGRESS.md`.
5. Inspect:
   - `src/types/domain.ts`
   - `src/context/StoreContext.tsx`
   - `src/data/mockData.ts`
   - `src/data/repositories/*`
   - storefront views
   - admin views
   - package files
   - configuration
6. Understand how the existing frontend currently works.
7. Map frontend domain objects to backend entities and APIs.

Do not make assumptions when the repository can answer the question.

---

# 3. DO NOT REBUILD THE FRONTEND

The frontend is already substantially implemented.

Do not:

- Rebuild the UI
- Replace the design system
- Remove working pages
- Replace animations unnecessarily
- Rewrite components without a backend integration reason
- Delete mock repositories before their API replacement is ready
- Make unrelated cleanup changes

The intended migration is:

```text
Existing UI
    ↓
Existing Repository Abstraction
    ↓
API-backed Repository
    ↓
Spring Boot
    ↓
PostgreSQL
```

---

# 4. TECHNOLOGY

Use:

- Java LTS
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Bean Validation
- Flyway
- OpenAPI/Swagger
- JUnit
- Mockito
- Testcontainers where useful

Use a modular monolith.

Do not introduce microservices unless explicitly approved.

---

# 5. LOCAL-FIRST

Use:

```text
Frontend   → localhost:3000
Backend    → localhost:8080
PostgreSQL → localhost:5432
```

Order:

```text
Backend Foundation
       ↓
Database
       ↓
APIs
       ↓
API Testing
       ↓
Frontend Integration
       ↓
Full Local QA
       ↓
Production Preparation
       ↓
Production
```

Never deploy production before local QA is successful.

---

# 6. PHASES

## PHASE 0 — Audit and Architecture

Inspect the repository and produce:

- Architecture proposal
- Frontend/backend domain mapping
- API plan
- Database plan
- Risks
- Integration points

Create appropriate documentation under:

```text
backend/docs/
```

Do not build the full backend.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 1 — Backend Foundation

Build:

- Spring Boot
- Maven
- Configuration
- PostgreSQL connection
- Flyway
- Base package structure
- Environment variables
- Health endpoint
- Global error handling
- Validation
- OpenAPI
- Logging foundation
- README

Verify startup, database connection, migrations, health endpoint, OpenAPI and tests.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 2 — Authentication and RBAC

Build:

- Users
- Roles
- Permissions
- Registration
- Login
- Password hashing
- Authentication
- Authorization
- Admin protection

Test:

- Valid login
- Invalid login
- Protected endpoints
- Unauthorized requests
- Forbidden requests
- Admin access
- Non-admin rejection

**STOP AND REQUEST APPROVAL.**

---

## PHASE 3 — Categories, Products and Variants

Build:

- Categories
- Products
- Variants
- SKU
- Images
- Attributes
- CRUD APIs
- Search/filter foundation
- Pagination

Backend owns price, availability, SKU and publish state.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 4 — Inventory

Build:

- Inventory
- Stock movements
- Adjustments
- Reservations
- Release
- Low-stock logic
- Concurrency protection

Test simultaneous stock consumption and out-of-stock behavior.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 5 — Customer, Cart and Wishlist

Build:

- Customer
- Addresses
- Cart
- Cart items
- Wishlist
- Wishlist items

Test all CRUD and invalid scenarios.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 6 — Coupons and Pricing

Build:

- Coupons
- Percentage/fixed discounts
- Minimum order
- Maximum discount
- Dates
- Usage limits
- Per-user limits
- Product/category restrictions where required

Backend must calculate authoritative totals.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 7 — Checkout and Orders

Build:

- Checkout
- Order
- Order items
- Order totals
- Order timeline
- State transitions
- Cancellation rules

Backend must calculate authoritative:

- Subtotal
- Discount
- Tax
- Shipping
- Final total

Checkout must be transactionally safe.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 8 — Local Mock Payments

Build:

- Payment
- Payment transaction
- Payment states
- Mock payment provider
- Success/failure/pending scenarios

Do not connect real payment gateways yet.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 9 — Shipping

Build:

- Shipments
- Tracking reference
- Shipment events
- Tracking API

Do not add real shipping-provider integration unless explicitly approved.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 10 — Returns and Refunds

Build:

- Return requests
- Return states
- Return items
- Refund records
- Refund states
- Payment linkage

Test valid and invalid transitions.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 11 — Reviews

Build:

- Reviews
- Ratings
- Verified purchase
- Moderation
- Admin moderation

**STOP AND REQUEST APPROVAL.**

---

## PHASE 12 — CMS and Settings

Build:

- Homepage content
- Journal
- FAQ
- Static content
- Store settings
- SEO metadata where required

Do not turn the frontend into an unnecessary visual page builder.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 13 — Admin Platform

Complete backend support for:

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

Protect every sensitive operation with backend authorization.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 14 — Frontend Integration

Replace mock repository implementations with API-backed repositories.

Integrate:

- Products
- Categories
- Search
- Cart
- Wishlist
- Checkout
- Orders
- Tracking
- Reviews
- Account
- Admin
- CMS

Preserve the existing UI/UX.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 15 — Full Local QA

Test:

### Customer

- Registration
- Login
- Browse
- Search
- Filter
- Product detail
- Wishlist
- Cart
- Coupon
- Checkout
- Payment simulation
- Order confirmation
- Order history
- Tracking
- Returns/refunds
- Reviews

### Admin

- Login
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
- Audit logs

### Failure cases

- Invalid data
- Unauthorized access
- Forbidden access
- Out-of-stock
- Invalid coupon
- Payment failure
- Duplicate requests
- Invalid state transitions
- Database failure
- API/network failure

Run unit, integration, API, security and browser/manual verification.

**STOP AND REQUEST APPROVAL.**

---

## PHASE 16 — Production Preparation

Only after successful local QA.

Prepare:

- Docker
- CI/CD
- Production configuration
- Cloud PostgreSQL plan
- Secrets management
- Monitoring
- Logging
- Backups
- Recovery strategy
- Deployment documentation

Do not deploy yet.

**STOP AND REQUEST DEPLOYMENT APPROVAL.**

---

## PHASE 17 — Production Deployment

Only after explicit deployment approval.

Deploy and verify:

- Health
- Authentication
- Catalog
- Search
- Cart
- Checkout
- Orders
- Payments
- Shipping
- Returns
- Admin
- Security
- Logs
- Monitoring

If something fails, stop and report it. Never hide or bypass a failure.

---

# 7. ENGINEERING RULES

## Never trust the frontend

The backend is authoritative for:

- Price
- Discount
- Tax
- Inventory
- Payment status
- User role
- Permissions
- Order status

## Use DTOs

Do not expose JPA entities directly by default.

## Use migrations

Database schema must be reproducible.

## Use transactions

Especially for:

- Checkout
- Order creation
- Inventory reservation
- Cancellation
- Refund-related operations

## Preserve integrity

Use:

- Foreign keys
- Unique constraints
- Validation
- Transactions
- Proper status transitions
- Concurrency controls

## Never hardcode secrets

Use environment variables and secure secret management.

---

# 8. AI RULE

Do not add AI before the deterministic business layer is reliable.

Future AI can include:

- AI Search
- Shopping Assistant
- Recommendations
- Product content generation
- SEO content
- Review analysis
- Sentiment analysis
- Admin insights
- Customer support

AI must query authoritative application APIs.

AI must never invent:

- Products
- Prices
- Inventory
- Orders
- Payment status
- Policies

AI-generated business content should use human approval where appropriate.

---

# 9. DEFINITION OF DONE

A phase is complete only when:

```text
Implementation complete
+
Compilation successful
+
Relevant tests passing
+
Error cases verified
+
Security checked
+
Documentation updated
+
Local verification complete
+
No unexplained regressions
```

If something is incomplete, do not report the phase as complete.

---

# 10. REQUIRED PHASE REPORT

At the end of every phase, use this structure:

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
Phase X — <name>

PERMISSION REQUIRED:
Please explicitly approve before I start the next phase.
```

Then stop.

---

# 11. FINAL COMMAND

Start with:

**PHASE 0 ONLY.**

Inspect the existing RÓRA repository and create the architecture, domain, API and database mapping.

Do not implement Phase 1 or any later phase.

When Phase 0 is complete, stop and ask the user for explicit approval.
