package com.rora.backend.inventory.dto;

import com.rora.backend.inventory.entity.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementDto {
    private Long id;
    private String inventoryId;
    private String sku;
    private String productId;
    private String productName;
    private MovementType movementType;
    private int quantityChange;
    private int previousQuantity;
    private int newQuantity;
    private String reason;
    private String referenceId;
    private String batchNumber;
    private String createdBy;
    private Instant createdAt;
}
