package com.rora.backend.returns.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.returns.dto.*;
import com.rora.backend.returns.entity.*;
import com.rora.backend.returns.repository.RefundRepository;
import com.rora.backend.returns.repository.ReturnItemRepository;
import com.rora.backend.returns.repository.ReturnRepository;
import com.rora.backend.returns.service.impl.ReturnServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReturnServiceTest {

    @Mock
    private ReturnRepository returnRepository;

    @Mock
    private ReturnItemRepository returnItemRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RefundService refundService;

    @InjectMocks
    private ReturnServiceImpl returnService;

    private Order testOrder;
    private ReturnRequest testReturn;

    @BeforeEach
    void setUp() {
        OrderItem item = OrderItem.builder()
                .id("oi-100")
                .productName("The Executive Briefcase")
                .colorName("Chestnut Brown")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(5499.00))
                .totalPrice(BigDecimal.valueOf(5499.00))
                .build();

        testOrder = Order.builder()
                .id("order-100")
                .orderNumber("#RRA89241")
                .customerId("cust-admin")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .status("Delivered")
                .total(BigDecimal.valueOf(5499.00))
                .items(new ArrayList<>(List.of(item)))
                .timelineEvents(new ArrayList<>())
                .build();

        testReturn = ReturnRequest.builder()
                .id("ret-100")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerId("cust-admin")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .item("The Executive Briefcase (Chestnut Brown)")
                .reason("Size / Laptop fit requirement changed")
                .status(ReturnStatus.UNDER_REVIEW)
                .inspectionStatus(InspectionStatus.AWAITING_HUB_DELIVERY)
                .amount(BigDecimal.valueOf(5499.00))
                .requestDate(Instant.now())
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("createReturnRequest - Successfully submits return request")
    void testCreateReturnRequest_Success() {
        CreateReturnRequest request = CreateReturnRequest.builder()
                .orderIdOrNumber("#RRA89241")
                .reason("Size / Laptop fit requirement changed")
                .customerNotes("Needs a smaller silhouette")
                .build();

        when(orderRepository.findById("#RRA89241")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("#RRA89241")).thenReturn(Optional.of(testOrder));
        when(returnRepository.save(any(ReturnRequest.class))).thenAnswer(i -> {
            ReturnRequest r = i.getArgument(0);
            r.setId("ret-new");
            return r;
        });

        ReturnRecordDto result = returnService.createReturnRequest(request, "admin@rora-luxury.com");

        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("#RRA89241");
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5499.00));
        assertThat(result.getStatusCode()).isEqualTo(ReturnStatus.UNDER_REVIEW);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("createReturnRequest - Rejects return on cancelled order")
    void testCreateReturnRequest_CancelledOrder() {
        testOrder.setStatus("Cancelled");

        CreateReturnRequest request = CreateReturnRequest.builder()
                .orderIdOrNumber("#RRA89241")
                .reason("Defective")
                .build();

        when(orderRepository.findById("#RRA89241")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("#RRA89241")).thenReturn(Optional.of(testOrder));

        assertThatThrownBy(() -> returnService.createReturnRequest(request, "admin@rora-luxury.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot request a return for a cancelled order");
    }

    @Test
    @DisplayName("approveReturn - Successfully approves return and executes auto-refund")
    void testApproveReturn_Success() {
        ApproveReturnRequest request = ApproveReturnRequest.builder()
                .inspectionStatus(InspectionStatus.PASSED_PRISTINE)
                .notes("Pristine leather, verified authentic")
                .autoRefund(true)
                .refundMethod("Original Payment Source")
                .build();

        when(returnRepository.findById("ret-100")).thenReturn(Optional.of(testReturn));
        when(returnRepository.save(any(ReturnRequest.class))).thenAnswer(i -> i.getArgument(0));

        ReturnRecordDto result = returnService.approveReturn("ret-100", request, "admin@rora-luxury.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(ReturnStatus.APPROVED_AND_REFUNDED);
        assertThat(result.getInspectionStatusCode()).isEqualTo(InspectionStatus.PASSED_PRISTINE);
        verify(refundService).createRefund(any(CreateRefundRequest.class), eq("admin@rora-luxury.com"));
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("approveReturn - Throws BadRequestException when return is already refunded")
    void testApproveReturn_InvalidStateTransition() {
        testReturn.setStatus(ReturnStatus.APPROVED_AND_REFUNDED);

        ApproveReturnRequest request = ApproveReturnRequest.builder()
                .autoRefund(true)
                .build();

        when(returnRepository.findById("ret-100")).thenReturn(Optional.of(testReturn));

        assertThatThrownBy(() -> returnService.approveReturn("ret-100", request, "admin@rora-luxury.com"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot approve return in status");
    }

    @Test
    @DisplayName("rejectReturn - Successfully rejects return request")
    void testRejectReturn_Success() {
        RejectReturnRequest request = RejectReturnRequest.builder()
                .reason("Item has significant wear and missing authentication tags")
                .inspectionStatus(InspectionStatus.FAILED_POLICY_CHECK)
                .build();

        when(returnRepository.findById("ret-100")).thenReturn(Optional.of(testReturn));
        when(returnRepository.save(any(ReturnRequest.class))).thenAnswer(i -> i.getArgument(0));

        ReturnRecordDto result = returnService.rejectReturn("ret-100", request, "admin@rora-luxury.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(ReturnStatus.REJECTED);
        assertThat(result.getInspectionStatusCode()).isEqualTo(InspectionStatus.FAILED_POLICY_CHECK);
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("getReturnSummary - Calculates accurate KPI metrics")
    void testGetReturnSummary() {
        when(returnRepository.count()).thenReturn(10L);
        when(returnRepository.countByStatus(ReturnStatus.REQUESTED)).thenReturn(2L);
        when(returnRepository.countByStatus(ReturnStatus.UNDER_REVIEW)).thenReturn(3L);
        when(returnRepository.countByStatus(ReturnStatus.APPROVED)).thenReturn(1L);
        when(returnRepository.countByStatus(ReturnStatus.RECEIVED_AT_HUB)).thenReturn(1L);
        when(returnRepository.countByStatus(ReturnStatus.APPROVED_AND_REFUNDED)).thenReturn(2L);
        when(returnRepository.countByStatus(ReturnStatus.REJECTED)).thenReturn(1L);
        when(returnRepository.countByStatus(ReturnStatus.CANCELLED)).thenReturn(0L);
        when(refundRepository.sumTotalRefundedAmount()).thenReturn(BigDecimal.valueOf(10998.00));

        ReturnSummaryDto summary = returnService.getReturnSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalReturns()).isEqualTo(10L);
        assertThat(summary.getUnderReview()).isEqualTo(3L);
        assertThat(summary.getApprovedAndRefunded()).isEqualTo(2L);
        assertThat(summary.getTotalRefundedAmount()).isEqualByComparingTo(BigDecimal.valueOf(10998.00));
    }
}
