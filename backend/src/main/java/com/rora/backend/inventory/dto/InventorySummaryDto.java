package com.rora.backend.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryDto {
    private long totalSkus;
    private long totalAvailableUnits;
    private long totalReservedUnits;
    private long totalUnits;
    private long lowStockAlertCount;
    private long outOfStockCount;
    private long inStockCount;
    private List<InventoryMovementDto> recentMovements;
}
