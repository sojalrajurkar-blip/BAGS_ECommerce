package com.rora.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private String action;
    // Frontend expects 'user' field
    private String user;
    private String actor;
    // Frontend expects 'entity' field
    private String entity;
    private String target;
    private String entityType;
    private String timestamp;
    private String ipAddress;
    private String status;
    private String severity;
    private Map<String, Object> details;
    private Instant createdAt;
}
