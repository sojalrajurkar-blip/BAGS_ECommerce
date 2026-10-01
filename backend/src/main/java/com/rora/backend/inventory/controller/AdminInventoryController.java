package com.rora.backend.inventory.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.inventory.dto.*;
import com.rora.backend.inventory.entity.MovementType;
import com.rora.backend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PRODUCT_MANAGER')")
@Tag(name = "Admin Inventory & Warehousing", description = "Endpoints for inventory tracking, stock movements, low-stock alerts, and warehouse ledger")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/summary")
    @Operation(summary = "Get inventory health KPI metrics and summary")
    public ResponseEntity<ApiResponse<InventorySummaryDto>> getInventorySummary() {
        InventorySummaryDto summary = inventoryService.getInventorySummary();
        return ResponseEntity.ok(ApiResponse.success("Inventory summary retrieved successfully", summary));
    }

    @GetMapping
    @Operation(summary = "Get paginated inventory catalog with search and status filtering")
    public ResponseEntity<ApiResponse<Page<InventoryDto>>> searchInventory(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InventoryDto> result = inventoryService.searchInventory(search, status, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Inventory items retrieved successfully", result));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get active low stock alerts")
    public ResponseEntity<ApiResponse<Page<InventoryDto>>> getLowStockAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("quantityAvailable").ascending());
        Page<InventoryDto> alerts = inventoryService.getLowStockAlerts(pageable);
        return ResponseEntity.ok(ApiResponse.success("Low stock alerts retrieved successfully", alerts));
    }

    @GetMapping("/{idOrSku}")
    @Operation(summary = "Get inventory item details by ID or SKU")
    public ResponseEntity<ApiResponse<InventoryDto>> getInventoryByIdOrSku(@PathVariable String idOrSku) {
        InventoryDto dto = inventoryService.getInventoryByIdOrSku(idOrSku);
        return ResponseEntity.ok(ApiResponse.success("Inventory details retrieved successfully", dto));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust inventory stock for a single SKU")
    public ResponseEntity<ApiResponse<InventoryDto>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String actor = userDetails != null ? userDetails.getUsername() : "admin";
        InventoryDto updated = inventoryService.adjustStock(request, actor);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", updated));
    }

    @PostMapping("/batch-adjust")
    @Operation(summary = "Batch adjust stock for multiple SKUs in a single transaction")
    public ResponseEntity<ApiResponse<List<InventoryDto>>> batchAdjustStock(
            @Valid @RequestBody BatchStockAdjustmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String actor = userDetails != null ? userDetails.getUsername() : "admin";
        List<InventoryDto> updatedList = inventoryService.batchAdjustStock(request, actor);
        return ResponseEntity.ok(ApiResponse.success("Batch stock adjustments applied successfully", updatedList));
    }

    @PutMapping("/{idOrSku}/threshold")
    @Operation(summary = "Update low stock threshold and warehouse bin locations")
    public ResponseEntity<ApiResponse<InventoryDto>> updateThreshold(
            @PathVariable String idOrSku,
            @Valid @RequestBody UpdateThresholdRequest request) {

        InventoryDto updated = inventoryService.updateThreshold(idOrSku, request);
        return ResponseEntity.ok(ApiResponse.success("Inventory thresholds and location updated successfully", updated));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get global stock movement audit ledger")
    public ResponseEntity<ApiResponse<Page<InventoryMovementDto>>> getMovements(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) MovementType movementType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InventoryMovementDto> movements = inventoryService.getMovements(sku, movementType, pageable);
        return ResponseEntity.ok(ApiResponse.success("Stock movements retrieved successfully", movements));
    }

    @GetMapping("/{idOrSku}/movements")
    @Operation(summary = "Get stock movement history for a specific inventory SKU")
    public ResponseEntity<ApiResponse<Page<InventoryMovementDto>>> getMovementsForInventory(
            @PathVariable String idOrSku,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InventoryMovementDto> movements = inventoryService.getMovementsForInventory(idOrSku, pageable);
        return ResponseEntity.ok(ApiResponse.success("Item movement history retrieved successfully", movements));
    }
}
