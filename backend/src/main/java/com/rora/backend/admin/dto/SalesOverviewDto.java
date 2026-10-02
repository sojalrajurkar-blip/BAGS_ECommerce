package com.rora.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOverviewDto {
    private String monthlyRevenue;
    private String monthlyGrowth;
    private Integer ordersThisMonth;
    private String ordersGrowth;
    private Integer activeCustomers;
    private String customersGrowth;
    private String averageOrderValue;
    private String aovGrowth;
    private List<CategoryBreakdownDto> categoryBreakdown;
}
