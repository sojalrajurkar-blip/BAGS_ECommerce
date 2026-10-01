package com.rora.backend.inventory.service;

import com.rora.backend.inventory.dto.*;
import com.rora.backend.inventory.entity.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    InventorySummaryDto getInventorySummary();

    Page<InventoryDto> searchInventory(String search, String status, String categoryId, Pageable pageable);

    Page<InventoryDto> getLowStockAlerts(Pageable pageable);

    InventoryDto getInventoryByIdOrSku(String idOrSku);

    InventoryDto adjustStock(StockAdjustmentRequest request, String actor);

    List<InventoryDto> batchAdjustStock(BatchStockAdjustmentRequest request, String actor);

    InventoryDto updateThreshold(String idOrSku, UpdateThresholdRequest request);

    Page<InventoryMovementDto> getMovements(String sku, MovementType movementType, Pageable pageable);

    Page<InventoryMovementDto> getMovementsForInventory(String idOrSku, Pageable pageable);

    void processSale(String sku, int quantity, String orderId, String customerEmail);

    void processOrderCancellation(String sku, int quantity, String orderId, String actor);

    void processReturn(String sku, int quantity, String orderId, String reason, String actor);

    void reserveStock(String sku, int quantity, String orderId);

    void releaseReservation(String sku, int quantity, String orderId);
}
