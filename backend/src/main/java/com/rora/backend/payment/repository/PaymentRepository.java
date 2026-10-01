package com.rora.backend.payment.repository;

import com.rora.backend.payment.entity.Payment;
import com.rora.backend.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findAllByCustomerEmail(String customerEmail);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal sumSuccessfulVolume();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'REFUNDED' OR p.status = 'PARTIALLY_REFUNDED'")
    BigDecimal sumRefundedVolume();
}
