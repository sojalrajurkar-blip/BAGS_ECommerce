package com.rora.backend.shopping;

import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.entity.Coupon;
import com.rora.backend.shopping.coupon.repository.CouponRepository;
import com.rora.backend.shopping.coupon.repository.CouponUsageRepository;
import com.rora.backend.shopping.coupon.service.CouponService;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponUsageRepository couponUsageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CouponService couponService;

    private Coupon mockCoupon;

    @BeforeEach
    void setUp() {
        mockCoupon = Coupon.builder()
                .id("coup-1")
                .code("RORA10")
                .description("10% off")
                .discountType("PERCENTAGE")
                .discountPercent(10)
                .minimumSpend(BigDecimal.valueOf(1999.00))
                .maxDiscountAmount(BigDecimal.valueOf(2000.00))
                .usageLimit(100)
                .usageCount(5)
                .perUserLimit(2)
                .isActive(true)
                .startDate(Instant.now().minus(5, ChronoUnit.DAYS))
                .expiryDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();
    }

    @Test
    void testValidateCoupon_Success() {
        when(couponRepository.findByCodeIgnoreCase("RORA10")).thenReturn(Optional.of(mockCoupon));
        when(couponUsageRepository.countByCouponIdAndUserId("coup-1", "user-1")).thenReturn(0L);

        CouponValidationResult result = couponService.validateCoupon("RORA10", BigDecimal.valueOf(5000.00), "user-1");

        assertTrue(result.isValid());
        assertEquals("RORA10", result.getCode());
        assertEquals(new BigDecimal("500.00"), result.getDiscountAmount());
    }

    @Test
    void testValidateCoupon_BelowMinimumSpend() {
        when(couponRepository.findByCodeIgnoreCase("RORA10")).thenReturn(Optional.of(mockCoupon));

        CouponValidationResult result = couponService.validateCoupon("RORA10", BigDecimal.valueOf(1000.00), "user-1");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Minimum order subtotal"));
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
    }

    @Test
    void testValidateCoupon_Expired() {
        mockCoupon.setExpiryDate(Instant.now().minus(1, ChronoUnit.DAYS));
        when(couponRepository.findByCodeIgnoreCase("RORA10")).thenReturn(Optional.of(mockCoupon));

        CouponValidationResult result = couponService.validateCoupon("RORA10", BigDecimal.valueOf(3000.00), "user-1");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("expired"));
    }

    @Test
    void testValidateCoupon_ExceededUserLimit() {
        when(couponRepository.findByCodeIgnoreCase("RORA10")).thenReturn(Optional.of(mockCoupon));
        when(couponUsageRepository.countByCouponIdAndUserId("coup-1", "user-1")).thenReturn(2L);

        CouponValidationResult result = couponService.validateCoupon("RORA10", BigDecimal.valueOf(3000.00), "user-1");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("maximum usage limit"));
    }
}
