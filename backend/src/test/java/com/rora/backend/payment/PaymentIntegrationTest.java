package com.rora.backend.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.payment.dto.PaymentInitiateRequest;
import com.rora.backend.payment.dto.PaymentProcessRequest;
import com.rora.backend.payment.dto.PaymentRefundRequest;
import com.rora.backend.payment.entity.Payment;
import com.rora.backend.payment.entity.PaymentMethod;
import com.rora.backend.payment.entity.PaymentStatus;
import com.rora.backend.payment.entity.SimulationAction;
import com.rora.backend.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        AddressDto address = AddressDto.builder()
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();

        testOrder = orderRepository.findByOrderNumber("#RRA89241")
                .orElseGet(() -> orderRepository.save(Order.builder()
                        .id("RRA-89241")
                        .orderNumber("#RRA89241")
                        .customerName("Sarah Johnson")
                        .customerEmail("admin@rora-luxury.com")
                        .status("Delivered")
                        .subtotal(BigDecimal.valueOf(8798.00))
                        .total(BigDecimal.valueOf(8798.00))
                        .paymentStatus("Captured")
                        .shippingAddress(address)
                        .items(new ArrayList<>())
                        .timelineEvents(new ArrayList<>())
                        .build()));

        testPayment = paymentRepository.findByOrderNumber("#RRA89241")
                .orElseGet(() -> paymentRepository.save(Payment.builder()
                        .id("pay-89241")
                        .order(testOrder)
                        .orderNumber("#RRA89241")
                        .customerEmail("admin@rora-luxury.com")
                        .amount(BigDecimal.valueOf(8798.00))
                        .currency("INR")
                        .paymentMethod(PaymentMethod.CARD)
                        .status(PaymentStatus.SUCCESS)
                        .transactions(new ArrayList<>())
                        .build()));
    }

    @Test
    @DisplayName("POST /api/v1/payments/initiate - Initiate payment for order")
    void testInitiatePayment_Success() throws Exception {
        AddressDto address = AddressDto.builder()
                .fullName("Arjun Test")
                .street("88 Koregaon Park")
                .city("Pune")
                .state("MH")
                .postalCode("411001")
                .country("India")
                .build();

        Order newOrder = orderRepository.save(Order.builder()
                .id("order-init-test")
                .orderNumber("#RRA77665")
                .customerName("Arjun Test")
                .customerEmail("arjun@rora-luxury.com")
                .status("Processing")
                .subtotal(BigDecimal.valueOf(3299.00))
                .total(BigDecimal.valueOf(3299.00))
                .paymentStatus("Pending")
                .shippingAddress(address)
                .items(new ArrayList<>())
                .timelineEvents(new ArrayList<>())
                .build());

        PaymentInitiateRequest request = PaymentInitiateRequest.builder()
                .orderIdOrNumber("#RRA77665")
                .amount(BigDecimal.valueOf(3299.00))
                .paymentMethod(PaymentMethod.UPI)
                .build();

        mockMvc.perform(post("/api/v1/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA77665"))
                .andExpect(jsonPath("$.data.status").value("INITIATED"));
    }

    @Test
    @DisplayName("POST /api/v1/payments/process - Simulate payment with mock provider")
    void testProcessPayment_Success() throws Exception {
        Payment initiatedPayment = paymentRepository.save(Payment.builder()
                .id("pay-to-process")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerEmail("admin@rora-luxury.com")
                .amount(BigDecimal.valueOf(8798.00))
                .currency("INR")
                .paymentMethod(PaymentMethod.UPI)
                .status(PaymentStatus.INITIATED)
                .transactions(new ArrayList<>())
                .build());

        PaymentProcessRequest request = PaymentProcessRequest.builder()
                .paymentId("pay-to-process")
                .simulationAction(SimulationAction.FORCE_SUCCESS)
                .upiVpa("sarah@okaxis")
                .build();

        mockMvc.perform(post("/api/v1/payments/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.transactions", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/payments/{paymentId} - Get payment details by ID")
    void testGetPaymentById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/payments/" + testPayment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testPayment.getId()))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA89241"));
    }

    @Test
    @DisplayName("GET /api/v1/payments/order/{orderIdOrNumber} - Get payment by order number")
    void testGetPaymentByOrderNumber_Success() throws Exception {
        mockMvc.perform(get("/api/v1/payments/order/RRA89241"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA89241"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/payments/summary - Admin gets payment KPI summary")
    void testGetSummary_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/payments/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPayments").isNumber())
                .andExpect(jsonPath("$.data.successfulPayments").isNumber());
    }

    @Test
    @WithMockUser(username = "customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/admin/payments/summary - Customer role gets 403 Forbidden")
    void testGetSummary_Customer_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/payments/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/payments - Admin searches payment transactions")
    void testSearchPayments_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/payments")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/payments/{paymentId}/refund - Admin issues refund")
    void testProcessRefund_Success() throws Exception {
        PaymentRefundRequest req = PaymentRefundRequest.builder()
                .paymentId(testPayment.getId())
                .amount(BigDecimal.valueOf(8798.00))
                .reason("Luxury customer return")
                .build();

        mockMvc.perform(post("/api/v1/admin/payments/" + testPayment.getId() + "/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));
    }
}
