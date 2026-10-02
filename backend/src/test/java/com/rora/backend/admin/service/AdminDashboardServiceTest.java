package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.AdminDashboardSummaryDto;
import com.rora.backend.admin.dto.SalesOverviewDto;
import com.rora.backend.admin.service.impl.AdminDashboardServiceImpl;
import com.rora.backend.customer.repository.CustomerRepository;
import com.rora.backend.inventory.repository.InventoryRepository;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.returns.entity.ReturnStatus;
import com.rora.backend.returns.repository.ReturnRepository;
import com.rora.backend.review.entity.ReviewStatus;
import com.rora.backend.review.repository.ReviewRepository;
import com.rora.backend.shipment.repository.ShipmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ReturnRepository returnRepository;

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @Test
    @DisplayName("Should return sales overview breakdown")
    void testGetSalesOverview() {
        when(orderRepository.count()).thenReturn(150L);
        when(customerRepository.count()).thenReturn(320L);

        SalesOverviewDto overview = adminDashboardService.getSalesOverview();

        assertThat(overview).isNotNull();
        assertThat(overview.getMonthlyRevenue()).isEqualTo("₹28,45,900");
        assertThat(overview.getCategoryBreakdown()).hasSize(5);
    }

    @Test
    @DisplayName("Should aggregate dashboard summary counts")
    void testGetDashboardSummary() {
        when(orderRepository.count()).thenReturn(100L);
        when(customerRepository.count()).thenReturn(80L);
        when(inventoryRepository.countLowStockItems()).thenReturn(2L);
        when(returnRepository.countByStatus(ReturnStatus.REQUESTED)).thenReturn(2L);
        when(returnRepository.countByStatus(ReturnStatus.UNDER_REVIEW)).thenReturn(1L);
        when(reviewRepository.countByStatus(ReviewStatus.PENDING_MODERATION)).thenReturn(2L);
        when(shipmentRepository.countByStatusIn(anyList())).thenReturn(5L);

        AdminDashboardSummaryDto summary = adminDashboardService.getDashboardSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalOrders()).isEqualTo(100L);
        assertThat(summary.getTotalCustomers()).isEqualTo(80L);
        assertThat(summary.getPendingReturnsCount()).isEqualTo(3L);
        assertThat(summary.getPendingReviewsCount()).isEqualTo(2L);
        assertThat(summary.getActiveShipmentsCount()).isEqualTo(5L);
    }
}
