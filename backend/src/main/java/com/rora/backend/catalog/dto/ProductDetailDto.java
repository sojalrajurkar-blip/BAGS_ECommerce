package com.rora.backend.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDto {

    private String id;
    private String slug;
    private String name;
    private String subtitle;
    private String tagline;
    private String category; // Category slug
    private String categoryId;
    private String categoryName;
    private CategoryDto categoryDetails;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal compareAtPrice;
    private int discount;
    private String currency;
    private BigDecimal rating;
    private int reviewCount;
    private String badge;
    private int stock;
    private boolean inStock;
    private boolean isNewArrival;
    private boolean isBestSeller;
    private boolean isCurated;
    private boolean isFeatured;
    private String description;
    private String story;
    private String material;
    private String dimensions;
    private String weight;
    private String capacity;
    private String size;
    private String sku;
    private String image; // Primary image
    private Map<String, String> specifications;
    private List<String> careInstructions;
    private List<String> features;
    private List<String> tags;
    private List<ProductVariantDto> colors;
    private List<ProductVariantDto> variants;
    private List<String> images;
    private Instant createdAt;
    private Instant updatedAt;
}
