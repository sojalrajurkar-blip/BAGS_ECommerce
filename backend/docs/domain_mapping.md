# RÓRA — Frontend to Backend Domain Mapping (Phase 0)

## 1. Purpose

This document maps all TypeScript domain interfaces in `src/types/domain.ts` to their authoritative Spring Boot Java Entities, Request/Response DTOs, and PostgreSQL database tables.

---

## 2. Core Domain Mapping Matrix

| Frontend TypeScript Interface | Spring Boot Entity (`com.rora.backend.*`) | PostgreSQL Table | Primary Key | Key Field Transformations & Notes |
|---|---|---|---|---|
| `Product` | `Product` | `products` | `UUID` / `String` | `price`, `originalPrice` -> `BigDecimal`, `images` -> `@ElementCollection` or JSONB, `tags` -> `text[]` / JSONB |
| `ProductVariant` / `ColorVariant` | `ProductVariant` | `product_variants` | `UUID` / `String` | `colorName`, `colorHex`, `sku`, `stock`, `price` (optional override) |
| `Category` | `Category` | `categories` | `UUID` / `String` | `slug` UNIQUE, `heroImage`, `productCount` (derived or cached) |
| `CartItem` | `CartItem` | `cart_items` | `UUID` / `String` | Foreign Keys to `cart_id`, `product_id`, `variant_id`; Authoritative unit price from DB |
| `WishlistItem` | `WishlistItem` | `wishlist_items` | `UUID` / `String` | Foreign Keys to `wishlist_id`, `product_id`, `created_at` |
| `Customer` | `Customer` | `customers` | `UUID` / `String` | Maps to `user_id`, `lifetime_value`, `orders_count`, `tier` |
| `Address` | `Address` | `addresses` | `UUID` / `String` | Linked to `customer_id` or directly embedded in `orders` (snapshot for immutability) |
| `Order` | `Order` | `orders` | `UUID` / `String` | `orderNumber` (e.g. `#RRA82910`), authoritative totals, `status`, `paymentStatus` |
| `OrderItem` | `OrderItem` | `order_items` | `UUID` / `String` | Snapshot of product name, color, image, price at moment of purchase |
| `OrderTimelineEvent` | `OrderTimelineEvent` | `order_timeline_events`| `UUID` / `String` | `step`, `time`, `completed`, `title`, `description` |
| `Review` | `Review` | `reviews` | `UUID` / `String` | `productId`, `author`, `rating`, `comment`, `verifiedPurchase`, `status` (APPROVED, PENDING, REJECTED) |
| `Coupon` | `Coupon` | `coupons` | `UUID` / `String` | `code` (UPPERCASE UNIQUE), `discountPercent`, `discountValue`, `minOrder`, `usageLimit`, `isActive` |
| `PaymentRecord` | `Payment` | `payments` | `UUID` / `String` | `orderNumber`, `amount`, `method`, `gatewayRef`, `status` (PENDING, SUCCESS, FAILED, REFUNDED) |
| `ShipmentRecord` | `Shipment` | `shipments` | `UUID` / `String` | `orderNumber`, `courier`, `awbNumber`, `destination`, `dispatchDate`, `status` |
| `ReturnRecord` | `ReturnRequest` | `returns` | `UUID` / `String` | `orderNumber`, `item`, `reason`, `inspectionStatus`, `amount`, `status` |
| `RefundRecord` | `Refund` | `refunds` | `UUID` / `String` | `returnRef`, `orderNumber`, `transactionRef`, `amount`, `status` |
| `AdminUser` | `User` | `users` | `UUID` / `String` | `name`, `email`, `password_hash`, `role_id`, `status`, `last_active` |
| `AdminRole` | `Role` | `roles` | `UUID` / `String` | `name`, `description`, permissions collection |
| `StoreSettings` | `StoreSetting` | `store_settings` | `UUID` / `String` | Key-value or typed entity (`storeName`, `currency`, `freeShippingThreshold`, `taxRate`) |
| `CMSContent` / `ContentEntry` | `CmsContent` / `JournalArticle` | `cms_content` / `journal_articles` | `UUID` / `String` | Announcement bar, Hero banner, Craftsmanship copy, Journal blog posts |
| `AuditLog` | `AuditLog` | `audit_logs` | `BIGINT` | `action`, `user_email`, `entity_type`, `entity_id`, `ip_address`, `timestamp`, `status` |
| `SalesOverview` | *Computed DTO* | *Aggregated View* | N/A | Aggregated via SQL `SUM()`, `COUNT()`, `AVG()` over `orders` and `order_items` |

---

## 3. Key Data Type Conventions

1. **Monetary Amounts**:
   - Frontend: `number` (Rupees ₹)
   - Backend Java: `BigDecimal` with 2 decimal scale (e.g. `24990.00`)
   - PostgreSQL: `NUMERIC(12, 2)`
2. **Identifiers**:
   - Frontend: `string` (e.g. `"prod-1"`, `"cat-totes"`, or UUIDs)
   - Backend Java: `String` or `UUID` (supporting both legacy human-friendly slugs/IDs and UUIDs)
   - PostgreSQL: `VARCHAR(64)` or `UUID`
3. **Timestamps**:
   - Frontend: ISO 8601 strings (`"2026-10-01T12:00:00Z"`)
   - Backend Java: `java.time.Instant` / `java.time.LocalDateTime`
   - PostgreSQL: `TIMESTAMP WITH TIME ZONE`
4. **JSON / Flexible Specifications**:
   - Frontend: `specifications?: Record<string, string>`, `careInstructions?: string[]`
   - PostgreSQL: `JSONB` for rich attributes while maintaining strong indexability on core columns.
