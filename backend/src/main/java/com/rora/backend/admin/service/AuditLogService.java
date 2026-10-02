package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.AuditLogDto;
import com.rora.backend.admin.dto.CreateAuditLogRequest;
import com.rora.backend.common.PagedResponse;

import java.util.List;

public interface AuditLogService {

    List<AuditLogDto> getRecentAuditLogs();

    PagedResponse<AuditLogDto> searchAuditLogs(String query, String severity, String status, int page, int size);

    AuditLogDto recordAuditLog(CreateAuditLogRequest request);

    void recordAction(String action, String actor, String target, String entityType, String severity, String status);
}
