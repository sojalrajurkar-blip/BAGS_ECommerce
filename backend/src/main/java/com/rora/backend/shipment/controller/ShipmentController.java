package com.rora.backend.shipment.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.shipment.dto.ShipmentDto;
import com.rora.backend.shipment.dto.ShipmentTrackingDto;
import com.rora.backend.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Tag(name = "Shipment & Tracking", description = "Public & Customer real-time package tracking and consignment lookup APIs")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/track/{trackingCodeOrOrderNumber}")
    @Operation(summary = "Track shipment live by AWB tracking number or order number")
    public ResponseEntity<ApiResponse<ShipmentTrackingDto>> trackShipment(
            @PathVariable String trackingCodeOrOrderNumber
    ) {
        ShipmentTrackingDto tracking = shipmentService.trackShipment(trackingCodeOrOrderNumber);
        return ResponseEntity.ok(ApiResponse.success(tracking));
    }

    @GetMapping("/order/{orderIdOrNumber}")
    @Operation(summary = "Get all shipments associated with an order")
    public ResponseEntity<ApiResponse<List<ShipmentDto>>> getShipmentsByOrder(
            @PathVariable String orderIdOrNumber
    ) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsByOrderIdOrNumber(orderIdOrNumber);
        return ResponseEntity.ok(ApiResponse.success(shipments));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment details by consignment ID")
    public ResponseEntity<ApiResponse<ShipmentDto>> getShipmentById(@PathVariable String id) {
        ShipmentDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @GetMapping("/awb/{awbNumber}")
    @Operation(summary = "Get shipment details by AWB tracking code")
    public ResponseEntity<ApiResponse<ShipmentDto>> getShipmentByAwb(@PathVariable String awbNumber) {
        ShipmentDto shipment = shipmentService.getShipmentByAwb(awbNumber);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }
}
