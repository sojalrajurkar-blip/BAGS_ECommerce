package com.rora.backend.shopping.cart.repository;

import com.rora.backend.shopping.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCartId(String cartId);

    Optional<CartItem> findByCartIdAndProductIdAndVariantId(String cartId, String productId, String variantId);

    Optional<CartItem> findByCartIdAndProductIdAndVariantIsNull(String cartId, String productId);
}
