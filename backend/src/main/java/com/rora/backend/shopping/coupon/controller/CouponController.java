package com.rora.backend.shopping.coupon.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.security.UserPrincipal;
import com.rora.backend.shopping.coupon.dto.CouponDto;
import com.rora.backend.shopping.coupon.dto.CouponValidationResult;
import com.rora.backend.shopping.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Public Coupon discovery and validation endpoints")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Get available public coupons", description = "Returns active promo codes available for shoppers")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getPublicCoupons() {
        List<CouponDto> coupons = couponService.getAllCoupons().stream()
                .filter(CouponDto::isActive)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate promo code", description = "Validates coupon code eligibility against current subtotal and customer account")
    public ResponseEntity<ApiResponse<CouponValidationResult>> validateCoupon(
            @RequestParam String code,
            @RequestParam(required = false, defaultValue = "0") BigDecimal subtotal,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String userId = currentUser != null ? currentUser.getId() : null;
        CouponValidationResult result = couponService.validateCoupon(code, subtotal, userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
