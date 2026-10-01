package com.rora.backend.inventory.dto;

import com.rora.backend.inventory.entity.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {
    private String id;
    private String productId;
    private String productName;
    private String productSlug;
    private String productImage;
    private String categoryName;
    private String variantId;
    private String variantName;
    private String colorName;
    private String colorHex;
    private String sku;
    private int quantityAvailable;
    private int quantityReserved;
    private int totalStock;
    private int lowStockThreshold;
    private StockStatus status;
    private String warehouseLocation;
    private String binLocation;
    private Instant createdAt;
    private Instant updatedAt;
}
