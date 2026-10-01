package com.rora.backend.shopping.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private String id;
    private String userId;
    private String sessionId;
    private List<CartItemDto> items;
    private int itemCount;
    private int uniqueItemCount;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String appliedCoupon;
    private BigDecimal freeShippingThreshold;
    private BigDecimal freeShippingRemaining;
    private boolean freeShippingEligible;
    private Instant updatedAt;
}
