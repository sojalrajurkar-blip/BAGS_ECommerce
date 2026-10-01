package com.rora.backend.shopping.wishlist.repository;

import com.rora.backend.shopping.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, String> {

    Optional<Wishlist> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
