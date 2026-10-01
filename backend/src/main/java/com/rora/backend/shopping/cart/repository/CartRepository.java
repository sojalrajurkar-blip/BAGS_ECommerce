package com.rora.backend.shopping.cart.repository;

import com.rora.backend.shopping.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);

    Optional<Cart> findBySessionId(String sessionId);

    Optional<Cart> findByUserIdOrSessionId(String userId, String sessionId);
}
