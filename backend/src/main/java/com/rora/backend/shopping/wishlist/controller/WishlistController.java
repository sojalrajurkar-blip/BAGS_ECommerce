package com.rora.backend.shopping.wishlist.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.security.UserPrincipal;
import com.rora.backend.shopping.wishlist.dto.WishlistDto;
import com.rora.backend.shopping.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Wishlist", description = "Customer Wishlist persistence and 1-click toggling endpoints")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist", description = "Returns customer saved wishlist items with live product status")
    public ResponseEntity<ApiResponse<WishlistDto>> getWishlist(@AuthenticationPrincipal UserPrincipal currentUser) {
        WishlistDto wishlist = wishlistService.getWishlistDtoForUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(wishlist));
    }

    @PostMapping("/toggle/{productIdOrSlug}")
    @Operation(summary = "Toggle wishlist item", description = "1-click toggle product in/out of wishlist")
    public ResponseEntity<ApiResponse<WishlistDto>> toggleWishlistItem(
            @PathVariable String productIdOrSlug,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistDto updated = wishlistService.toggleWishlistItem(currentUser.getId(), productIdOrSlug);
        return ResponseEntity.ok(ApiResponse.success("Wishlist updated successfully", updated));
    }

    @PostMapping("/items/{productIdOrSlug}")
    @Operation(summary = "Add item to wishlist", description = "Explicitly adds a product to customer wishlist")
    public ResponseEntity<ApiResponse<WishlistDto>> addWishlistItem(
            @PathVariable String productIdOrSlug,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistDto updated = wishlistService.addWishlistItem(currentUser.getId(), productIdOrSlug);
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", updated));
    }

    @DeleteMapping("/items/{productIdOrSlug}")
    @Operation(summary = "Remove item from wishlist", description = "Removes a product from customer wishlist")
    public ResponseEntity<ApiResponse<WishlistDto>> removeWishlistItem(
            @PathVariable String productIdOrSlug,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        WishlistDto updated = wishlistService.removeWishlistItem(currentUser.getId(), productIdOrSlug);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", updated));
    }

    @DeleteMapping
    @Operation(summary = "Clear wishlist", description = "Removes all items from customer wishlist")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(@AuthenticationPrincipal UserPrincipal currentUser) {
        wishlistService.clearWishlist(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", null));
    }
}
