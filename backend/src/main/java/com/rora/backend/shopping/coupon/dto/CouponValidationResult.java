package com.rora.backend.shopping.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResult {

    private boolean valid;
    private String code;
    private String message;
    private BigDecimal discountAmount;
    private String discountType;
    private Integer discountPercent;
    private BigDecimal minimumSpend;
}
