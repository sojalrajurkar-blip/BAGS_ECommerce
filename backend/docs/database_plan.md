# RÓRA — PostgreSQL Database Schema & Migration Plan (Phase 0)

## 1. Overview & Strategy

- **Database Engine**: PostgreSQL 18.4
- **Database Name**: `rora_db` (Local Port: `5432`)
- **Migration Tool**: Flyway (`db/migration/V1__initial_schema.sql`, `V2__seed_initial_data.sql`)
- **Naming Conventions**: `snake_case` table and column names, plural table names, explicit foreign keys, non-nullable audit timestamps (`created_at`, `updated_at`).

---

## 2. Table Schema Breakdown

### 2.1 Identity & Access Control
- `roles` (`id`, `name` UNIQUE, `description`, `created_at`)
- `permissions` (`id`, `name` UNIQUE, `description`)
- `role_permissions` (`role_id` FK, `permission_id` FK, PRIMARY KEY (`role_id`, `permission_id`))
- `users` (`id`, `email` UNIQUE, `password_hash`, `name`, `status`, `avatar_url`, `last_active`, `created_at`, `updated_at`)
- `user_roles` (`user_id` FK, `role_id` FK, PRIMARY KEY (`user_id`, `role_id`))

### 2.2 Customers & Addresses
- `customers` (`id`, `user_id` FK UNIQUE, `tier`, `total_spent`, `lifetime_value`, `orders_count`, `phone`, `created_at`, `updated_at`)
- `addresses` (`id`, `customer_id` FK, `full_name`, `street`, `address_line2`, `city`, `state`, `postal_code`, `country`, `phone`, `is_default`, `created_at`, `updated_at`)

### 2.3 Catalog & Variants
- `categories` (`id`, `slug` UNIQUE, `name`, `title`, `headline`, `subtitle`, `description`, `hero_image`, `created_at`, `updated_at`)
- `products` (`id`, `slug` UNIQUE, `name`, `subtitle`, `tagline`, `category_id` FK, `price`, `original_price`, `compare_at_price`, `discount`, `currency`, `rating`, `review_count`, `badge`, `stock`, `is_published`, `is_featured`, `is_new_arrival`, `is_best_seller`, `material`, `description`, `story`, `specifications` JSONB, `tags` text[], `created_at`, `updated_at`)
- `product_variants` (`id`, `product_id` FK, `sku` UNIQUE, `color_name`, `color_hex`, `image`, `stock`, `price_override`, `created_at`, `updated_at`)
- `product_images` (`id`, `product_id` FK, `image_url`, `display_order`)

### 2.4 Inventory & Movements
- `inventory` (`id`, `product_id` FK, `variant_id` FK UNIQUE, `sku` UNIQUE, `quantity_available`, `quantity_reserved`, `low_stock_threshold`, `created_at`, `updated_at`)
- `inventory_movements` (`id`, `inventory_id` FK, `movement_type` [RESTOCK, RESERVATION, SALE, RETURN, ADJUSTMENT], `quantity_change`, `previous_quantity`, `new_quantity`, `reason`, `created_by`, `created_at`)

### 2.5 Shopping (Cart & Wishlist)
- `carts` (`id`, `user_id` FK (nullable for guest), `session_token`, `coupon_code`, `created_at`, `updated_at`)
- `cart_items` (`id`, `cart_id` FK, `product_id` FK, `variant_id` FK, `quantity`, `created_at`, `updated_at`, UNIQUE(`cart_id`, `product_id`, `variant_id`))
- `wishlists` (`id`, `user_id` FK UNIQUE, `created_at`, `updated_at`)
- `wishlist_items` (`id`, `wishlist_id` FK, `product_id` FK, `created_at`, UNIQUE(`wishlist_id`, `product_id`))

### 2.6 Commerce & Coupons
- `coupons` (`id`, `code` UNIQUE, `description`, `discount_type` [PERCENTAGE, FIXED], `discount_value`, `discount_percent`, `min_order_amount`, `max_discount_amount`, `usage_limit`, `usage_count`, `per_user_limit`, `is_active`, `start_date`, `expiry_date`, `created_at`, `updated_at`)
- `coupon_usages` (`id`, `coupon_id` FK, `user_id` FK, `order_id` FK, `discount_applied`, `used_at`)

### 2.7 Orders, Payments & Fulfillment
- `orders` (`id`, `order_number` UNIQUE, `user_id` FK, `customer_name`, `customer_email`, `customer_phone`, `status` [PLACED, CONFIRMED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, RETURNED], `subtotal`, `discount_amount`, `shipping_fee`, `tax_amount`, `total`, `coupon_code`, `shipping_address` JSONB, `billing_address` JSONB, `payment_method`, `payment_status` [PENDING, SUCCESS, FAILED, REFUNDED], `tracking_number`, `carrier`, `estimated_delivery`, `created_at`, `updated_at`)
- `order_items` (`id`, `order_id` FK, `product_id` FK, `variant_id` FK, `product_name`, `color_name`, `sku`, `unit_price`, `quantity`, `total_price`, `image_url`)
- `order_timeline_events` (`id`, `order_id` FK, `step_name`, `completed`, `event_time`, `title`, `description`, `display_order`)
- `payments` (`id`, `order_id` FK, `order_number`, `amount`, `payment_method`, `gateway_reference`, `status` [INITIATED, PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED], `created_at`, `updated_at`)
- `shipments` (`id`, `order_id` FK, `order_number`, `courier`, `awb_number` UNIQUE, `destination_city`, `destination_state`, `destination_country`, `dispatch_date`, `status` [ORDER_CONFIRMED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, RETURNED], `created_at`, `updated_at`)
- `shipment_events` (`id`, `shipment_id` FK, `status`, `location`, `description`, `event_time`)

### 2.8 Returns & Refunds
- `returns` (`id`, `order_id` FK, `order_number`, `user_id` FK, `item_summary`, `reason`, `inspection_status` [PENDING, PASSED, FAILED], `refund_amount`, `status` [RETURN_REQUESTED, RETURN_APPROVED, RETURN_REJECTED, RETURN_PICKUP, RETURN_RECEIVED, REFUND_INITIATED, REFUNDED], `created_at`, `updated_at`)
- `refunds` (`id`, `return_id` FK, `order_id` FK, `order_number`, `payment_id` FK, `transaction_reference`, `amount`, `status` [PENDING, COMPLETED, FAILED], `processed_at`)

### 2.9 Reviews, CMS, Settings & Audit
- `reviews` (`id`, `product_id` FK, `user_id` FK, `author_name`, `rating`, `title`, `comment`, `verified_purchase`, `helpful_count`, `status` [APPROVED, PENDING, REJECTED], `created_at`, `updated_at`)
- `cms_content` (`id`, `content_key` UNIQUE, `title`, `content_data` JSONB, `updated_at`)
- `journal_articles` (`id`, `slug` UNIQUE, `title`, `subtitle`, `category`, `read_time`, `author`, `image_url`, `excerpt`, `content`, `tags` text[], `published_at`, `created_at`, `updated_at`)
- `store_settings` (`id`, `setting_key` UNIQUE, `setting_value`, `setting_type`, `description`, `updated_at`)
- `audit_logs` (`id` BIGSERIAL, `action`, `actor_email`, `actor_role`, `entity_type`, `entity_id`, `ip_address`, `details` JSONB, `status`, `created_at`)

---

## 3. Indexes & Constraints

- `idx_products_category_id` on `products(category_id)`
- `idx_products_slug` on `products(slug)`
- `idx_products_price` on `products(price)`
- `idx_orders_user_id` on `orders(user_id)`
- `idx_orders_order_number` on `orders(order_number)`
- `idx_inventory_sku` on `inventory(sku)`
- `idx_coupons_code` on `coupons(code)`
- `idx_reviews_product_id` on `reviews(product_id)`
- `idx_audit_logs_created_at` on `audit_logs(created_at)`
