package com.rora.backend.inventory.entity;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(name = "sku", length = 128, nullable = false, unique = true)
    private String sku;

    @Column(name = "quantity_available", nullable = false)
    @Builder.Default
    private int quantityAvailable = 0;

    @Column(name = "quantity_reserved", nullable = false)
    @Builder.Default
    private int quantityReserved = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private int lowStockThreshold = 5;

    @Column(name = "warehouse_location", length = 128)
    @Builder.Default
    private String warehouseLocation = "Main Atelier Vault, Mumbai";

    @Column(name = "bin_location", length = 64)
    @Builder.Default
    private String binLocation = "A-01-01";

    public boolean isOutOfStock() {
        return quantityAvailable <= 0;
    }

    public boolean isLowStock() {
        return quantityAvailable > 0 && quantityAvailable <= lowStockThreshold;
    }

    public StockStatus getStatus() {
        if (isOutOfStock()) {
            return StockStatus.OUT_OF_STOCK;
        } else if (isLowStock()) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
    }

    public int getTotalStock() {
        return quantityAvailable + quantityReserved;
    }
}
