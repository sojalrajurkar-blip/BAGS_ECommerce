package com.rora.backend.shopping.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUpdateRequest {

    private String code;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private Integer discountPercent;
    private BigDecimal minimumSpend;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private Integer perUserLimit;
    private Boolean isActive;
    private Instant startDate;
    private Instant expiryDate;
}
