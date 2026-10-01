package com.rora.backend.returns.repository;

import com.rora.backend.returns.entity.RefundRecord;
import com.rora.backend.returns.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<RefundRecord, String>, JpaSpecificationExecutor<RefundRecord> {

    Optional<RefundRecord> findByReturnRequestId(String returnId);

    Optional<RefundRecord> findByReturnRef(String returnRef);

    List<RefundRecord> findByOrderNumber(String orderNumber);

    List<RefundRecord> findByOrderId(String orderId);

    List<RefundRecord> findByCustomerEmailIgnoreCase(String customerEmail);

    long countByStatus(RefundStatus status);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RefundRecord r WHERE r.status = 'COMPLETED'")
    BigDecimal sumTotalRefundedAmount();

    @Query("SELECT r FROM RefundRecord r WHERE " +
            "(:status IS NULL OR r.status = :status) AND (" +
            ":query IS NULL OR :query = '' OR " +
            "LOWER(r.id) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.returnRef) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.transactionRef) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.method) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<RefundRecord> searchRefunds(
            @Param("query") String query,
            @Param("status") RefundStatus status,
            Pageable pageable
    );
}
