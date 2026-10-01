package com.rora.backend.shipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.shipment.dto.CreateShipmentRequest;
import com.rora.backend.shipment.dto.UpdateShipmentStatusRequest;
import com.rora.backend.shipment.entity.Shipment;
import com.rora.backend.shipment.entity.ShipmentEvent;
import com.rora.backend.shipment.entity.ShipmentStatus;
import com.rora.backend.shipment.repository.ShipmentEventRepository;
import com.rora.backend.shipment.repository.ShipmentRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ShipmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentEventRepository shipmentEventRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Order testOrder;
    private Shipment testShipment;

    @BeforeEach
    void setUp() {
        shipmentEventRepository.deleteAll();
        shipmentRepository.deleteAll();

        AddressDto address = AddressDto.builder()
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .phone("+91 98200 12345")
                .build();

        testOrder = Order.builder()
                .id("test-ship-order-1")
                .orderNumber("#RRA89241")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .status("Processing")
                .subtotal(BigDecimal.valueOf(8798.00))
                .total(BigDecimal.valueOf(8798.00))
                .shippingAddress(address)
                .items(new ArrayList<>())
                .timelineEvents(new ArrayList<>())
                .build();
        testOrder = orderRepository.save(testOrder);

        testShipment = Shipment.builder()
                .id("ship-test-441")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerName("Sarah Johnson")
                .customerEmail("admin@rora-luxury.com")
                .customerPhone("+91 98200 12345")
                .courier("Bluedart Express")
                .awbNumber("BLU-88239014")
                .origin("Mumbai Central Studio")
                .destination("Bandra West, Mumbai")
                .status(ShipmentStatus.IN_TRANSIT)
                .dispatchDate(Instant.now())
                .estimatedDelivery("May 01, 2026")
                .trackingUrl("https://track.rora-luxury.com/shipment/BLU-88239014")
                .events(new ArrayList<>())
                .build();

        ShipmentEvent event = ShipmentEvent.builder()
                .shipment(testShipment)
                .status(ShipmentStatus.MANIFESTED)
                .location("Mumbai Central Studio")
                .activity("Manifest generated at atelier")
                .eventTimestamp(Instant.now())
                .build();
        testShipment.addEvent(event);

        testShipment = shipmentRepository.save(testShipment);
    }

    @Test
    @DisplayName("GET /api/v1/shipments/track/{trackingCode} - Public package tracking by AWB")
    void testTrackShipmentByAwb_Success() throws Exception {
        mockMvc.perform(get("/api/v1/shipments/track/BLU-88239014"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.awbNumber").value("BLU-88239014"))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA89241"))
                .andExpect(jsonPath("$.data.courier").value("Bluedart Express"))
                .andExpect(jsonPath("$.data.events", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/shipments/track/{orderNumber} - Public package tracking by Order Number")
    void testTrackShipmentByOrderNumber_Success() throws Exception {
        mockMvc.perform(get("/api/v1/shipments/track/RRA89241"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("#RRA89241"))
                .andExpect(jsonPath("$.data.awbNumber").value("BLU-88239014"));
    }

    @Test
    @DisplayName("GET /api/v1/shipments/{id} - Get shipment details by ID")
    void testGetShipmentById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/shipments/" + testShipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testShipment.getId()))
                .andExpect(jsonPath("$.data.customer").value("Sarah Johnson"));
    }

    @Test
    @DisplayName("GET /api/v1/shipments/order/{orderNumber} - Get shipment by order reference")
    void testGetShipmentsByOrder_Success() throws Exception {
        mockMvc.perform(get("/api/v1/shipments/order/RRA89241"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/shipments/summary - Admin gets shipping KPI summary")
    void testGetSummary_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/shipments/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalShipments").isNumber());
    }

    @Test
    @WithMockUser(username = "customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/admin/shipments/summary - Customer role gets 403 Forbidden")
    void testGetSummary_Customer_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/shipments/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/shipments - Admin creates new shipment consignment")
    void testCreateShipment_Admin_Success() throws Exception {
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderIdOrNumber(testOrder.getId())
                .courier("Delhivery Express")
                .awbNumber("DEL-77889900")
                .origin("Mumbai Central Studio")
                .destination("Pune, MH")
                .estimatedDelivery("May 05, 2026")
                .notes("Handle with luxury white-glove care")
                .build();

        mockMvc.perform(post("/api/v1/admin/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.awbNumber").value("DEL-77889900"))
                .andExpect(jsonPath("$.data.courier").value("Delhivery Express"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/shipments/{id}/events - Admin records tracking milestone event")
    void testAddShipmentEvent_Admin_Success() throws Exception {
        UpdateShipmentStatusRequest request = UpdateShipmentStatusRequest.builder()
                .status(ShipmentStatus.DELIVERED)
                .location("Bandra West, Mumbai")
                .activity("Delivered to customer - Verified Signature")
                .notes("Signed by Sarah Johnson")
                .build();

        mockMvc.perform(post("/api/v1/admin/shipments/" + testShipment.getId() + "/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
    }
}
