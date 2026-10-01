# RÓRA — API Specification & Endpoint Plan (Phase 0)

All endpoints are versioned with the `/api/v1` prefix.

---

## 1. Authentication & User Profile (`/api/v1/auth`, `/api/v1/account`)

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Register new customer account |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user, return JWT token & user summary |
| `POST` | `/api/v1/auth/refresh` | Public | Refresh JWT session token |
| `GET` | `/api/v1/auth/me` | Authenticated | Retrieve current user principal & profile |
| `GET` | `/api/v1/account/profile` | Customer | Get customer details and order history |
| `PUT` | `/api/v1/account/profile` | Customer | Update customer name, phone, preferences |
| `GET` | `/api/v1/account/addresses` | Customer | List saved addresses |
| `POST` | `/api/v1/account/addresses` | Customer | Add new saved shipping address |
| `DELETE` | `/api/v1/account/addresses/{id}` | Customer | Remove saved address |

---

## 2. Catalog & Products (`/api/v1/products`, `/api/v1/categories`)

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `GET` | `/api/v1/categories` | Public | List all active product categories |
| `GET` | `/api/v1/categories/{slug}` | Public | Get single category by slug |
| `GET` | `/api/v1/products` | Public | Filtered & paginated product listing (`category`, `minPrice`, `maxPrice`, `material`, `color`, `badge`, `sortBy`, `page`, `limit`) |
| `GET` | `/api/v1/products/{idOrSlug}` | Public | Get complete product details with variants, images, and specifications |
| `GET` | `/api/v1/products/featured` | Public | Get curated featured products |
| `GET` | `/api/v1/products/best-sellers` | Public | Get best seller / popular products |
| `GET` | `/api/v1/products/{id}/related` | Public | Get contextual related products |
| `GET` | `/api/v1/products/search` | Public | Full-text product search by keyword query `?q=` |

---

## 3. Cart & Wishlist (`/api/v1/cart`, `/api/v1/wishlist`)

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `GET` | `/api/v1/cart` | Public / Session / Customer | Get active cart with authoritative calculations |
| `POST` | `/api/v1/cart/items` | Public / Session / Customer | Add item/variant to cart |
| `PATCH` | `/api/v1/cart/items/{itemId}` | Public / Session / Customer | Update quantity of a cart item |
| `DELETE` | `/api/v1/cart/items/{itemId}` | Public / Session / Customer | Remove item from cart |
| `POST` | `/api/v1/cart/apply-coupon` | Public / Session / Customer | Validate and apply promotional coupon code |
| `DELETE` | `/api/v1/cart/remove-coupon` | Public / Session / Customer | Remove applied coupon |
| `DELETE` | `/api/v1/cart` | Public / Session / Customer | Clear all items from cart |
| `GET` | `/api/v1/wishlist` | Customer | Get user's saved wishlist products |
| `POST` | `/api/v1/wishlist/{productId}` | Customer | Add/Toggle product in wishlist |
| `DELETE` | `/api/v1/wishlist/{productId}` | Customer | Remove product from wishlist |

---

## 4. Checkout, Orders & Payments (`/api/v1/checkout`, `/api/v1/orders`, `/api/v1/payments`)

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `POST` | `/api/v1/checkout/preview` | Public / Customer | Calculate authoritative subtotal, discount, shipping, tax before placement |
| `POST` | `/api/v1/orders` | Public / Customer | Place order with shipping address & payment method |
| `GET` | `/api/v1/orders` | Customer | List logged-in customer's orders |
| `GET` | `/api/v1/orders/{orderId}` | Public (with token/secret) / Customer | Get full order details & status timeline |
| `POST` | `/api/v1/orders/{orderId}/cancel` | Customer | Request cancellation of pending order |
| `GET` | `/api/v1/orders/{orderId}/tracking` | Public / Customer | Get shipment timeline & carrier tracking events |
| `POST` | `/api/v1/payments/process-mock` | Public / Customer | Simulate local payment success/failure |

---

## 5. Reviews, CMS & Store Settings (`/api/v1/reviews`, `/api/v1/cms`, `/api/v1/settings`)

| Method | Endpoint | Access Level | Description |
|---|---|---|---|
| `GET` | `/api/v1/products/{id}/reviews` | Public | List approved customer reviews for a product |
| `POST` | `/api/v1/products/{id}/reviews` | Customer | Submit new product review (rating, comment) |
| `GET` | `/api/v1/cms/home` | Public | Get hero banner, announcement, craftsmanship copy |
| `GET` | `/api/v1/cms/journal` | Public | List journal / editorial articles |
| `GET` | `/api/v1/cms/journal/{slug}` | Public | Get single journal article |
| `GET` | `/api/v1/cms/faq` | Public | Get categorized FAQ questions and answers |
| `GET` | `/api/v1/settings` | Public | Get public store settings (currency, free shipping threshold) |

---

## 6. Admin Platform Endpoints (`/api/v1/admin/*`)

*All admin endpoints require `ROLE_ADMIN` or specific granular manager roles (`@PreAuthorize`).*

| Module | Method | Endpoint | Description |
|---|---|---|---|
| **Dashboard** | `GET` | `/api/v1/admin/dashboard/sales-overview` | Monthly revenue, AOV, active customers, category breakdown |
| **Products** | `GET, POST, PUT, DELETE` | `/api/v1/admin/products` | Manage product catalog, variants, images, stock |
| **Categories** | `GET, POST, PUT, DELETE` | `/api/v1/admin/categories` | Manage categories and hero imagery |
| **Inventory** | `GET, POST` | `/api/v1/admin/inventory/adjust` | View stock alerts, log manual inventory movements |
| **Orders** | `GET, PATCH` | `/api/v1/admin/orders` | Filter orders, transition lifecycle status (`Processing` -> `Shipped`) |
| **Customers** | `GET, GET /{id}` | `/api/v1/admin/customers` | Customer lifetime value, order counts, tier management |
| **Payments** | `GET` | `/api/v1/admin/payments` | Payment ledger, gateway reference lookup |
| **Shipments** | `GET, POST, PATCH` | `/api/v1/admin/shipments` | Create AWB, assign courier, update transit status |
| **Returns** | `GET, PATCH` | `/api/v1/admin/returns` | Inspect return requests, approve/reject |
| **Refunds** | `GET, POST` | `/api/v1/admin/refunds` | Process approved refund against original payment |
| **Coupons** | `GET, POST, PUT, DELETE` | `/api/v1/admin/coupons` | Manage discount codes, limits, expiry dates |
| **Reviews** | `GET, PATCH` | `/api/v1/admin/reviews` | Review moderation (Approve, Hide, Delete) |
| **CMS** | `GET, PUT` | `/api/v1/admin/cms` | Update homepage copy and journal posts |
| **Users & Roles** | `GET, POST, PUT` | `/api/v1/admin/users`, `/roles` | Manage staff accounts and RBAC permissions |
| **Settings** | `GET, PUT` | `/api/v1/admin/settings` | Update store configuration, shipping thresholds, tax rates |
| **Audit Logs** | `GET` | `/api/v1/admin/audit-logs` | Immutable security and operational audit trail |
