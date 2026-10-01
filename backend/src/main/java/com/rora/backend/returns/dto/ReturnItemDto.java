package com.rora.backend.returns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemDto {

    private Long id;
    private String orderItemId;
    private String productId;
    private String variantId;
    private String productName;
    private String colorName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String reason;
    private String conditionNotes;
}
