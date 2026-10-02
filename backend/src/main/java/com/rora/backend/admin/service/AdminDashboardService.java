package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.AdminDashboardSummaryDto;
import com.rora.backend.admin.dto.SalesOverviewDto;

public interface AdminDashboardService {

    SalesOverviewDto getSalesOverview();

    AdminDashboardSummaryDto getDashboardSummary();
}
