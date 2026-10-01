package com.rora.backend.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchStockAdjustmentRequest {

    @NotEmpty(message = "Adjustments list cannot be empty")
    @Valid
    @Schema(description = "List of stock adjustments to apply in a single transaction")
    private List<StockAdjustmentRequest> adjustments;

    @Schema(description = "Global batch justification note", example = "Quarterly comprehensive physical audit reconciliation")
    private String globalReason;
}
