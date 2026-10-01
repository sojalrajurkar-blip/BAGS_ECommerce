# RÓRA — Risks, Mitigations & Integration Strategy (Phase 0)

## 1. Technical Risks & Mitigations

| Risk / Challenge | Severity | Mitigation Strategy |
|---|---|---|
| **Checkout Race Condition / Double Stock Allocation** | High | Use Pessimistic Locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) on `inventory` row during checkout transaction. Stock is checked and decremented atomically. |
| **Financial Rounding Inaccuracies** | High | Use `java.math.BigDecimal` with `RoundingMode.HALF_UP` on all monetary and tax calculations. Never rely on frontend calculations. |
| **Coupon Code Exploit / Overuse** | Medium | Check active date range, global usage limit, and user-specific usage count inside the `@Transactional` checkout boundary before order confirmation. |
| **CORS / Pre-flight Issues in Next.js** | Low | Implement Spring WebMvc `CorsConfigurer` allowing `http://localhost:3000` with headers `Authorization`, `Content-Type`, and standard HTTP methods. |
| **Frontend Breaking During API Switch** | High | Frontend repository layer (`src/data/repositories/*`) acts as an abstraction boundary. We swap in API clients with identical TypeScript interface return types, ensuring zero UI breakage. |
| **Guest vs Authenticated User Cart Sync** | Medium | Support session token header (`X-Session-Id`) for guest carts. When a user logs in, migrate guest cart items into user's persistent cart. |

---

## 2. Frontend Integration Points

The Next.js 16 frontend is cleanly decoupled via TypeScript repositories:

```text
src/data/repositories/
├── productRepository.ts   ---> /api/v1/products (Search, Filter, Detail, Related)
├── categoryRepository.ts  ---> /api/v1/categories
├── orderRepository.ts     ---> /api/v1/orders, /api/v1/checkout
├── reviewRepository.ts    ---> /api/v1/reviews
├── couponRepository.ts    ---> /api/v1/coupons, /api/v1/cart/apply-coupon
├── contentRepository.ts   ---> /api/v1/cms, /api/v1/cms/journal, /api/v1/cms/faq
└── adminRepository.ts     ---> /api/v1/admin/*
```

When reaching Phase 14, each mock repository will be replaced by an HTTP client pointing to `NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1`.
