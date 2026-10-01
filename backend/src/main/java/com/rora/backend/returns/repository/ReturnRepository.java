package com.rora.backend.returns.repository;

import com.rora.backend.returns.entity.ReturnRequest;
import com.rora.backend.returns.entity.ReturnStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReturnRepository extends JpaRepository<ReturnRequest, String>, JpaSpecificationExecutor<ReturnRequest> {

    List<ReturnRequest> findByCustomerEmailIgnoreCase(String customerEmail);

    List<ReturnRequest> findByOrderNumber(String orderNumber);

    List<ReturnRequest> findByOrderId(String orderId);

    long countByStatus(ReturnStatus status);

    @Query("SELECT r FROM ReturnRequest r WHERE " +
            "(:status IS NULL OR r.status = :status) AND (" +
            ":query IS NULL OR :query = '' OR " +
            "LOWER(r.id) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.item) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.reason) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ReturnRequest> searchReturns(
            @Param("query") String query,
            @Param("status") ReturnStatus status,
            Pageable pageable
    );
}
