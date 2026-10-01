package com.rora.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDto {

    private String id;
    private String sku;
    private String name;
    private String colorName;
    private String colorHex;
    private String image;
    private int stock;
    private BigDecimal priceOverride;

    // Helper getters for frontend color object compatibility
    public String getHex() {
        return colorHex;
    }
}
