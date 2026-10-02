package com.rora.backend.admin.controller;

import com.rora.backend.admin.dto.AuditLogDto;
import com.rora.backend.admin.dto.CreateAuditLogRequest;
import com.rora.backend.admin.service.AuditLogService;
import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Admin Audit Logs", description = "Immutable system activity log and security audit trail")
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('AUDIT_VIEW')")
    @Operation(summary = "Get Recent Audit Logs", description = "Retrieve list of all system audit log events ordered chronologically")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getRecentAuditLogs() {
        List<AuditLogDto> logs = auditLogService.getRecentAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", logs));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('AUDIT_VIEW')")
    @Operation(summary = "Search & Filter Audit Logs", description = "Paginated audit log search with severity and status filters")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogDto>>> searchAuditLogs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        PagedResponse<AuditLogDto> response = auditLogService.searchAuditLogs(query, severity, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Audit logs search results", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('AUDIT_VIEW')")
    @Operation(summary = "Record Custom Audit Event", description = "Record a client-side or administrative security event")
    public ResponseEntity<ApiResponse<AuditLogDto>> recordAuditLog(
            @Valid @RequestBody CreateAuditLogRequest request) {
        AuditLogDto recorded = auditLogService.recordAuditLog(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Audit event recorded successfully", recorded));
    }
}
