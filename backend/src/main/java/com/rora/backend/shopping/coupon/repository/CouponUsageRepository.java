package com.rora.backend.shopping.coupon.repository;

import com.rora.backend.shopping.coupon.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    long countByCouponIdAndUserId(String couponId, String userId);

    List<CouponUsage> findByCouponId(String couponId);
}
