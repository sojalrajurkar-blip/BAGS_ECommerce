package com.rora.backend.shopping.wishlist.repository;

import com.rora.backend.shopping.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {

    Optional<WishlistItem> findByWishlistIdAndProductId(String wishlistId, String productId);

    boolean existsByWishlistIdAndProductId(String wishlistId, String productId);

    List<WishlistItem> findByWishlistIdOrderByAddedAtDesc(String wishlistId);
}
