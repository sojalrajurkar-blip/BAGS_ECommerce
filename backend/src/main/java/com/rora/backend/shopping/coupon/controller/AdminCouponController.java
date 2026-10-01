package com.rora.backend.shopping.coupon.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.shopping.coupon.dto.CouponCreateRequest;
import com.rora.backend.shopping.coupon.dto.CouponDto;
import com.rora.backend.shopping.coupon.dto.CouponUpdateRequest;
import com.rora.backend.shopping.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MARKETING_MANAGER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Coupons", description = "Admin CRUD operations for Promotional Discount Coupons")
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    @Operation(summary = "Get all coupons (Admin)", description = "Returns full list of coupons with usage counts")
    public ResponseEntity<ApiResponse<List<CouponDto>>> getAllCoupons() {
        List<CouponDto> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(ApiResponse.success(coupons));
    }

    @PostMapping
    @Operation(summary = "Create promo coupon", description = "Creates a new coupon code with usage thresholds and date bounds")
    public ResponseEntity<ApiResponse<CouponDto>> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        CouponDto created = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update coupon", description = "Updates existing coupon configurations")
    public ResponseEntity<ApiResponse<CouponDto>> updateCoupon(
            @PathVariable String id,
            @RequestBody CouponUpdateRequest request
    ) {
        CouponDto updated = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete coupon", description = "Deletes coupon code by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully", null));
    }
}
