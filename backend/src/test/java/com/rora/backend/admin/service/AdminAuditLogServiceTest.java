package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.AuditLogDto;
import com.rora.backend.admin.dto.CreateAuditLogRequest;
import com.rora.backend.admin.entity.AuditLog;
import com.rora.backend.admin.repository.AuditLogRepository;
import com.rora.backend.admin.service.impl.AuditLogServiceImpl;
import com.rora.backend.common.PagedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @Test
    @DisplayName("Should return recent audit logs ordered chronologically")
    void testGetRecentAuditLogs() {
        AuditLog log = AuditLog.builder()
                .id(1L)
                .action("Product Price Updated")
                .actor("Sarah Jenkins")
                .target("The Nomad Backpack")
                .entityType("Product")
                .status("SUCCESS")
                .severity("Info")
                .createdAt(Instant.now())
                .build();

        when(auditLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(log));

        List<AuditLogDto> results = auditLogService.getRecentAuditLogs();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAction()).isEqualTo("Product Price Updated");
        assertThat(results.get(0).getUser()).isEqualTo("Sarah Jenkins");
    }

    @Test
    @DisplayName("Should search audit logs with pagination")
    void testSearchAuditLogs() {
        AuditLog log = AuditLog.builder()
                .id(2L)
                .action("Order Dispatched")
                .actor("Logistics Service")
                .target("Order #RRA89241")
                .entityType("Order")
                .status("SUCCESS")
                .severity("Success")
                .createdAt(Instant.now())
                .build();

        Page<AuditLog> page = new PageImpl<>(List.of(log));
        when(auditLogRepository.searchAuditLogs(eq("order"), eq("Success"), eq("SUCCESS"), any(Pageable.class)))
                .thenReturn(page);

        PagedResponse<AuditLogDto> response = auditLogService.searchAuditLogs("order", "Success", "SUCCESS", 0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should record custom audit log event")
    void testRecordAuditLog() {
        CreateAuditLogRequest req = CreateAuditLogRequest.builder()
                .action("Coupon Code Created")
                .actor("Kabir Verma")
                .target("JOURNEY20")
                .entityType("Coupon")
                .severity("Info")
                .status("SUCCESS")
                .details(Map.of("discount", 20))
                .build();

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> {
            AuditLog a = inv.getArgument(0);
            a.setId(3L);
            a.setCreatedAt(Instant.now());
            return a;
        });

        AuditLogDto recorded = auditLogService.recordAuditLog(req);

        assertThat(recorded).isNotNull();
        assertThat(recorded.getAction()).isEqualTo("Coupon Code Created");
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
