package com.rora.backend.payment.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.dto.*;
import com.rora.backend.payment.entity.*;
import com.rora.backend.payment.repository.PaymentRepository;
import com.rora.backend.payment.repository.PaymentTransactionRepository;
import com.rora.backend.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private MockPaymentProvider mockPaymentProvider = new MockPaymentProvider();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id("order-100")
                .orderNumber("#RRA99001")
                .customerEmail("sarah@rora-luxury.com")
                .customerName("Sarah Johnson")
                .paymentStatus("Pending")
                .total(BigDecimal.valueOf(4899.00))
                .timelineEvents(new ArrayList<>(List.of(
                        OrderTimelineEvent.builder().stepName("Payment Verified").completed(false).build()
                )))
                .build();

        testPayment = Payment.builder()
                .id("pay-100")
                .order(testOrder)
                .orderNumber("#RRA99001")
                .customerEmail("sarah@rora-luxury.com")
                .amount(BigDecimal.valueOf(4899.00))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .status(PaymentStatus.INITIATED)
                .transactions(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Initiate payment creates a new Payment in INITIATED status")
    void testInitiatePayment_Success() {
        PaymentInitiateRequest req = PaymentInitiateRequest.builder()
                .orderIdOrNumber("#RRA99001")
                .amount(BigDecimal.valueOf(4899.00))
                .paymentMethod(PaymentMethod.UPI)
                .idempotencyKey("idem-12345")
                .build();

        when(orderRepository.findByIdOrOrderNumber("#RRA99001")).thenReturn(Optional.of(testOrder));
        when(paymentRepository.findByIdempotencyKey("idem-12345")).thenReturn(Optional.empty());
        when(paymentRepository.findByOrderId("order-100")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId("pay-new");
            return p;
        });

        PaymentDto result = paymentService.initiatePayment(req, "sarah@rora-luxury.com");

        assertNotNull(result);
        assertEquals("pay-new", result.getId());
        assertEquals("#RRA99001", result.getOrderNumber());
        assertEquals(PaymentStatus.INITIATED, result.getStatus());
        assertEquals(0, BigDecimal.valueOf(4899.00).compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("Initiate payment returns idempotent result when idempotency key exists")
    void testInitiatePayment_IdempotentKey() {
        PaymentInitiateRequest req = PaymentInitiateRequest.builder()
                .orderIdOrNumber("#RRA99001")
                .amount(BigDecimal.valueOf(4899.00))
                .paymentMethod(PaymentMethod.UPI)
                .idempotencyKey("idem-12345")
                .build();

        when(orderRepository.findByIdOrOrderNumber("#RRA99001")).thenReturn(Optional.of(testOrder));
        when(paymentRepository.findByIdempotencyKey("idem-12345")).thenReturn(Optional.of(testPayment));

        PaymentDto result = paymentService.initiatePayment(req, "sarah@rora-luxury.com");

        assertNotNull(result);
        assertEquals("pay-100", result.getId());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Process payment with FORCE_SUCCESS updates Payment and Order to SUCCESS / Captured")
    void testProcessPayment_ForceSuccess() {
        PaymentProcessRequest req = PaymentProcessRequest.builder()
                .paymentId("pay-100")
                .simulationAction(SimulationAction.FORCE_SUCCESS)
                .upiVpa("sarah@okaxis")
                .build();

        when(paymentRepository.findById("pay-100")).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDto result = paymentService.processPayment(req);

        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(1, result.getTransactions().size());
        assertEquals("Captured", testOrder.getPaymentStatus());
        assertTrue(testOrder.getTimelineEvents().get(0).isCompleted());
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Process payment with FORCE_FAILURE records failure reason and updates Order")
    void testProcessPayment_ForceFailure() {
        PaymentProcessRequest req = PaymentProcessRequest.builder()
                .paymentId("pay-100")
                .simulationAction(SimulationAction.FORCE_FAILURE)
                .build();

        when(paymentRepository.findById("pay-100")).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDto result = paymentService.processPayment(req);

        assertNotNull(result);
        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertNotNull(result.getFailureReason());
        assertEquals("Failed", testOrder.getPaymentStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Process refund creates REFUND transaction and transitions payment to REFUNDED")
    void testProcessRefund_FullRefund_Success() {
        testPayment.setStatus(PaymentStatus.SUCCESS);

        PaymentRefundRequest req = PaymentRefundRequest.builder()
                .paymentId("pay-100")
                .amount(BigDecimal.valueOf(4899.00))
                .reason("Pre-dispatch cancellation")
                .build();

        when(paymentRepository.findById("pay-100")).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDto result = paymentService.processRefund(req, "admin@rora-luxury.com");

        assertNotNull(result);
        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        assertEquals(1, result.getTransactions().size());
        assertEquals(TransactionType.REFUND, result.getTransactions().get(0).getTransactionType());
        assertEquals("Refunded", testOrder.getPaymentStatus());
    }

    @Test
    @DisplayName("Process refund throws BadRequestException when amount exceeds original payment")
    void testProcessRefund_ExcessAmount_ThrowsBadRequest() {
        testPayment.setStatus(PaymentStatus.SUCCESS);

        PaymentRefundRequest req = PaymentRefundRequest.builder()
                .paymentId("pay-100")
                .amount(BigDecimal.valueOf(9999.00))
                .reason("Too much refund")
                .build();

        when(paymentRepository.findById("pay-100")).thenReturn(Optional.of(testPayment));

        assertThrows(BadRequestException.class, () -> paymentService.processRefund(req, "admin@rora-luxury.com"));
    }

    @Test
    @DisplayName("Get payment summary returns aggregated KPIs")
    void testGetPaymentSummary() {
        when(paymentRepository.count()).thenReturn(10L);
        when(paymentRepository.countByStatus(PaymentStatus.SUCCESS)).thenReturn(8L);
        when(paymentRepository.countByStatus(PaymentStatus.PENDING)).thenReturn(1L);
        when(paymentRepository.countByStatus(PaymentStatus.INITIATED)).thenReturn(0L);
        when(paymentRepository.countByStatus(PaymentStatus.FAILED)).thenReturn(1L);
        when(paymentRepository.countByStatus(PaymentStatus.CANCELLED)).thenReturn(0L);
        when(paymentRepository.countByStatus(PaymentStatus.REFUNDED)).thenReturn(0L);
        when(paymentRepository.countByStatus(PaymentStatus.PARTIALLY_REFUNDED)).thenReturn(0L);
        when(paymentRepository.sumSuccessfulVolume()).thenReturn(BigDecimal.valueOf(45000.00));
        when(paymentRepository.sumRefundedVolume()).thenReturn(BigDecimal.ZERO);
        when(paymentTransactionRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of());

        PaymentSummaryDto summary = paymentService.getPaymentSummary();

        assertNotNull(summary);
        assertEquals(10L, summary.getTotalPayments());
        assertEquals(8L, summary.getSuccessfulPayments());
        assertEquals(0, BigDecimal.valueOf(45000.00).compareTo(summary.getTotalRevenue()));
    }

    @Test
    @DisplayName("Search payments admin returns paginated list")
    void testSearchPaymentsAdmin() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> page = new PageImpl<>(List.of(testPayment), pageable, 1);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<PaymentDto> result = paymentService.searchPaymentsAdmin("RRA", PaymentStatus.INITIATED, PaymentMethod.UPI, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("pay-100", result.getContent().get(0).getId());
    }
}
