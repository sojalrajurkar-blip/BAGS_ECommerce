package com.rora.backend.shopping.coupon.entity;

import com.rora.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Column(name = "code", length = 64, nullable = false, unique = true)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_type", length = 32, nullable = false)
    @Builder.Default
    private String discountType = "PERCENTAGE";

    @Column(name = "discount_value", precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "discount_percent")
    private Integer discountPercent;

    @Column(name = "minimum_spend", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal minimumSpend = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private int usageCount = 0;

    @Column(name = "per_user_limit")
    @Builder.Default
    private Integer perUserLimit = 1;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "expiry_date")
    private Instant expiryDate;
}
