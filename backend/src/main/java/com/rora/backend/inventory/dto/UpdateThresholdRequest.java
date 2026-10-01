package com.rora.backend.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateThresholdRequest {

    @Min(value = 0, message = "Low stock threshold cannot be negative")
    @Schema(description = "Minimum stock units threshold before triggering alert", example = "5")
    private Integer lowStockThreshold;

    @Schema(description = "Primary warehouse facility location", example = "South Logistics Hub, Bengaluru")
    private String warehouseLocation;

    @Schema(description = "Specific storage bin / rack aisle identifier", example = "B-04-12")
    private String binLocation;
}
