package com.rora.backend.admin.controller;

import com.rora.backend.admin.dto.AdminDashboardSummaryDto;
import com.rora.backend.admin.dto.SalesOverviewDto;
import com.rora.backend.admin.service.AdminDashboardService;
import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics & Dashboard", description = "Backoffice KPI metrics, executive summary, and category sales overview")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PRODUCT_MANAGER', 'ORDER_MANAGER')")
    @Operation(summary = "Get Dashboard KPI Summary", description = "Consolidated counts for revenue, orders, customers, low-stock, shipments, and pending returns")
    public ResponseEntity<ApiResponse<AdminDashboardSummaryDto>> getDashboardSummary() {
        AdminDashboardSummaryDto summary = adminDashboardService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PRODUCT_MANAGER', 'ORDER_MANAGER')")
    @Operation(summary = "Get Sales Analytics Overview", description = "Monthly revenue, growth percentages, AOV metrics, and category revenue distribution")
    public ResponseEntity<ApiResponse<SalesOverviewDto>> getSalesOverview() {
        SalesOverviewDto overview = adminDashboardService.getSalesOverview();
        return ResponseEntity.ok(ApiResponse.success("Sales overview retrieved successfully", overview));
    }
}
