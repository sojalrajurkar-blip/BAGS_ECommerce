package com.rora.backend.shopping.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private String id;
    private String productId;
    private String productSlug;
    private String productName;
    private String variantId;
    private String variantName;
    private String colorName;
    private String colorHex;
    private String image;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    private boolean inStock;
    private int availableStock;
}
