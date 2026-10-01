package com.rora.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private String slug;
    private String name;
    private String subtitle;
    private String tagline;
    private String categoryId;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal compareAtPrice;
    private Integer discount;
    private String currency;
    private BigDecimal rating;
    private Integer reviewCount;
    private String badge;
    private Integer stock;
    private Boolean inStock;
    private Boolean isNewArrival;
    private Boolean isBestSeller;
    private Boolean isCurated;
    private Boolean isFeatured;
    private String description;
    private String story;
    private String material;
    private String dimensions;
    private String weight;
    private String capacity;
    private String sku;

    private Map<String, String> specifications;
    private List<String> careInstructions;
    private List<String> features;
    private List<String> tags;

    private List<ProductVariantRequest> variants;
    private List<String> images;
}
