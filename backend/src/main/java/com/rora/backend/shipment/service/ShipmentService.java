package com.rora.backend.shipment.service;

import com.rora.backend.shipment.dto.*;
import com.rora.backend.shipment.entity.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShipmentService {

    ShipmentDto createShipment(CreateShipmentRequest request);

    ShipmentDto getShipmentById(String id);

    ShipmentDto getShipmentByAwb(String awbNumber);

    List<ShipmentDto> getShipmentsByOrderIdOrNumber(String orderIdOrNumber);

    ShipmentTrackingDto trackShipment(String trackingCodeOrOrderNumber);

    ShipmentDto addShipmentEvent(String shipmentId, UpdateShipmentStatusRequest request);

    ShipmentDto updateShipmentStatus(String shipmentId, ShipmentStatus status, String location, String notes);

    Page<ShipmentDto> searchShipments(String query, ShipmentStatus status, Pageable pageable);

    ShipmentSummaryDto getShipmentSummary();
}
