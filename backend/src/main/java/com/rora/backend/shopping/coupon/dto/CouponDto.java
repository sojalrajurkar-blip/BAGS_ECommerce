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
public class CouponDto {

    private String id;
    private String code;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private Integer discountPercent;
    private BigDecimal minimumSpend;
    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;
    private int usageCount;
    private Integer perUserLimit;
    private boolean active;
    private Instant startDate;
    private Instant expiryDate;
    private Instant createdAt;
}
