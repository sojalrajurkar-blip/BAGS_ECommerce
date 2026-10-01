package com.rora.backend.inventory.dto;

import com.rora.backend.inventory.entity.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {

    @NotBlank(message = "SKU or inventory ID is required")
    @Schema(description = "Product Variant SKU or Inventory ID", example = "RRA-NMD-01-OLV")
    private String sku;

    @NotNull(message = "Quantity change cannot be null")
    @Schema(description = "Quantity change (positive to add, negative to deduct)", example = "10")
    private Integer quantityChange;

    @NotNull(message = "Movement type is required")
    @Schema(description = "Type of stock movement", example = "RESTOCK")
    private MovementType movementType;

    @Schema(description = "Operational justification / note", example = "Direct consignment receipt from Porto atelier")
    private String reason;

    @Schema(description = "Purchase Order or Shipment reference ID", example = "PO-2026-PORTO-91")
    private String referenceId;

    @Schema(description = "Batch or Lot number", example = "BATCH-2026-Q1-91")
    private String batchNumber;
}
