package com.rora.backend.shipment.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.shipment.dto.*;
import com.rora.backend.shipment.entity.ShipmentStatus;
import com.rora.backend.shipment.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/shipments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ORDER_MANAGER')")
@Tag(name = "Admin Shipments", description = "Admin consignment dispatch, carrier management, and shipping events")
public class AdminShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/summary")
    @Operation(summary = "Get admin shipping KPI metrics summary")
    public ResponseEntity<ApiResponse<ShipmentSummaryDto>> getSummary() {
        ShipmentSummaryDto summary = shipmentService.getShipmentSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping
    @Operation(summary = "Search and filter shipments with pagination")
    public ResponseEntity<ApiResponse<Page<ShipmentDto>>> searchShipments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShipmentDto> results = shipmentService.searchShipments(query, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment consignment details by ID")
    public ResponseEntity<ApiResponse<ShipmentDto>> getShipmentById(@PathVariable String id) {
        ShipmentDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment));
    }

    @PostMapping
    @Operation(summary = "Create and dispatch new shipment consignment for an order")
    public ResponseEntity<ApiResponse<ShipmentDto>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request
    ) {
        ShipmentDto created = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Shipment consignment created successfully", created));
    }

    @PostMapping("/{id}/events")
    @Operation(summary = "Add milestone event and update consignment tracking status")
    public ResponseEntity<ApiResponse<ShipmentDto>> addEvent(
            @PathVariable String id,
            @Valid @RequestBody UpdateShipmentStatusRequest request
    ) {
        ShipmentDto updated = shipmentService.addShipmentEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Shipment event recorded successfully", updated));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Quick update shipment status")
    public ResponseEntity<ApiResponse<ShipmentDto>> updateStatus(
            @PathVariable String id,
            @RequestParam ShipmentStatus status,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String notes
    ) {
        ShipmentDto updated = shipmentService.updateShipmentStatus(id, status, location, notes);
        return ResponseEntity.ok(ApiResponse.success("Shipment status updated successfully", updated));
    }
}
