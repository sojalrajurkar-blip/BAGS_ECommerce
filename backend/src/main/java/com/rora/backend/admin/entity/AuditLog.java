package com.rora.backend.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "action", length = 128, nullable = false)
    private String action;

    @Column(name = "actor", length = 255, nullable = false)
    private String actor;

    @Column(name = "target", length = 255)
    private String target;

    @Column(name = "entity_type", length = 128)
    private String entityType;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "status", length = 64, nullable = false)
    @Builder.Default
    private String status = "SUCCESS";

    @Column(name = "severity", length = 32, nullable = false)
    @Builder.Default
    private String severity = "INFO";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = "SUCCESS";
        }
        if (this.severity == null) {
            this.severity = "INFO";
        }
    }
}
