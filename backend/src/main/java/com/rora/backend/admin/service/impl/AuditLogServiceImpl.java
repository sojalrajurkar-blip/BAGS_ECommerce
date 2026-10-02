package com.rora.backend.admin.service.impl;

import com.rora.backend.admin.dto.AuditLogDto;
import com.rora.backend.admin.dto.CreateAuditLogRequest;
import com.rora.backend.admin.entity.AuditLog;
import com.rora.backend.admin.repository.AuditLogRepository;
import com.rora.backend.admin.service.AuditLogService;
import com.rora.backend.common.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDto> getRecentAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToAuditLogDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLogDto> searchAuditLogs(String query, String severity, String status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<AuditLog> auditPage = auditLogRepository.searchAuditLogs(query, severity, status, pageable);

        List<AuditLogDto> content = auditPage.getContent().stream()
                .map(this::mapToAuditLogDto)
                .collect(Collectors.toList());

        return PagedResponse.<AuditLogDto>builder()
                .content(content)
                .page(auditPage.getNumber())
                .size(auditPage.getSize())
                .totalElements(auditPage.getTotalElements())
                .totalPages(auditPage.getTotalPages())
                .last(auditPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public AuditLogDto recordAuditLog(CreateAuditLogRequest request) {
        AuditLog logEntity = AuditLog.builder()
                .action(request.getAction())
                .actor(request.getActor())
                .target(request.getTarget())
                .entityType(request.getEntityType())
                .ipAddress(request.getIpAddress())
                .status(request.getStatus() != null ? request.getStatus() : "SUCCESS")
                .severity(request.getSeverity() != null ? request.getSeverity() : "Info")
                .details(request.getDetails())
                .build();

        AuditLog saved = auditLogRepository.save(logEntity);
        return mapToAuditLogDto(saved);
    }

    @Override
    @Transactional
    public void recordAction(String action, String actor, String target, String entityType, String severity, String status) {
        try {
            AuditLog logEntity = AuditLog.builder()
                    .action(action)
                    .actor(actor != null ? actor : "system-admin")
                    .target(target)
                    .entityType(entityType)
                    .severity(severity != null ? severity : "Info")
                    .status(status != null ? status : "SUCCESS")
                    .build();
            auditLogRepository.save(logEntity);
        } catch (Exception e) {
            log.error("Failed to write audit log asynchronously: {}", e.getMessage());
        }
    }

    private AuditLogDto mapToAuditLogDto(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .action(log.getAction())
                .user(log.getActor())
                .actor(log.getActor())
                .entity(log.getTarget() != null ? log.getTarget() : log.getEntityType())
                .target(log.getTarget())
                .entityType(log.getEntityType())
                .timestamp(formatTimeAgo(log.getCreatedAt()))
                .ipAddress(log.getIpAddress())
                .status(log.getStatus())
                .severity(log.getSeverity())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private String formatTimeAgo(Instant instant) {
        if (instant == null) return "Just now";
        Duration duration = Duration.between(instant, Instant.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return "Just now";
        long minutes = duration.toMinutes();
        if (minutes < 60) return minutes + " mins ago";
        long hours = duration.toHours();
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");
        long days = duration.toDays();
        if (days == 1) return "Yesterday";
        if (days < 30) return days + " days ago";
        return (days / 30) + " months ago";
    }
}
