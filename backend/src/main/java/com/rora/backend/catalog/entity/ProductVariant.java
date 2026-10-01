package com.rora.backend.catalog.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rora.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @Column(name = "sku", length = 128, nullable = false, unique = true)
    private String sku;

    @Column(name = "name", length = 128)
    private String name;

    @Column(name = "color_name", length = 128, nullable = false)
    private String colorName;

    @Column(name = "color_hex", length = 32, nullable = false)
    private String colorHex;

    @Column(name = "image", columnDefinition = "TEXT", nullable = false)
    private String image;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "price_override", precision = 12, scale = 2)
    private BigDecimal priceOverride;
}
