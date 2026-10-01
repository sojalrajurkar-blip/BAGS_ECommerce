package com.rora.backend.shopping.wishlist.service;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.shopping.wishlist.dto.WishlistDto;
import com.rora.backend.shopping.wishlist.dto.WishlistItemDto;
import com.rora.backend.shopping.wishlist.entity.Wishlist;
import com.rora.backend.shopping.wishlist.entity.WishlistItem;
import com.rora.backend.shopping.wishlist.repository.WishlistItemRepository;
import com.rora.backend.shopping.wishlist.repository.WishlistRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Transactional
    public Wishlist getOrCreateWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                    Wishlist wishlist = Wishlist.builder()
                            .user(user)
                            .build();
                    return wishlistRepository.save(wishlist);
                });
    }

    @Transactional(readOnly = true)
    public WishlistDto getWishlistDtoForUser(String userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto toggleWishlistItem(String userId, String productIdOrSlug) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productService.findEntityByIdOrSlug(productIdOrSlug);

        Optional<WishlistItem> existing = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), product.getId());

        if (existing.isPresent()) {
            wishlist.removeItem(existing.get());
            wishlistItemRepository.delete(existing.get());
            log.info("Removed product {} from wishlist for user {}", product.getSlug(), userId);
        } else {
            WishlistItem newItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .addedAt(Instant.now())
                    .build();
            wishlist.addItem(newItem);
            wishlistItemRepository.save(newItem);
            log.info("Added product {} to wishlist for user {}", product.getSlug(), userId);
        }

        wishlist.setUpdatedAt(Instant.now());
        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToDto(saved);
    }

    @Transactional
    public WishlistDto addWishlistItem(String userId, String productIdOrSlug) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productService.findEntityByIdOrSlug(productIdOrSlug);

        if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getId(), product.getId())) {
            WishlistItem newItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .addedAt(Instant.now())
                    .build();
            wishlist.addItem(newItem);
            wishlistItemRepository.save(newItem);
            wishlist.setUpdatedAt(Instant.now());
            wishlistRepository.save(wishlist);
        }

        return mapToDto(wishlist);
    }

    @Transactional
    public WishlistDto removeWishlistItem(String userId, String productIdOrSlug) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        Product product = productService.findEntityByIdOrSlug(productIdOrSlug);

        Optional<WishlistItem> existing = wishlistItemRepository.findByWishlistIdAndProductId(wishlist.getId(), product.getId());
        if (existing.isPresent()) {
            wishlist.removeItem(existing.get());
            wishlistItemRepository.delete(existing.get());
            wishlist.setUpdatedAt(Instant.now());
            wishlistRepository.save(wishlist);
        }

        return mapToDto(wishlist);
    }

    @Transactional
    public void clearWishlist(String userId) {
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlist.getItems().clear();
        wishlist.setUpdatedAt(Instant.now());
        wishlistRepository.save(wishlist);
    }

    @Transactional(readOnly = true)
    public boolean isProductInWishlist(String userId, String productId) {
        if (userId == null) return false;
        Optional<Wishlist> wishlist = wishlistRepository.findByUserId(userId);
        return wishlist.map(w -> wishlistItemRepository.existsByWishlistIdAndProductId(w.getId(), productId)).orElse(false);
    }

    private WishlistDto mapToDto(Wishlist wishlist) {
        List<WishlistItemDto> itemDtos = wishlistItemRepository.findByWishlistIdOrderByAddedAtDesc(wishlist.getId())
                .stream()
                .map(item -> WishlistItemDto.builder()
                        .id(item.getId())
                        .product(productService.mapToSummaryDto(item.getProduct()))
                        .addedAt(item.getAddedAt())
                        .build())
                .collect(Collectors.toList());

        return WishlistDto.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .itemCount(itemDtos.size())
                .items(itemDtos)
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }
}
