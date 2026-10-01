package com.rora.backend.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ProductCreateRequest {

    @NotBlank(message = "Product slug is required")
    private String slug;

    @NotBlank(message = "Product name is required")
    private String name;

    private String subtitle;
    private String tagline;

    @NotBlank(message = "Category ID or slug is required")
    private String categoryId;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
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
