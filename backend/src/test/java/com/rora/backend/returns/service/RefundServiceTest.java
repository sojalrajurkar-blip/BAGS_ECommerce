package com.rora.backend.returns.service;

import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.entity.Payment;
import com.rora.backend.payment.entity.PaymentMethod;
import com.rora.backend.payment.entity.PaymentStatus;
import com.rora.backend.payment.repository.PaymentRepository;
import com.rora.backend.payment.repository.PaymentTransactionRepository;
import com.rora.backend.returns.dto.CreateRefundRequest;
import com.rora.backend.returns.dto.RefundRecordDto;
import com.rora.backend.returns.entity.RefundRecord;
import com.rora.backend.returns.entity.RefundStatus;
import com.rora.backend.returns.entity.ReturnRequest;
import com.rora.backend.returns.repository.RefundRepository;
import com.rora.backend.returns.repository.ReturnRepository;
import com.rora.backend.returns.service.impl.RefundServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private ReturnRepository returnRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @InjectMocks
    private RefundServiceImpl refundService;

    private Order testOrder;
    private ReturnRequest testReturn;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id("order-100")
                .orderNumber("#RRA89241")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .total(BigDecimal.valueOf(5499.00))
                .status("Delivered")
                .paymentStatus("Captured")
                .build();

        testReturn = ReturnRequest.builder()
                .id("ret-100")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .amount(BigDecimal.valueOf(5499.00))
                .build();

        testPayment = Payment.builder()
                .id("pay-100")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerEmail("admin@rora-luxury.com")
                .amount(BigDecimal.valueOf(5499.00))
                .paymentMethod(PaymentMethod.CARD)
                .status(PaymentStatus.SUCCESS)
                .transactions(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("createRefund - Successfully creates refund and links to payment")
    void testCreateRefund_Success() {
        CreateRefundRequest request = CreateRefundRequest.builder()
                .returnId("ret-100")
                .amount(BigDecimal.valueOf(5499.00))
                .method("Original Payment Source")
                .reason("Return approved")
                .build();

        when(returnRepository.findById("ret-100")).thenReturn(Optional.of(testReturn));
        when(refundRepository.findByReturnRequestId("ret-100")).thenReturn(Optional.empty());
        when(paymentRepository.findByOrderId("order-100")).thenReturn(Optional.of(testPayment));
        when(refundRepository.save(any(RefundRecord.class))).thenAnswer(i -> {
            RefundRecord r = i.getArgument(0);
            r.setId("ref-new");
            return r;
        });

        RefundRecordDto result = refundService.createRefund(request, "admin@rora-luxury.com");

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5499.00));
        assertThat(result.getStatusCode()).isEqualTo(RefundStatus.COMPLETED);
        assertThat(testPayment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(testOrder.getPaymentStatus()).isEqualTo("Refunded");
        verify(paymentTransactionRepository).save(any());
        verify(orderRepository).save(testOrder);
    }
}
