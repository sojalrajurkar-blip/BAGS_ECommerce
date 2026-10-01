package com.rora.backend.shipment.service.impl;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.order.dto.OrderItemDto;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.entity.OrderItem;
import com.rora.backend.order.entity.OrderTimelineEvent;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.shipment.dto.*;
import com.rora.backend.shipment.entity.Shipment;
import com.rora.backend.shipment.entity.ShipmentEvent;
import com.rora.backend.shipment.entity.ShipmentStatus;
import com.rora.backend.shipment.repository.ShipmentEventRepository;
import com.rora.backend.shipment.repository.ShipmentRepository;
import com.rora.backend.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final OrderRepository orderRepository;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.of("Asia/Kolkata"));

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a").withZone(ZoneId.of("Asia/Kolkata"));

    @Override
    @Transactional
    public ShipmentDto createShipment(CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderIdOrNumber());

        Order order = findOrder(request.getOrderIdOrNumber());

        String courier = request.getCourier() != null && !request.getCourier().trim().isEmpty()
                ? request.getCourier().trim()
                : "Bluedart Express";

        String awb = request.getAwbNumber() != null && !request.getAwbNumber().trim().isEmpty()
                ? request.getAwbNumber().trim().toUpperCase()
                : generateAwbNumber(courier);

        // Check if AWB is already taken
        if (shipmentRepository.findByAwbNumberIgnoreCase(awb).isPresent()) {
            throw new BadRequestException("Shipment with AWB number already exists: " + awb);
        }

        String origin = request.getOrigin() != null && !request.getOrigin().trim().isEmpty()
                ? request.getOrigin().trim()
                : "Mumbai Central Studio";

        String destination = request.getDestination();
        if ((destination == null || destination.trim().isEmpty()) && order.getShippingAddress() != null) {
            destination = order.getShippingAddress().getCity() != null
                    ? order.getShippingAddress().getCity() + ", " + order.getShippingAddress().getState()
                    : order.getShippingAddress().getStreet();
        }
        if (destination == null || destination.trim().isEmpty()) {
            destination = "India";
        }

        String estimated = request.getEstimatedDelivery() != null && !request.getEstimatedDelivery().trim().isEmpty()
                ? request.getEstimatedDelivery()
                : "Expected in 2-4 Business Days";

        Shipment shipment = Shipment.builder()
                .order(order)
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .courier(courier)
                .awbNumber(awb)
                .origin(origin)
                .destination(destination)
                .status(ShipmentStatus.IN_TRANSIT)
                .dispatchDate(Instant.now())
                .estimatedDelivery(estimated)
                .trackingUrl("https://track.rora-luxury.com/shipment/" + awb)
                .notes(request.getNotes())
                .build();

        // Add initial Manifested and Dispatched events
        ShipmentEvent manifestEvent = ShipmentEvent.builder()
                .status(ShipmentStatus.MANIFESTED)
                .location(origin)
                .activity("Consignment packed with tamper-evident seal and manifest generated at " + origin)
                .eventTimestamp(Instant.now())
                .build();
        shipment.addEvent(manifestEvent);

        Shipment savedShipment = shipmentRepository.save(shipment);

        // Update Order Carrier & Tracking info
        order.setCarrier(courier);
        order.setTrackingNumber(awb);
        order.setEstimatedDelivery(estimated);
        order.setStatus("Shipped");

        // Add Order Timeline Event
        OrderTimelineEvent timelineEvent = OrderTimelineEvent.builder()
                .order(order)
                .stepName("Dispatched from Atelier")
                .title("Dispatched from Atelier")
                .description("Consignment handed over to " + courier + " (Tracking AWB: " + awb + ")")
                .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                .completed(true)
                .displayOrder(3)
                .build();
        order.addTimelineEvent(timelineEvent);
        orderRepository.save(order);

        log.info("Successfully created shipment ID: {} with AWB: {} for order: {}", savedShipment.getId(), awb, order.getOrderNumber());
        return mapToDto(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentById(String id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + id));
        return mapToDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentDto getShipmentByAwb(String awbNumber) {
        Shipment shipment = shipmentRepository.findByAwbNumberIgnoreCase(awbNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with AWB: " + awbNumber));
        return mapToDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentDto> getShipmentsByOrderIdOrNumber(String orderIdOrNumber) {
        String clean = orderIdOrNumber.trim();
        List<Shipment> shipments = shipmentRepository.findByOrderNumber(clean);
        if (shipments.isEmpty() && !clean.startsWith("#")) {
            shipments = shipmentRepository.findByOrderNumber("#" + clean);
        }
        if (shipments.isEmpty()) {
            shipments = shipmentRepository.findByOrderId(clean);
        }
        return shipments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentTrackingDto trackShipment(String trackingCodeOrOrderNumber) {
        String clean = trackingCodeOrOrderNumber.trim();

        // 1. Try finding by AWB number
        Optional<Shipment> shipmentOpt = shipmentRepository.findByAwbNumberIgnoreCase(clean);

        // 2. Try finding by Order number or Order ID
        if (shipmentOpt.isEmpty()) {
            List<Shipment> list = shipmentRepository.findByOrderNumber(clean);
            if (list.isEmpty() && !clean.startsWith("#")) {
                list = shipmentRepository.findByOrderNumber("#" + clean);
            }
            if (list.isEmpty()) {
                list = shipmentRepository.findByOrderId(clean);
            }
            if (!list.isEmpty()) {
                shipmentOpt = Optional.of(list.get(0));
            }
        }

        if (shipmentOpt.isPresent()) {
            Shipment shipment = shipmentOpt.get();
            return mapToTrackingDto(shipment);
        }

        // 3. If no explicit Shipment record exists yet, check if Order exists and synthesize a tracking view
        Order order = orderRepository.findByOrderNumber(clean)
                .or(() -> orderRepository.findByOrderNumber("#" + clean))
                .or(() -> orderRepository.findById(clean))
                .orElseThrow(() -> new ResourceNotFoundException("No shipment or order found matching tracking reference: " + trackingCodeOrOrderNumber));

        return synthesizeTrackingFromOrder(order);
    }

    @Override
    @Transactional
    public ShipmentDto addShipmentEvent(String shipmentId, UpdateShipmentStatusRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        ShipmentStatus newStatus = request.getStatus();
        shipment.setStatus(newStatus);

        ShipmentEvent event = ShipmentEvent.builder()
                .status(newStatus)
                .location(request.getLocation() != null ? request.getLocation().trim() : shipment.getOrigin())
                .activity(request.getActivity())
                .notes(request.getNotes())
                .eventTimestamp(Instant.now())
                .build();
        shipment.addEvent(event);

        Order order = shipment.getOrder();

        if (newStatus == ShipmentStatus.DELIVERED) {
            shipment.setActualDeliveryDate(Instant.now());
            if (order != null) {
                order.setStatus("Delivered");
                order.addTimelineEvent(OrderTimelineEvent.builder()
                        .order(order)
                        .stepName("Delivered")
                        .title("Delivered")
                        .description("Package securely delivered at destination. Signed by recipient.")
                        .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                        .completed(true)
                        .displayOrder(5)
                        .build());
                orderRepository.save(order);
            }
        } else if (newStatus == ShipmentStatus.OUT_FOR_DELIVERY) {
            if (order != null) {
                order.setStatus("Out for Delivery");
                order.addTimelineEvent(OrderTimelineEvent.builder()
                        .order(order)
                        .stepName("Out for Delivery")
                        .title("Out for Delivery")
                        .description("Package is out for delivery with local courier executive in " + (request.getLocation() != null ? request.getLocation() : shipment.getDestination()))
                        .eventTime(DATE_TIME_FORMATTER.format(Instant.now()))
                        .completed(true)
                        .displayOrder(4)
                        .build());
                orderRepository.save(order);
            }
        } else if (newStatus == ShipmentStatus.IN_TRANSIT) {
            if (order != null && !"Shipped".equalsIgnoreCase(order.getStatus()) && !"In Transit".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("In Transit");
                orderRepository.save(order);
            }
        }

        Shipment updated = shipmentRepository.save(shipment);
        log.info("Added shipment event: {} [{}] for shipment AWB: {}", newStatus, request.getActivity(), shipment.getAwbNumber());
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public ShipmentDto updateShipmentStatus(String shipmentId, ShipmentStatus status, String location, String notes) {
        UpdateShipmentStatusRequest req = UpdateShipmentStatusRequest.builder()
                .status(status)
                .location(location)
                .activity("Shipment status updated to " + status.getDisplayName() + (location != null ? " at " + location : ""))
                .notes(notes)
                .build();
        return addShipmentEvent(shipmentId, req);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipmentDto> searchShipments(String query, ShipmentStatus status, Pageable pageable) {
        String cleanQuery = query != null ? query.trim() : null;
        Page<Shipment> page = shipmentRepository.searchShipments(cleanQuery, status, pageable);
        return page.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentSummaryDto getShipmentSummary() {
        long total = shipmentRepository.count();
        long pending = shipmentRepository.countByStatus(ShipmentStatus.CREATED) + shipmentRepository.countByStatus(ShipmentStatus.MANIFESTED);
        long inTransit = shipmentRepository.countByStatus(ShipmentStatus.IN_TRANSIT) + shipmentRepository.countByStatus(ShipmentStatus.PICKED_UP);
        long outForDelivery = shipmentRepository.countByStatus(ShipmentStatus.OUT_FOR_DELIVERY);
        long delivered = shipmentRepository.countByStatus(ShipmentStatus.DELIVERED);
        long exceptions = shipmentRepository.countByStatus(ShipmentStatus.FAILED_DELIVERY) + shipmentRepository.countByStatus(ShipmentStatus.RETURNED_TO_ORIGIN);

        return ShipmentSummaryDto.builder()
                .totalShipments(total)
                .pendingDispatch(pending)
                .inTransit(inTransit)
                .outForDelivery(outForDelivery)
                .delivered(delivered)
                .deliveryExceptions(exceptions)
                .build();
    }

    private Order findOrder(String orderIdOrNumber) {
        String clean = orderIdOrNumber.trim();
        return orderRepository.findById(clean)
                .or(() -> orderRepository.findByOrderNumber(clean))
                .or(() -> orderRepository.findByOrderNumber("#" + clean))
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with identifier: " + orderIdOrNumber));
    }

    private String generateAwbNumber(String courier) {
        String prefix = "RRA";
        if (courier.toLowerCase().contains("blue")) {
            prefix = "BLU";
        } else if (courier.toLowerCase().contains("delhi")) {
            prefix = "DEL";
        } else if (courier.toLowerCase().contains("dtdc")) {
            prefix = "DTD";
        } else if (courier.toLowerCase().contains("dhl")) {
            prefix = "DHL";
        }
        int randomNum = 10000000 + new Random().nextInt(90000000);
        return prefix + "-" + randomNum;
    }

    private ShipmentDto mapToDto(Shipment shipment) {
        List<ShipmentEventDto> eventDtos = shipment.getEvents() != null
                ? shipment.getEvents().stream().map(this::mapEventToDto).collect(Collectors.toList())
                : new ArrayList<>();

        return ShipmentDto.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder() != null ? shipment.getOrder().getId() : null)
                .orderNumber(shipment.getOrderNumber())
                .customer(shipment.getCustomerName())
                .customerName(shipment.getCustomerName())
                .customerEmail(shipment.getCustomerEmail())
                .customerPhone(shipment.getCustomerPhone())
                .courier(shipment.getCourier())
                .awbNumber(shipment.getAwbNumber())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .status(shipment.getStatus())
                .statusDisplay(shipment.getStatus() != null ? shipment.getStatus().getDisplayName() : "Created")
                .dispatchDate(shipment.getDispatchDate())
                .formattedDispatchDate(shipment.getDispatchDate() != null ? DATE_FORMATTER.format(shipment.getDispatchDate()) : null)
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .actualDeliveryDate(shipment.getActualDeliveryDate())
                .formattedActualDeliveryDate(shipment.getActualDeliveryDate() != null ? DATE_FORMATTER.format(shipment.getActualDeliveryDate()) : null)
                .trackingUrl(shipment.getTrackingUrl())
                .notes(shipment.getNotes())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .events(eventDtos)
                .build();
    }

    private ShipmentEventDto mapEventToDto(ShipmentEvent event) {
        return ShipmentEventDto.builder()
                .id(event.getId())
                .status(event.getStatus())
                .statusDisplay(event.getStatus() != null ? event.getStatus().getDisplayName() : "")
                .location(event.getLocation())
                .activity(event.getActivity())
                .eventTimestamp(event.getEventTimestamp())
                .formattedTimestamp(event.getEventTimestamp() != null ? DATE_TIME_FORMATTER.format(event.getEventTimestamp()) : "")
                .notes(event.getNotes())
                .build();
    }

    private ShipmentTrackingDto mapToTrackingDto(Shipment shipment) {
        List<ShipmentEventDto> eventDtos = shipment.getEvents() != null
                ? shipment.getEvents().stream().map(this::mapEventToDto).collect(Collectors.toList())
                : new ArrayList<>();

        List<OrderItemDto> itemDtos = new ArrayList<>();
        if (shipment.getOrder() != null && shipment.getOrder().getItems() != null) {
            itemDtos = shipment.getOrder().getItems().stream()
                    .map(this::mapOrderItemToDto)
                    .collect(Collectors.toList());
        }

        return ShipmentTrackingDto.builder()
                .awbNumber(shipment.getAwbNumber())
                .orderNumber(shipment.getOrderNumber())
                .customerName(shipment.getCustomerName())
                .courier(shipment.getCourier())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .status(shipment.getStatus())
                .statusDisplay(shipment.getStatus() != null ? shipment.getStatus().getDisplayName() : "Created")
                .dispatchDate(shipment.getDispatchDate())
                .formattedDispatchDate(shipment.getDispatchDate() != null ? DATE_FORMATTER.format(shipment.getDispatchDate()) : null)
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .actualDeliveryDate(shipment.getActualDeliveryDate())
                .formattedActualDeliveryDate(shipment.getActualDeliveryDate() != null ? DATE_FORMATTER.format(shipment.getActualDeliveryDate()) : null)
                .trackingUrl(shipment.getTrackingUrl())
                .events(eventDtos)
                .items(itemDtos)
                .build();
    }

    private ShipmentTrackingDto synthesizeTrackingFromOrder(Order order) {
        List<ShipmentEventDto> eventDtos = new ArrayList<>();
        if (order.getTimelineEvents() != null) {
            for (OrderTimelineEvent te : order.getTimelineEvents()) {
                eventDtos.add(ShipmentEventDto.builder()
                        .status(ShipmentStatus.fromString(te.getTitle()))
                        .statusDisplay(te.getTitle() != null ? te.getTitle() : te.getStepName())
                        .location("Atelier Hub")
                        .activity(te.getDescription())
                        .eventTimestamp(te.getCreatedAt())
                        .formattedTimestamp(te.getEventTime() != null ? te.getEventTime() : (te.getCreatedAt() != null ? DATE_TIME_FORMATTER.format(te.getCreatedAt()) : ""))
                        .build());
            }
        }

        List<OrderItemDto> itemDtos = order.getItems() != null
                ? order.getItems().stream().map(this::mapOrderItemToDto).collect(Collectors.toList())
                : new ArrayList<>();

        String dest = order.getShippingAddress() != null
                ? order.getShippingAddress().getCity() + ", " + order.getShippingAddress().getState()
                : "India";

        return ShipmentTrackingDto.builder()
                .awbNumber(order.getTrackingNumber() != null ? order.getTrackingNumber() : "PENDING")
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .courier(order.getCarrier() != null ? order.getCarrier() : "Luxury White-Glove Dispatch")
                .origin("Mumbai Central Studio")
                .destination(dest)
                .status(ShipmentStatus.fromString(order.getStatus()))
                .statusDisplay(order.getStatus())
                .dispatchDate(order.getCreatedAt())
                .formattedDispatchDate(order.getCreatedAt() != null ? DATE_FORMATTER.format(order.getCreatedAt()) : null)
                .estimatedDelivery(order.getEstimatedDelivery())
                .trackingUrl(order.getTrackingNumber() != null ? "https://track.rora-luxury.com/shipment/" + order.getTrackingNumber() : null)
                .events(eventDtos)
                .items(itemDtos)
                .build();
    }

    private OrderItemDto mapOrderItemToDto(OrderItem item) {
        return OrderItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .productName(item.getProductName())
                .colorName(item.getColorName())
                .imageUrl(item.getImageUrl())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
