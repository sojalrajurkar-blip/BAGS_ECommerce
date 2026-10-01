package com.rora.backend.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    private String id;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String name;

    @NotBlank(message = "Color name is required")
    private String colorName;

    @NotBlank(message = "Color hex code is required")
    private String colorHex;

    @NotBlank(message = "Variant image URL is required")
    private String image;

    @Builder.Default
    private int stock = 0;

    private BigDecimal priceOverride;
}
