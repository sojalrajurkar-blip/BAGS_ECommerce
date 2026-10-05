# RÓRA — Antigravity Forensic Audit Report

**Date:** October 5, 2026  
**Auditor:** Antigravity AI Pair Programmer  
**Project:** RÓRA Luxury Leather Goods & Carry Essentials

---

## 1. Actual Project Architecture Discovered from Code

### Frontend Architecture
- **Framework:** Next.js 16.3.8 (App Router) + React 19 + TypeScript 5.8.2
- **Styling & Tokens:** Pure Vanilla CSS Design Token Architecture (`src/index.css`, `src/styles/tokens.css`, `src/styles/components.css`) using curated luxury palette (Warm Bone `#F7F4EE`, Nero Black `#141311`, Tuscan Olive `#4A533C`, Terracotta `#B86B52`, Muted Brass `#C9B99F`).
- **Animation & Motion:** GSAP 3.15.0 + ScrollTrigger + Lenis 1.3.26 smooth scroll provider (`src/animations/`).
- **Hero Canvas:** 300-frame sequential canvas deconstruction engine (`src/components/product/RoraProductSequence.tsx`) rendering High-DPI frame sequence with DPR support, nearest-neighbor fallback, chunked progressive loading, and reduced-motion fallback.
- **Data & State Management:** Centralized `StoreContext.tsx` with resilient hybrid data layer (`src/data/repositories/`) backed by `apiClient.ts` communicating with Spring Boot 3.4 REST backend.

### Backend Architecture
- **Framework:** Spring Boot 3.4.3 + Java 21 LTS + Spring Security 6
- **Database Layer:** PostgreSQL 18.x with Flyway migrations (`V1` to `V14`) + Spring Data JPA / Hibernate
- **Authentication:** Stateless JWT (JJWT 0.12.6, 256-bit Hex Key) with Role-Based Access Control (`ROLE_ADMIN`, `ROLE_CUSTOMER`, `ROLE_MANAGER`, `ROLE_PRODUCT_MANAGER`, `ROLE_ORDER_MANAGER`).
- **API Documentation:** OpenAPI 3 / Swagger UI (`/swagger-ui.html`, `/v3/api-docs`)
- **Monitoring & Metrics:** Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`)

---

## 2. Actual Frontend Start Command

- **Development:** `npm.cmd run dev` (or `npx.cmd next dev -p 3000`)
- **Production Build:** `npm.cmd run build` (or `npx.cmd next build`)
- **Production Start:** `npm.cmd run start` (or `npx.cmd next start -p 3000`)
- **Typecheck:** `npx.cmd tsc --noEmit`
- **Lint:** `npx.cmd eslint src/`

---

## 3. Actual Backend Start Command

- **Maven Test Suite:** `mvn test`
- **Maven Local Run:** `mvn spring-boot:run`
- **Maven Production Package:** `mvn clean package -DskipTests`

---

## 4. Actual Database Requirements

- **Engine:** PostgreSQL 18+ (Compatible with PostgreSQL 15+)
- **Host:** `localhost` / `127.0.0.1`
- **Port:** `5432`
- **Database Name:** `rora_db`
- **Username:** `postgres`
- **Password:** `password`
- **Migration Engine:** Flyway 14 migration scripts (`V1__initial_schema.sql` through `V14__fix_stale_schemas_and_columns.sql`)

---

## 5. Required Environment Variables

### Frontend (`.env`)
```properties
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
NODE_ENV=development
PORT=3000
NEXT_TELEMETRY_DISABLED=1
```

### Backend (`backend/.env` & `application.yml`)
```properties
DB_URL=jdbc:postgresql://localhost:5432/rora_db
DB_USERNAME=postgres
DB_PASSWORD=password
PORT=8080
SPRING_PROFILES_ACTIVE=dev
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:3000,http://localhost:80
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_MS=86400000
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
```

---

## 6. Actual Ports

- **Frontend:** `3000`
- **Backend REST API:** `8080`
- **Database (PostgreSQL):** `5432`

---

## 7. Actual API Base URL

`http://localhost:8080/api/v1`

---

## 8. Actual Frontend-to-Backend Request Flow

```
[React / Next.js Component]
         │
         ▼
[Domain Repository (e.g. productRepository, cartRepository)]
         │
         ▼
[Centralized apiClient (apiClient.ts)]
   - Injects X-Session-ID (guest tracking)
   - Injects Authorization: Bearer <JWT>
   - Unwraps standard ApiResponseWrapper<T> { success, data, message }
         │
         ▼ (HTTP JSON via fetch)
[Spring Boot 3.4 RestController (@RequestMapping /api/v1/*)]
         │
         ▼
[Spring Security Filter Chain & RBAC Annotations]
         │
         ▼
[Service Implementation (e.g. ProductServiceImpl, CartServiceImpl)]
         │
         ▼
[Spring Data JPA Repository]
         │
         ▼
[PostgreSQL Database (rora_db)]
```

---

## 9. Route Inventory

| Route | File Path | Purpose |
|---|---|---|
| `/` | `src/app/page.tsx` | Flagship Editorial Homepage & 300-Frame Hero |
| `/shop` | `src/app/shop/page.tsx` | Filterable Product Catalog with sorting & faceted search |
| `/category/[slug]` | `src/app/category/[slug]/page.tsx` | Category detail with hero banners & curated bags |
| `/product/[slug]` | `src/app/product/[slug]/page.tsx` | Product Detail Page (PDP) with 360 preview, color swatches, reviews & add to bag |
| `/search` | `src/app/search/page.tsx` | Live search results with filters |
| `/cart` | `src/app/cart/page.tsx` | Dedicated full cart page with shipping estimation & promo coupons |
| `/checkout` | `src/app/checkout/page.tsx` | Multi-step luxury checkout flow with address & payment options |
| `/confirmation` | `src/app/confirmation/page.tsx` | Order confirmation & receipt with live delivery timeline |
| `/wishlist` | `src/app/wishlist/page.tsx` | Customer saved wishlist items |
| `/account` | `src/app/account/page.tsx` | Customer account dashboard, profile, and addresses |
| `/orders` | `src/app/orders/page.tsx` | Customer order history & real-time shipment milestone tracking |
| `/returns` | `src/app/returns/page.tsx` | Customer return & refund request submission portal |
| `/journal` | `src/app/journal/page.tsx` | Editorial stories & artisan craftsmanship journal |
| `/journal/[slug]` | `src/app/journal/[slug]/page.tsx` | Journal article detail |
| `/faq` | `src/app/faq/page.tsx` | Help center & categorized store FAQs |
| `/about` | `src/app/about/page.tsx` | RÓRA Atelier history, materials, sustainability & values |
| `/contact` | `src/app/contact/page.tsx` | Concierge inquiries & client assistance |
| `/shipping` | `src/app/shipping/page.tsx` | Logistics, complimentary delivery policy, & international care |
| `/admin` | `src/app/admin/page.tsx` | Backoffice management portal (14 enterprise modules) |

---

## 10. Static Asset Inventory

- **Location:** `public/images/rora/`
- **Product Photography:** Unsplash high-res luxury photography linked in `src/data/imageAssets.ts` and `src/data/mockData.ts`.
- **Remote Host Authorization:** `images.unsplash.com` configured in `next.config.mjs`.

---

## 11. GSAP / Canvas Asset Inventory

- **Path:** `public/images/rora/product-sequence/`
- **Total Frames Present:** 300 JPG frames (`frame-001.jpg` to `frame-300.jpg`)
- **First Frame Size:** ~62 KB
- **Average Frame Size:** ~18-24 KB
- **Total Sequence Size:** ~6.1 MB
- **Component:** `src/components/product/RoraProductSequence.tsx`

---

## 12. Known Runtime & API Contract Risks Discovered

1. **Coupon Repository Contract Discrepancy:**
   - Frontend called `GET /coupons/active` and `POST /coupons/validate` with JSON body.
   - Backend `CouponController.java` exposes `GET /api/v1/coupons` and `GET /api/v1/coupons/validate?code=...&subtotal=...`.
2. **Review Repository Unwrapping Discrepancy:**
   - `getReviewsForProduct` expected a flat array, but backend returns `PagedResponse<ReviewDto>` (`data.content`), causing fallback to seed reviews.
3. **Return Submission Payload Discrepancy:**
   - `returnRepository.ts` sent unstructured object without `orderIdOrNumber` or `items` array required by Spring backend validation.
4. **Admin Dashboard Overview Endpoint Discrepancy:**
   - `adminRepository.ts` called `/admin/dashboard/summary` and read `data.monthlyRevenue` which is actually hosted on `/admin/dashboard/overview`.
5. **Admin Inventory Stock Adjustment Movement Type:**
   - `adminRepository.ts` omitted `movementType` required by `StockAdjustmentRequest.java`.
6. **Admin Paginated Endpoints (`Page<T>`):**
   - Several admin repository methods checked `Array.isArray(data)` directly instead of inspecting `data.content`.

---

## 13. Existing Test Coverage

- **Backend:** 204 unit & integration tests passing across all 14 modules (`com.rora.backend.*`).
- **Frontend:** TypeScript strict compilation (0 errors) and ESLint (0 errors).
- **What existing tests do not verify:** Real browser hydration lifecycle, canvas frame sequencing under slow networks, client-side token renewal, cross-origin request negotiation in dev vs prod, and dynamic DOM reactivity.

---

## 14. Exact Files Requiring Surgical Repair

1. `src/data/repositories/couponRepository.ts`
2. `src/data/repositories/reviewRepository.ts`
3. `src/data/repositories/returnRepository.ts`
4. `src/data/repositories/adminRepository.ts`
5. `src/data/apiClient.ts`
6. `src/data/repositories/orderRepository.ts`
