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
public class ProductFilterParams {

    private String category;
    private String search;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Boolean isNewArrival;
    private Boolean isBestSeller;
    private Boolean isCurated;
    private Boolean isFeatured;
    private BigDecimal minRating;
    private String material;
    private String color;
    private String sortBy; // price_asc, price_desc, rating_desc, newest, popularity
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int limit = 20;
}
