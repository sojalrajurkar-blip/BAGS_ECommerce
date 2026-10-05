package com.rora.backend.order.service;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.inventory.service.InventoryService;
import com.rora.backend.order.dto.*;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderItemRepository;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.order.repository.OrderTimelineEventRepository;
import com.rora.backend.shopping.cart.entity.Cart;
import com.rora.backend.shopping.cart.entity.CartItem;
import com.rora.backend.shopping.cart.repository.CartRepository;
import com.rora.backend.shopping.cart.service.CartService;
import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.service.CouponService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    public static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(1999.00);
    public static final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(199.00);

    private static final DateTimeFormatter TIMELINE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy · hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderTimelineEventRepository orderTimelineEventRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final InventoryService inventoryService;

    @Transactional
    public OrderDto placeOrder(String userId, CheckoutRequest request) {
        log.info("Processing checkout for user: {}, email: {}", userId, request.getCustomerEmail());

        User user = null;
        if (userId != null && !userId.trim().isEmpty()) {
            user = userRepository.findById(userId).orElse(null);
        }

        String orderNumber = generateUniqueOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .customerId(null)
                .customerName(request.getCustomerName().trim())
                .customerEmail(request.getCustomerEmail().trim().toLowerCase())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress() != null ? request.getBillingAddress() : request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Mock Gateway")
                .paymentStatus("Pending")
                .status("Processing")
                .carrier("Bluedart Express")
                .estimatedDelivery("2-4 Business Days")
                .trackingNumber("BD-" + (10000000 + (long)(Math.random() * 90000000)))
                .subtotal(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .timelineEvents(new ArrayList<>())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        boolean fromCart = false;

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Direct item checkout (Buy Now flow)
            for (CheckoutItemDto itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

                ProductVariant variant = null;
                String colorName = null;
                String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ? product.getImages().get(0).getImageUrl() : "";
                BigDecimal unitPrice = product.getPrice();

                if (itemReq.getVariantId() != null && !itemReq.getVariantId().trim().isEmpty()) {
                    variant = productVariantRepository.findById(itemReq.getVariantId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found: " + itemReq.getVariantId()));
                    colorName = variant.getColorName();
                    if (variant.getImage() != null && !variant.getImage().trim().isEmpty()) {
                        imageUrl = variant.getImage();
                    }
                    if (variant.getPriceOverride() != null && variant.getPriceOverride().compareTo(BigDecimal.ZERO) > 0) {
                        unitPrice = variant.getPriceOverride();
                    }
                    // Deduct stock
                    if (variant.getStock() >= itemReq.getQuantity()) {
                        variant.setStock(variant.getStock() - itemReq.getQuantity());
                        productVariantRepository.save(variant);
                    }
                }

                if (product.getStock() >= itemReq.getQuantity()) {
                    product.setStock(product.getStock() - itemReq.getQuantity());
                    productRepository.save(product);
                }

                int qty = Math.max(1, itemReq.getQuantity());
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                subtotal = subtotal.add(itemTotal);

                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .variant(variant)
                        .productName(product.getName())
                        .colorName(colorName)
                        .imageUrl(imageUrl)
                        .unitPrice(unitPrice)
                        .quantity(qty)
                        .totalPrice(itemTotal)
                        .build();

                order.addItem(orderItem);
            }
        } else {
            // Cart-based checkout
            fromCart = true;
            Optional<Cart> cartOpt = (userId != null && !userId.trim().isEmpty())
                    ? cartRepository.findByUserId(userId)
                    : (request.getSessionId() != null ? cartRepository.findBySessionId(request.getSessionId()) : Optional.empty());

            if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
                throw new BadRequestException("Cannot complete checkout: Cart is empty.");
            }

            Cart cart = cartOpt.get();
            for (CartItem cartItem : cart.getItems()) {
                Product product = cartItem.getProduct();
                ProductVariant variant = cartItem.getVariant();

                int qty = cartItem.getQuantity();
                BigDecimal itemTotal = cartItem.getTotalPrice();
                subtotal = subtotal.add(itemTotal);

                // Deduct stock
                if (variant != null && variant.getStock() >= qty) {
                    variant.setStock(variant.getStock() - qty);
                    productVariantRepository.save(variant);
                }
                if (product != null && product.getStock() >= qty) {
                    product.setStock(product.getStock() - qty);
                    productRepository.save(product);
                }

                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .variant(variant)
                        .productName(product != null ? product.getName() : "Luxury Item")
                        .colorName(variant != null ? variant.getColorName() : null)
                        .imageUrl((variant != null && variant.getImage() != null && !variant.getImage().isEmpty())
                                ? variant.getImage()
                                : (product != null && product.getImages() != null && !product.getImages().isEmpty()
                                ? product.getImages().get(0).getImageUrl() : ""))
                        .unitPrice(cartItem.getUnitPrice())
                        .quantity(qty)
                        .totalPrice(itemTotal)
                        .build();

                order.addItem(orderItem);
            }
        }

        // Coupon calculation
        String couponCode = request.getCouponCode();
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (couponCode != null && !couponCode.trim().isEmpty()) {
            CouponValidationResult couponResult = couponService.validateCoupon(couponCode, subtotal, userId);
            if (couponResult.isValid()) {
                discountAmount = couponResult.getDiscountAmount();
                order.setCouponCode(couponResult.getCode());
            } else {
                log.warn("Coupon {} invalid during checkout: {}", couponCode, couponResult.getMessage());
                throw new BadRequestException("Coupon error: " + couponResult.getMessage());
            }
        }

        // Shipping calculation
        BigDecimal shippingFee = (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0)
                ? BigDecimal.ZERO
                : STANDARD_SHIPPING_FEE;

        // Total
        BigDecimal total = subtotal.subtract(discountAmount).add(shippingFee);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setTotal(total);

        // Initial 5-step timeline
        String nowStr = TIMELINE_FORMATTER.format(Instant.now());

        order.addTimelineEvent(OrderTimelineEvent.builder()
                .stepName("Order Placed")
                .completed(true)
                .eventTime(nowStr)
                .title("Order Confirmed")
                .description("Your bespoke luxury order has been confirmed and sent to our artisan atelier.")
                .displayOrder(1)
                .build());

        order.addTimelineEvent(OrderTimelineEvent.builder()
                .stepName("Payment Verified")
                .completed(true)
                .eventTime(nowStr)
                .title("Payment Captured")
                .description("Secure transaction of ₹" + total.toPlainString() + " validated.")
                .displayOrder(2)
                .build());

        order.addTimelineEvent(OrderTimelineEvent.builder()
                .stepName("Dispatched from Hub")
                .completed(false)
                .title("Quality Inspection & Packaging")
                .description("Handcrafted packaging and white-glove inspection in progress.")
                .displayOrder(3)
                .build());

        order.addTimelineEvent(OrderTimelineEvent.builder()
                .stepName("Out for Delivery")
                .completed(false)
                .title("Courier Handover")
                .description("Handed over to Bluedart Express for priority delivery.")
                .displayOrder(4)
                .build());

        order.addTimelineEvent(OrderTimelineEvent.builder()
                .stepName("Delivered")
                .completed(false)
                .title("Signature Handover")
                .description("Expected contactless delivery at customer destination.")
                .displayOrder(5)
                .build());

        Order savedOrder = orderRepository.save(order);
        log.info("Successfully created order: {} with {} items, total: {}",
                savedOrder.getOrderNumber(), savedOrder.getItems().size(), savedOrder.getTotal());

        // Record inventory sales movement ledger
        for (OrderItem item : savedOrder.getItems()) {
            try {
                String sku = null;
                if (item.getVariant() != null && item.getVariant().getSku() != null) {
                    sku = item.getVariant().getSku();
                } else if (item.getProduct() != null && item.getProduct().getSku() != null) {
                    sku = item.getProduct().getSku();
                }
                if (sku != null) {
                    inventoryService.processSale(sku, item.getQuantity(), savedOrder.getOrderNumber(), savedOrder.getCustomerEmail());
                }
            } catch (Exception e) {
                log.error("Failed to record inventory sale ledger for item in order {}: {}", savedOrder.getOrderNumber(), e.getMessage());
            }
        }

        // Record coupon usage
        if (order.getCouponCode() != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                couponService.recordUsage(order.getCouponCode(), userId, savedOrder.getId(), discountAmount);
            } catch (Exception e) {
                log.error("Failed to record coupon usage for coupon {}: {}", order.getCouponCode(), e.getMessage());
            }
        }

        // Clear cart if placed from cart
        if (fromCart) {
            try {
                cartService.clearCart(userId, request.getSessionId());
            } catch (Exception e) {
                log.error("Failed to clear cart after order placement: {}", e.getMessage());
            }
        }

        return mapToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderByIdOrNumber(String idOrNumber) {
        Order order = orderRepository.findByIdOrOrderNumber(idOrNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID or number: " + idOrNumber));
        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public OrderDto trackOrderByNumber(String orderNumber) {
        String cleanNumber = orderNumber.trim();
        if (cleanNumber.startsWith("#")) {
            cleanNumber = cleanNumber.substring(1);
        }
        final String finalCleanNumber = cleanNumber;
        Order order = orderRepository.findByOrderNumber(orderNumber.trim())
                .or(() -> orderRepository.findByOrderNumber("#" + finalCleanNumber))
                .or(() -> orderRepository.findByOrderNumber(finalCleanNumber))
                .or(() -> orderRepository.findById(finalCleanNumber))
                .orElseThrow(() -> new ResourceNotFoundException("No order found with tracking number / ID: " + orderNumber));
        return mapToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getCustomerOrders(String userId, String email) {
        List<Order> orders = new ArrayList<>();
        if (userId != null && !userId.trim().isEmpty()) {
            orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        if (orders.isEmpty() && email != null && !email.trim().isEmpty()) {
            orders = orderRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(email.trim());
        }
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrdersAdmin(String search, String status, Pageable pageable) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("all")) ? status.trim() : null;

        Page<Order> page;
        if (cleanSearch == null && cleanStatus == null) {
            page = orderRepository.findAll(pageable);
        } else if (cleanSearch == null) {
            page = orderRepository.findByStatusIgnoreCase(cleanStatus, pageable);
        } else if (cleanStatus == null) {
            page = orderRepository.searchByKeyword(cleanSearch, pageable);
        } else {
            page = orderRepository.searchByKeywordAndStatus(cleanSearch, cleanStatus, pageable);
        }
        return page.map(this::mapToDto);
    }

    @Transactional
    public OrderDto updateOrderStatus(String idOrNumber, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findByIdOrOrderNumber(idOrNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + idOrNumber));

        String oldStatus = order.getStatus();
        String newStatus = request.getStatus().trim();
        order.setStatus(newStatus);

        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus().trim());
        }
        if (request.getCarrier() != null) {
            order.setCarrier(request.getCarrier().trim());
        }
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber().trim());
        }
        if (request.getEstimatedDelivery() != null) {
            order.setEstimatedDelivery(request.getEstimatedDelivery().trim());
        }

        String nowStr = TIMELINE_FORMATTER.format(Instant.now());

        // Update timeline status based on milestone progression
        if ("Dispatched".equalsIgnoreCase(newStatus) || "Shipped".equalsIgnoreCase(newStatus)) {
            updateTimelineStep(order, "Dispatched from Hub", true, nowStr);
        } else if ("Out for Delivery".equalsIgnoreCase(newStatus)) {
            updateTimelineStep(order, "Dispatched from Hub", true, nowStr);
            updateTimelineStep(order, "Out for Delivery", true, nowStr);
        } else if ("Delivered".equalsIgnoreCase(newStatus)) {
            updateTimelineStep(order, "Dispatched from Hub", true, nowStr);
            updateTimelineStep(order, "Out for Delivery", true, nowStr);
            updateTimelineStep(order, "Delivered", true, nowStr);
            order.setPaymentStatus("Completed");
        } else if ("Cancelled".equalsIgnoreCase(newStatus) && !"Cancelled".equalsIgnoreCase(oldStatus)) {
            // Restore inventory
            restoreStockForOrder(order);
            order.setPaymentStatus("Refunded");
        }

        Order updated = orderRepository.save(order);
        log.info("Updated status for order {}: {} -> {}", updated.getOrderNumber(), oldStatus, newStatus);
        return mapToDto(updated);
    }

    @Transactional
    public OrderDto updateOrderTracking(String idOrNumber, OrderTrackingUpdateRequest request) {
        Order order = orderRepository.findByIdOrOrderNumber(idOrNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + idOrNumber));

        if (request.getCarrier() != null) {
            order.setCarrier(request.getCarrier().trim());
        }
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber().trim());
        }
        if (request.getEstimatedDelivery() != null) {
            order.setEstimatedDelivery(request.getEstimatedDelivery().trim());
        }

        if (request.getCompletedStep() != null) {
            String timeStr = request.getEventTime() != null ? request.getEventTime() : TIMELINE_FORMATTER.format(Instant.now());
            updateTimelineStep(order, request.getCompletedStep(), true, timeStr);
        }

        Order updated = orderRepository.save(order);
        log.info("Updated tracking details for order {}", updated.getOrderNumber());
        return mapToDto(updated);
    }

    @Transactional
    public OrderDto cancelOrder(String idOrNumber, String userId) {
        Order order = orderRepository.findByIdOrOrderNumber(idOrNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + idOrNumber));

        if (userId != null && order.getUser() != null && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this order.");
        }

        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Cancelled".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Order cannot be cancelled in status: " + order.getStatus());
        }

        restoreStockForOrder(order);
        order.setStatus("Cancelled");
        order.setPaymentStatus("Refunded");

        Order updated = orderRepository.save(order);
        log.info("Cancelled order: {}", updated.getOrderNumber());
        return mapToDto(updated);
    }

    private void updateTimelineStep(Order order, String stepName, boolean completed, String eventTime) {
        for (OrderTimelineEvent event : order.getTimelineEvents()) {
            if (event.getStepName().equalsIgnoreCase(stepName.trim())) {
                event.setCompleted(completed);
                if (eventTime != null) {
                    event.setEventTime(eventTime);
                }
                return;
            }
        }
    }

    private void restoreStockForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getVariant() != null) {
                ProductVariant variant = item.getVariant();
                variant.setStock(variant.getStock() + item.getQuantity());
                productVariantRepository.save(variant);
                if (variant.getSku() != null) {
                    try {
                        inventoryService.processOrderCancellation(variant.getSku(), item.getQuantity(), order.getOrderNumber(), "system");
                    } catch (Exception e) {
                        log.error("Failed to record restoral movement for variant {}: {}", variant.getSku(), e.getMessage());
                    }
                }
            }
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
                if (item.getVariant() == null && product.getSku() != null) {
                    try {
                        inventoryService.processOrderCancellation(product.getSku(), item.getQuantity(), order.getOrderNumber(), "system");
                    } catch (Exception e) {
                        log.error("Failed to record restoral movement for product {}: {}", product.getSku(), e.getMessage());
                    }
                }
            }
        }
    }

    private String generateUniqueOrderNumber() {
        for (int i = 0; i < 20; i++) {
            int randomCode = 10000 + (int)(Math.random() * 90000);
            String candidate = "#RRA" + randomCode;
            if (orderRepository.findByOrderNumber(candidate).isEmpty()) {
                return candidate;
            }
        }
        return "#RRA" + System.currentTimeMillis() % 100000;
    }

    public OrderDto mapToDto(Order order) {
        if (order == null) return null;

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                        .productName(item.getProductName())
                        .colorName(item.getColorName())
                        .imageUrl(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        List<OrderTimelineEventDto> timelineDtos = order.getTimelineEvents().stream()
                .sorted(Comparator.comparingInt(OrderTimelineEvent::getDisplayOrder))
                .map(event -> OrderTimelineEventDto.builder()
                        .id(event.getId())
                        .stepName(event.getStepName())
                        .completed(event.isCompleted())
                        .eventTime(event.getEventTime())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .displayOrder(event.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .taxAmount(order.getTaxAmount())
                .total(order.getTotal())
                .couponCode(order.getCouponCode())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .trackingNumber(order.getTrackingNumber())
                .carrier(order.getCarrier())
                .estimatedDelivery(order.getEstimatedDelivery())
                .items(itemDtos)
                .timeline(timelineDtos)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
