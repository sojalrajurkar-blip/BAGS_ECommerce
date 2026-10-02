package com.rora.backend.admin.repository;

import com.rora.backend.admin.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:query IS NULL OR :query = '' OR LOWER(a.action) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.actor) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.target) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.entityType) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:severity IS NULL OR :severity = '' OR :severity = 'ALL' OR LOWER(a.severity) = LOWER(:severity)) AND " +
           "(:status IS NULL OR :status = '' OR :status = 'ALL' OR LOWER(a.status) = LOWER(:status)) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> searchAuditLogs(@Param("query") String query,
                                   @Param("severity") String severity,
                                   @Param("status") String status,
                                   Pageable pageable);
}
