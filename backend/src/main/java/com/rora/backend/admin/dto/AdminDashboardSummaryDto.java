package com.rora.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryDto {
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalCustomers;
    private Long lowStockCount;
    private Long pendingReturnsCount;
    private Long activeShipmentsCount;
    private Long pendingReviewsCount;
    private SalesOverviewDto salesOverview;
}
