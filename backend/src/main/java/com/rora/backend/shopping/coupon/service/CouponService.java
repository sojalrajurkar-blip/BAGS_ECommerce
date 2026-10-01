package com.rora.backend.shopping.coupon.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.DuplicateResourceException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.shopping.coupon.dto.*;
import com.rora.backend.shopping.coupon.entity.Coupon;
import com.rora.backend.shopping.coupon.entity.CouponUsage;
import com.rora.backend.shopping.coupon.repository.CouponRepository;
import com.rora.backend.shopping.coupon.repository.CouponUsageRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CouponValidationResult validateCoupon(String code, BigDecimal subtotal, String userId) {
        if (code == null || code.trim().isEmpty()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .message("Coupon code cannot be empty")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        String normalizedCode = code.trim().toUpperCase();
        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(normalizedCode);

        if (couponOpt.isEmpty()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .message("Invalid coupon code")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        Coupon coupon = couponOpt.get();

        if (!coupon.isActive()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .message("Coupon is no longer active")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        Instant now = Instant.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .message("Coupon promotion has not started yet")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .message("Coupon has expired")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .message("Coupon usage limit has been reached")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (subtotal != null && coupon.getMinimumSpend() != null && subtotal.compareTo(coupon.getMinimumSpend()) < 0) {
            return CouponValidationResult.builder()
                    .valid(false)
                    .code(normalizedCode)
                    .minimumSpend(coupon.getMinimumSpend())
                    .message(String.format("Minimum order subtotal of ₹%s required for this coupon", coupon.getMinimumSpend()))
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        if (userId != null && coupon.getPerUserLimit() != null && coupon.getPerUserLimit() > 0) {
            long userUsages = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), userId);
            if (userUsages >= coupon.getPerUserLimit()) {
                return CouponValidationResult.builder()
                        .valid(false)
                        .code(normalizedCode)
                        .message("You have already reached the maximum usage limit for this coupon")
                        .discountAmount(BigDecimal.ZERO)
                        .build();
            }
        }

        // Calculate discount amount
        BigDecimal discount = BigDecimal.ZERO;
        if (subtotal != null && subtotal.compareTo(BigDecimal.ZERO) > 0) {
            if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType()) && coupon.getDiscountPercent() != null) {
                BigDecimal percent = BigDecimal.valueOf(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                discount = subtotal.multiply(percent).setScale(2, RoundingMode.HALF_UP);

                if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                    discount = coupon.getMaxDiscountAmount();
                }
            } else if (coupon.getDiscountValue() != null) {
                discount = coupon.getDiscountValue().min(subtotal);
            }
        }

        return CouponValidationResult.builder()
                .valid(true)
                .code(normalizedCode)
                .message("Coupon applied successfully")
                .discountAmount(discount)
                .discountType(coupon.getDiscountType())
                .discountPercent(coupon.getDiscountPercent())
                .minimumSpend(coupon.getMinimumSpend())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CouponDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CouponDto getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return mapToDto(coupon);
    }

    @Transactional
    public CouponDto createCoupon(CouponCreateRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Coupon code already exists: " + code);
        }

        Coupon coupon = Coupon.builder()
                .code(code)
                .description(request.getDescription())
                .discountType(request.getDiscountType() != null ? request.getDiscountType().toUpperCase() : "PERCENTAGE")
                .discountValue(request.getDiscountValue())
                .discountPercent(request.getDiscountPercent())
                .minimumSpend(request.getMinimumSpend() != null ? request.getMinimumSpend() : BigDecimal.ZERO)
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .usageLimit(request.getUsageLimit())
                .usageCount(0)
                .perUserLimit(request.getPerUserLimit() != null ? request.getPerUserLimit() : 1)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .expiryDate(request.getExpiryDate())
                .build();

        Coupon saved = couponRepository.save(coupon);
        log.info("Created coupon: {}", saved.getCode());
        return mapToDto(saved);
    }

    @Transactional
    public CouponDto updateCoupon(String id, CouponUpdateRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));

        if (request.getCode() != null) {
            String newCode = request.getCode().trim().toUpperCase();
            if (!newCode.equalsIgnoreCase(coupon.getCode()) && couponRepository.existsByCodeIgnoreCase(newCode)) {
                throw new DuplicateResourceException("Coupon code already exists: " + newCode);
            }
            coupon.setCode(newCode);
        }

        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getDiscountType() != null) coupon.setDiscountType(request.getDiscountType().toUpperCase());
        if (request.getDiscountValue() != null) coupon.setDiscountValue(request.getDiscountValue());
        if (request.getDiscountPercent() != null) coupon.setDiscountPercent(request.getDiscountPercent());
        if (request.getMinimumSpend() != null) coupon.setMinimumSpend(request.getMinimumSpend());
        if (request.getMaxDiscountAmount() != null) coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getPerUserLimit() != null) coupon.setPerUserLimit(request.getPerUserLimit());
        if (request.getIsActive() != null) coupon.setActive(request.getIsActive());
        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getExpiryDate() != null) coupon.setExpiryDate(request.getExpiryDate());

        Coupon updated = couponRepository.save(coupon);
        log.info("Updated coupon: {}", updated.getCode());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        couponRepository.delete(coupon);
        log.info("Deleted coupon: {}", coupon.getCode());
    }

    @Transactional
    public void recordUsage(String couponCode, String userId, String orderId, BigDecimal discountAmount) {
        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(couponCode);
        if (couponOpt.isEmpty()) return;

        Coupon coupon = couponOpt.get();
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        CouponUsage usage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .orderId(orderId)
                .discountAmount(discountAmount != null ? discountAmount : BigDecimal.ZERO)
                .usedAt(Instant.now())
                .build();

        couponUsageRepository.save(usage);
        log.info("Recorded coupon usage: {} for order: {}", coupon.getCode(), orderId);
    }

    public CouponDto mapToDto(Coupon coupon) {
        if (coupon == null) return null;
        return CouponDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .discountPercent(coupon.getDiscountPercent())
                .minimumSpend(coupon.getMinimumSpend())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .perUserLimit(coupon.getPerUserLimit())
                .active(coupon.isActive())
                .startDate(coupon.getStartDate())
                .expiryDate(coupon.getExpiryDate())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
