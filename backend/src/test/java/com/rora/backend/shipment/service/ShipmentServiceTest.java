package com.rora.backend.shipment.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.dto.AddressDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.shipment.dto.*;
import com.rora.backend.shipment.entity.Shipment;
import com.rora.backend.shipment.entity.ShipmentEvent;
import com.rora.backend.shipment.entity.ShipmentStatus;
import com.rora.backend.shipment.repository.ShipmentEventRepository;
import com.rora.backend.shipment.repository.ShipmentRepository;
import com.rora.backend.shipment.service.impl.ShipmentServiceImpl;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentEventRepository shipmentEventRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    private Order testOrder;
    private Shipment testShipment;

    @BeforeEach
    void setUp() {
        AddressDto address = AddressDto.builder()
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("MH")
                .postalCode("400050")
                .country("India")
                .fullName("Sarah Johnson")
                .phone("+91 98200 12345")
                .build();

        testOrder = Order.builder()
                .id("order-100")
                .orderNumber("#RRA89241")
                .customerId("cust-1")
                .customerName("Sarah Johnson")
                .customerEmail("sarah@example.com")
                .customerPhone("+91 98200 12345")
                .shippingAddress(address)
                .status("Processing")
                .timelineEvents(new ArrayList<>())
                .items(new ArrayList<>())
                .build();

        testShipment = Shipment.builder()
                .id("ship-100")
                .order(testOrder)
                .orderNumber("#RRA89241")
                .customerId("cust-1")
                .customerName("Sarah Johnson")
                .customerEmail("sarah@example.com")
                .customerPhone("+91 98200 12345")
                .courier("Bluedart Express")
                .awbNumber("BLU-88239014")
                .origin("Mumbai Central Studio")
                .destination("Mumbai, MH")
                .status(ShipmentStatus.IN_TRANSIT)
                .dispatchDate(Instant.now())
                .estimatedDelivery("May 01, 2026")
                .events(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("createShipment - Successfully create and dispatch consignment")
    void testCreateShipment_Success() {
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderIdOrNumber("#RRA89241")
                .courier("Bluedart Express")
                .awbNumber("BLU-88239014")
                .origin("Mumbai Central Studio")
                .destination("Mumbai, MH")
                .estimatedDelivery("May 01, 2026")
                .build();

        when(orderRepository.findById("#RRA89241")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("#RRA89241")).thenReturn(Optional.of(testOrder));
        when(shipmentRepository.findByAwbNumberIgnoreCase("BLU-88239014")).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment s = invocation.getArgument(0);
            s.setId("ship-new");
            return s;
        });

        ShipmentDto result = shipmentService.createShipment(request);

        assertThat(result).isNotNull();
        assertThat(result.getAwbNumber()).isEqualTo("BLU-88239014");
        assertThat(result.getCourier()).isEqualTo("Bluedart Express");
        assertThat(result.getOrderNumber()).isEqualTo("#RRA89241");
        assertThat(testOrder.getStatus()).isEqualTo("Shipped");
        assertThat(testOrder.getCarrier()).isEqualTo("Bluedart Express");
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("createShipment - Order Not Found throws ResourceNotFoundException")
    void testCreateShipment_OrderNotFound() {
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderIdOrNumber("INVALID-ORDER")
                .courier("Bluedart Express")
                .build();

        when(orderRepository.findById("INVALID-ORDER")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("INVALID-ORDER")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("#INVALID-ORDER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shipmentService.createShipment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    @DisplayName("createShipment - Duplicate AWB throws BadRequestException")
    void testCreateShipment_DuplicateAwb() {
        CreateShipmentRequest request = CreateShipmentRequest.builder()
                .orderIdOrNumber("#RRA89241")
                .courier("Bluedart Express")
                .awbNumber("BLU-88239014")
                .build();

        when(orderRepository.findById("#RRA89241")).thenReturn(Optional.empty());
        when(orderRepository.findByOrderNumber("#RRA89241")).thenReturn(Optional.of(testOrder));
        when(shipmentRepository.findByAwbNumberIgnoreCase("BLU-88239014")).thenReturn(Optional.of(testShipment));

        assertThatThrownBy(() -> shipmentService.createShipment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Shipment with AWB number already exists");
    }

    @Test
    @DisplayName("getShipmentById - Return shipment by consignment ID")
    void testGetShipmentById_Success() {
        when(shipmentRepository.findById("ship-100")).thenReturn(Optional.of(testShipment));

        ShipmentDto result = shipmentService.getShipmentById("ship-100");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("ship-100");
        assertThat(result.getAwbNumber()).isEqualTo("BLU-88239014");
    }

    @Test
    @DisplayName("trackShipment - Found active shipment by AWB")
    void testTrackShipment_Success() {
        when(shipmentRepository.findByAwbNumberIgnoreCase("BLU-88239014")).thenReturn(Optional.of(testShipment));

        ShipmentTrackingDto tracking = shipmentService.trackShipment("BLU-88239014");

        assertThat(tracking).isNotNull();
        assertThat(tracking.getAwbNumber()).isEqualTo("BLU-88239014");
        assertThat(tracking.getOrderNumber()).isEqualTo("#RRA89241");
    }

    @Test
    @DisplayName("addShipmentEvent - Milestone event updates shipment and order status to DELIVERED")
    void testAddShipmentEvent_Delivered() {
        UpdateShipmentStatusRequest request = UpdateShipmentStatusRequest.builder()
                .status(ShipmentStatus.DELIVERED)
                .location("Bandra West, Mumbai")
                .activity("Delivered to recipient - Signature verified")
                .notes("Signed by Sarah")
                .build();

        when(shipmentRepository.findById("ship-100")).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(i -> i.getArgument(0));

        ShipmentDto result = shipmentService.addShipmentEvent("ship-100", request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(result.getActualDeliveryDate()).isNotNull();
        assertThat(testOrder.getStatus()).isEqualTo("Delivered");
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("getShipmentSummary - Returns accurate KPI metrics")
    void testGetShipmentSummary() {
        when(shipmentRepository.count()).thenReturn(10L);
        when(shipmentRepository.countByStatus(ShipmentStatus.CREATED)).thenReturn(1L);
        when(shipmentRepository.countByStatus(ShipmentStatus.MANIFESTED)).thenReturn(1L);
        when(shipmentRepository.countByStatus(ShipmentStatus.IN_TRANSIT)).thenReturn(3L);
        when(shipmentRepository.countByStatus(ShipmentStatus.PICKED_UP)).thenReturn(1L);
        when(shipmentRepository.countByStatus(ShipmentStatus.OUT_FOR_DELIVERY)).thenReturn(2L);
        when(shipmentRepository.countByStatus(ShipmentStatus.DELIVERED)).thenReturn(2L);
        when(shipmentRepository.countByStatus(ShipmentStatus.FAILED_DELIVERY)).thenReturn(0L);
        when(shipmentRepository.countByStatus(ShipmentStatus.RETURNED_TO_ORIGIN)).thenReturn(0L);

        ShipmentSummaryDto summary = shipmentService.getShipmentSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.getTotalShipments()).isEqualTo(10L);
        assertThat(summary.getPendingDispatch()).isEqualTo(2L);
        assertThat(summary.getInTransit()).isEqualTo(4L);
        assertThat(summary.getOutForDelivery()).isEqualTo(2L);
        assertThat(summary.getDelivered()).isEqualTo(2L);
        assertThat(summary.getDeliveryExceptions()).isEqualTo(0L);
    }
}
