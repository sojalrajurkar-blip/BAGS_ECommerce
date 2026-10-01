package com.rora.backend.shopping.cart.service;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.shopping.cart.dto.*;
import com.rora.backend.shopping.cart.entity.Cart;
import com.rora.backend.shopping.cart.entity.CartItem;
import com.rora.backend.shopping.cart.repository.CartItemRepository;
import com.rora.backend.shopping.cart.repository.CartRepository;
import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.service.CouponService;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    public static final BigDecimal FREE_SHIPPING_THRESHOLD = BigDecimal.valueOf(1999.00);
    public static final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(199.00);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductVariantRepository productVariantRepository;
    private final CouponService couponService;

    @Transactional
    public Cart getOrCreateCart(String userId, String sessionId) {
        if (userId != null && !userId.trim().isEmpty()) {
            return cartRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                        Cart cart = Cart.builder()
                                .user(user)
                                .build();
                        return cartRepository.save(cart);
                    });
        } else if (sessionId != null && !sessionId.trim().isEmpty()) {
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> {
                        Cart cart = Cart.builder()
                                .sessionId(sessionId)
                                .build();
                        return cartRepository.save(cart);
                    });
        } else {
            // Generate guest session ID if none provided
            String generatedSession = UUID.randomUUID().toString();
            Cart cart = Cart.builder()
                    .sessionId(generatedSession)
                    .build();
            return cartRepository.save(cart);
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(String userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        return calculateAndMapCart(cart, userId);
    }

    @Transactional
    public CartDto addItem(String userId, String sessionId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId, sessionId);
        Product product = productService.findEntityByIdOrSlug(request.getProductId());

        ProductVariant variant = null;
        if (request.getVariantId() != null && !request.getVariantId().trim().isEmpty()) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + request.getVariantId()));
            if (!variant.getProduct().getId().equals(product.getId())) {
                throw new BadRequestException("Variant does not belong to the selected product");
            }
        } else if (!product.getVariants().isEmpty()) {
            // Default to first variant if product has variants
            variant = product.getVariants().get(0);
        }

        int availableStock = variant != null ? variant.getStock() : product.getStock();
        if (availableStock <= 0) {
            throw new BadRequestException("Product is currently out of stock");
        }

        BigDecimal unitPrice = (variant != null && variant.getPriceOverride() != null)
                ? variant.getPriceOverride()
                : product.getPrice();

        // Check if item already exists in cart
        String variantId = variant != null ? variant.getId() : null;
        Optional<CartItem> existingItemOpt = variantId != null
                ? cartItemRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), product.getId(), variantId)
                : cartItemRepository.findByCartIdAndProductIdAndVariantIsNull(cart.getId(), product.getId());

        int quantityToAdd = Math.max(1, request.getQuantity());

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQuantity = existing.getQuantity() + quantityToAdd;
            if (newQuantity > availableStock) {
                newQuantity = availableStock;
            }
            existing.setQuantity(newQuantity);
            existing.setUnitPrice(unitPrice);
            existing.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(existing);
        } else {
            int quantity = Math.min(quantityToAdd, availableStock);
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .totalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        log.info("Added item to cart {} for product {}", cart.getId(), product.getSlug());
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto updateItemQuantity(String userId, String sessionId, String itemId, int quantity) {
        Cart cart = getOrCreateCart(userId, sessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to active cart");
        }

        if (quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            int availableStock = item.getVariant() != null ? item.getVariant().getStock() : item.getProduct().getStock();
            int finalQuantity = Math.min(quantity, Math.max(1, availableStock));

            BigDecimal unitPrice = (item.getVariant() != null && item.getVariant().getPriceOverride() != null)
                    ? item.getVariant().getPriceOverride()
                    : item.getProduct().getPrice();

            item.setQuantity(finalQuantity);
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(finalQuantity)));
            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto removeItem(String userId, String sessionId, String itemId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to active cart");
        }

        cart.removeItem(item);
        cartItemRepository.delete(item);
        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto clearCart(String userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        cart.getItems().clear();
        cart.setCouponCode(null);
        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto applyCoupon(String userId, String sessionId, String couponCode) {
        Cart cart = getOrCreateCart(userId, sessionId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        if (items.isEmpty()) {
            throw new BadRequestException("Cannot apply coupon to an empty cart");
        }

        BigDecimal subtotal = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CouponValidationResult validation = couponService.validateCoupon(couponCode, subtotal, userId);
        if (!validation.isValid()) {
            throw new BadRequestException(validation.getMessage());
        }

        cart.setCouponCode(validation.getCode());
        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto removeCoupon(String userId, String sessionId) {
        Cart cart = getOrCreateCart(userId, sessionId);
        cart.setCouponCode(null);
        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        return calculateAndMapCart(saved, userId);
    }

    @Transactional
    public CartDto mergeGuestCart(String userId, String guestSessionId) {
        if (userId == null || guestSessionId == null || guestSessionId.trim().isEmpty()) {
            return getCartDto(userId, null);
        }

        Optional<Cart> guestCartOpt = cartRepository.findBySessionId(guestSessionId);
        if (guestCartOpt.isEmpty() || guestCartOpt.get().getItems().isEmpty()) {
            return getCartDto(userId, null);
        }

        Cart guestCart = guestCartOpt.get();
        Cart userCart = getOrCreateCart(userId, null);

        for (CartItem guestItem : guestCart.getItems()) {
            String variantId = guestItem.getVariant() != null ? guestItem.getVariant().getId() : null;
            Optional<CartItem> existingUserItem = variantId != null
                    ? cartItemRepository.findByCartIdAndProductIdAndVariantId(userCart.getId(), guestItem.getProduct().getId(), variantId)
                    : cartItemRepository.findByCartIdAndProductIdAndVariantIsNull(userCart.getId(), guestItem.getProduct().getId());

            int availableStock = guestItem.getVariant() != null ? guestItem.getVariant().getStock() : guestItem.getProduct().getStock();

            if (existingUserItem.isPresent()) {
                CartItem userItem = existingUserItem.get();
                int combinedQty = Math.min(userItem.getQuantity() + guestItem.getQuantity(), availableStock);
                userItem.setQuantity(combinedQty);
                userItem.setTotalPrice(userItem.getUnitPrice().multiply(BigDecimal.valueOf(combinedQty)));
                cartItemRepository.save(userItem);
            } else {
                CartItem newUserItem = CartItem.builder()
                        .cart(userCart)
                        .product(guestItem.getProduct())
                        .variant(guestItem.getVariant())
                        .quantity(Math.min(guestItem.getQuantity(), availableStock))
                        .unitPrice(guestItem.getUnitPrice())
                        .totalPrice(guestItem.getUnitPrice().multiply(BigDecimal.valueOf(Math.min(guestItem.getQuantity(), availableStock))))
                        .build();
                userCart.addItem(newUserItem);
                cartItemRepository.save(newUserItem);
            }
        }

        if (userCart.getCouponCode() == null && guestCart.getCouponCode() != null) {
            userCart.setCouponCode(guestCart.getCouponCode());
        }

        // Clean up guest cart
        cartRepository.delete(guestCart);

        userCart.setUpdatedAt(Instant.now());
        Cart savedUserCart = cartRepository.save(userCart);
        log.info("Merged guest session cart {} into user cart {}", guestSessionId, userCart.getId());
        return calculateAndMapCart(savedUserCart, userId);
    }

    private CartDto calculateAndMapCart(Cart cart, String userId) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalUnits = 0;

        for (CartItem item : items) {
            Product product = item.getProduct();
            ProductVariant variant = item.getVariant();

            // Zero-Trust: re-verify price from product/variant
            BigDecimal currentPrice = (variant != null && variant.getPriceOverride() != null)
                    ? variant.getPriceOverride()
                    : product.getPrice();

            int availableStock = variant != null ? variant.getStock() : product.getStock();
            boolean inStock = availableStock > 0;

            BigDecimal itemTotal = currentPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            totalUnits += item.getQuantity();

            String image = (variant != null && variant.getImage() != null) ? variant.getImage()
                    : (!product.getImages().isEmpty() ? product.getImages().get(0).getImageUrl() : null);

            itemDtos.add(CartItemDto.builder()
                    .id(item.getId())
                    .productId(product.getId())
                    .productSlug(product.getSlug())
                    .productName(product.getName())
                    .variantId(variant != null ? variant.getId() : null)
                    .variantName(variant != null ? variant.getName() : null)
                    .colorName(variant != null ? variant.getColorName() : null)
                    .colorHex(variant != null ? variant.getColorHex() : null)
                    .image(image)
                    .unitPrice(currentPrice)
                    .quantity(item.getQuantity())
                    .totalPrice(itemTotal)
                    .inStock(inStock)
                    .availableStock(availableStock)
                    .build());
        }

        // Coupon calculation
        BigDecimal discountAmount = BigDecimal.ZERO;
        String appliedCoupon = cart.getCouponCode();

        if (appliedCoupon != null && !appliedCoupon.trim().isEmpty() && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            CouponValidationResult couponResult = couponService.validateCoupon(appliedCoupon, subtotal, userId);
            if (couponResult.isValid()) {
                discountAmount = couponResult.getDiscountAmount();
            } else {
                appliedCoupon = null; // Auto-invalidate if subtotal dropped below minimum spend
            }
        }

        // Shipping calculation
        boolean isFreeShipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 || subtotal.compareTo(BigDecimal.ZERO) == 0;
        BigDecimal shippingFee = isFreeShipping ? BigDecimal.ZERO : STANDARD_SHIPPING_FEE;

        BigDecimal freeShippingRemaining = FREE_SHIPPING_THRESHOLD.subtract(subtotal);
        if (freeShippingRemaining.compareTo(BigDecimal.ZERO) < 0) {
            freeShippingRemaining = BigDecimal.ZERO;
        }

        // Tax (inclusive in final prices, but calculate 18% GST portion for transparent display if needed)
        BigDecimal taxAmount = BigDecimal.ZERO; // Prices are GST inclusive

        BigDecimal grandTotal = subtotal.subtract(discountAmount).add(shippingFee);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        return CartDto.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .sessionId(cart.getSessionId())
                .items(itemDtos)
                .itemCount(totalUnits)
                .uniqueItemCount(itemDtos.size())
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .taxAmount(taxAmount)
                .total(grandTotal)
                .appliedCoupon(appliedCoupon)
                .freeShippingThreshold(FREE_SHIPPING_THRESHOLD)
                .freeShippingRemaining(freeShippingRemaining)
                .freeShippingEligible(isFreeShipping)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
