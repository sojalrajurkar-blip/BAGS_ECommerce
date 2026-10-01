package com.rora.backend.shopping.coupon.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CouponCreateRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    private String description;

    @Builder.Default
    private String discountType = "PERCENTAGE";

    private BigDecimal discountValue;
    private Integer discountPercent;

    @Builder.Default
    private BigDecimal minimumSpend = BigDecimal.ZERO;

    private BigDecimal maxDiscountAmount;
    private Integer usageLimit;

    @Builder.Default
    private Integer perUserLimit = 1;

    @Builder.Default
    private Boolean isActive = true;

    private Instant startDate;
    private Instant expiryDate;
}
