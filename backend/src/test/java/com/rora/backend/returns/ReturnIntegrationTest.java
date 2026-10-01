package com.rora.backend.returns;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.returns.dto.ApproveReturnRequest;
import com.rora.backend.returns.dto.CreateRefundRequest;
import com.rora.backend.returns.dto.CreateReturnRequest;
import com.rora.backend.returns.dto.RejectReturnRequest;
import com.rora.backend.returns.entity.InspectionStatus;
import com.rora.backend.returns.entity.RefundRecord;
import com.rora.backend.returns.entity.ReturnItem;
import com.rora.backend.returns.entity.ReturnRequest;
import com.rora.backend.returns.entity.ReturnStatus;
import com.rora.backend.returns.repository.RefundRepository;
import com.rora.backend.returns.repository.ReturnItemRepository;
import com.rora.backend.returns.repository.ReturnRepository;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ReturnIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;
    private ReturnRequest testReturn;

    @BeforeEach
    void setUp() {
        refundRepository.deleteAll();
        returnItemRepository.deleteAll();
        returnRepository.deleteAll();

        AddressDto address = AddressDto.builder()
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();

        OrderItem item = OrderItem.builder()
                .id("oi-test-100")
                .productName("The Executive Briefcase")
                .colorName("Chestnut Brown")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(5499.00))
                .totalPrice(BigDecimal.valueOf(5499.00))
                .build();

        testOrder = Order.builder()
                .id("order-return-test-1")
                .orderNumber("#RRA88612")
                .customerId("cust-admin")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .status("Delivered")
                .subtotal(BigDecimal.valueOf(5499.00))
                .total(BigDecimal.valueOf(5499.00))
                .shippingAddress(address)
                .items(new ArrayList<>())
                .timelineEvents(new ArrayList<>())
                .build();
        testOrder.addItem(item);
        testOrder = orderRepository.save(testOrder);

        testReturn = ReturnRequest.builder()
                .id("ret-test-104")
                .order(testOrder)
                .orderNumber("#RRA88612")
                .customerId("cust-admin")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .item("The Executive Briefcase (Chestnut Brown)")
                .reason("Size / Laptop fit requirement changed")
                .customerNotes("Requesting refund.")
                .status(ReturnStatus.UNDER_REVIEW)
                .inspectionStatus(InspectionStatus.AWAITING_HUB_DELIVERY)
                .amount(BigDecimal.valueOf(5499.00))
                .requestDate(Instant.now())
                .items(new ArrayList<>())
                .build();

        ReturnItem ri = ReturnItem.builder()
                .returnRequest(testReturn)
                .productName("The Executive Briefcase")
                .colorName("Chestnut Brown")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(5499.00))
                .totalPrice(BigDecimal.valueOf(5499.00))
                .build();
        testReturn.addItem(ri);

        testReturn = returnRepository.save(testReturn);
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("POST /api/v1/returns - Customer submits return request")
    void testCreateReturnRequest_Success() throws Exception {
        CreateReturnRequest request = CreateReturnRequest.builder()
                .orderIdOrNumber(testOrder.getId())
                .reason("Color shade did not match expectation")
                .customerNotes("Prefers Obsidian Black")
                .build();

        mockMvc.perform(post("/api/v1/returns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA88612"))
                .andExpect(jsonPath("$.data.amount").value(5499.00));
    }

    @Test
    @DisplayName("GET /api/v1/returns/{id} - Get return details by ID")
    void testGetReturnById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/returns/" + testReturn.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testReturn.getId()))
                .andExpect(jsonPath("$.data.customer").value("Sarah Johnson"));
    }

    @Test
    @DisplayName("GET /api/v1/returns/order/{orderNumber} - Get return by order reference")
    void testGetReturnsByOrder_Success() throws Exception {
        mockMvc.perform(get("/api/v1/returns/order/RRA88612"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/returns/summary - Admin gets returns KPI summary")
    void testGetSummary_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/returns/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalReturns").isNumber())
                .andExpect(jsonPath("$.data.underReview").isNumber());
    }

    @Test
    @WithMockUser(username = "customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/admin/returns/summary - Customer role gets 403 Forbidden")
    void testGetSummary_Customer_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/returns/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/returns/{id}/approve - Admin approves return with auto-refund")
    void testApproveReturn_Success() throws Exception {
        ApproveReturnRequest request = ApproveReturnRequest.builder()
                .inspectionStatus(InspectionStatus.PASSED_PRISTINE)
                .notes("Item pristine and all seals verified.")
                .autoRefund(true)
                .build();

        mockMvc.perform(post("/api/v1/admin/returns/" + testReturn.getId() + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("Approved & Refunded"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/returns/{id}/reject - Admin rejects return")
    void testRejectReturn_Success() throws Exception {
        RejectReturnRequest request = RejectReturnRequest.builder()
                .reason("Leather damaged with deep scratches, failing policy guidelines.")
                .inspectionStatus(InspectionStatus.REJECTED_DAMAGED)
                .build();

        mockMvc.perform(post("/api/v1/admin/returns/" + testReturn.getId() + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("Rejected"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/refunds - Admin executes manual refund")
    void testCreateManualRefund_Success() throws Exception {
        CreateRefundRequest request = CreateRefundRequest.builder()
                .orderIdOrNumber(testOrder.getId())
                .amount(BigDecimal.valueOf(1000.00))
                .method("UPI / PhonePe")
                .reason("Concierge goodwill discount refund")
                .build();

        mockMvc.perform(post("/api/v1/admin/refunds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(1000.00))
                .andExpect(jsonPath("$.data.status").value("Completed"));
    }
}
