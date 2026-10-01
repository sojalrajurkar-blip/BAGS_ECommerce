package com.rora.backend.shopping.cart.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.security.UserPrincipal;
import com.rora.backend.shopping.cart.dto.*;
import com.rora.backend.shopping.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Hybrid Guest & Customer Cart endpoints with real-time recalculation")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get active cart", description = "Retrieves cart items, discounts, shipping fees, and financial totals for authenticated user or guest session")
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto cart = cartService.getCartDto(userId, activeSession);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product variant to cart with server-side inventory and price checks")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @Valid @RequestBody AddToCartRequest request,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.addItem(userId, activeSession, request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", updated));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity", description = "Updates quantity of a specific cart line item")
    public ResponseEntity<ApiResponse<CartDto>> updateItemQuantity(
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.updateItemQuantity(userId, activeSession, itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", updated));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Removes a line item from cart")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @PathVariable String itemId,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.removeItem(userId, activeSession, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", updated));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Empties all items from active cart")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.clearCart(userId, activeSession);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", updated));
    }

    @PostMapping("/apply-coupon")
    @Operation(summary = "Apply discount coupon", description = "Applies a validated promo code to the cart subtotal")
    public ResponseEntity<ApiResponse<CartDto>> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest request,
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.applyCoupon(userId, activeSession, request.getCode());
        return ResponseEntity.ok(ApiResponse.success("Coupon applied successfully", updated));
    }

    @DeleteMapping("/remove-coupon")
    @Operation(summary = "Remove discount coupon", description = "Removes applied promo code and recalculates totals")
    public ResponseEntity<ApiResponse<CartDto>> removeCoupon(
            @RequestHeader(value = "X-Session-ID", required = false) String sessionHeader,
            @RequestParam(required = false) String sessionId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String activeSession = sessionHeader != null ? sessionHeader : sessionId;
        String userId = currentUser != null ? currentUser.getId() : null;
        CartDto updated = cartService.removeCoupon(userId, activeSession);
        return ResponseEntity.ok(ApiResponse.success("Coupon removed", updated));
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge guest cart upon login", description = "Transfers anonymous guest cart items into authenticated customer account")
    public ResponseEntity<ApiResponse<CartDto>> mergeCart(
            @Valid @RequestBody MergeCartRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        if (currentUser == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Must be logged in to merge cart", null, "/api/v1/cart/merge"));
        }
        CartDto merged = cartService.mergeGuestCart(currentUser.getId(), request.getSessionId());
        return ResponseEntity.ok(ApiResponse.success("Cart merged successfully", merged));
    }
}
