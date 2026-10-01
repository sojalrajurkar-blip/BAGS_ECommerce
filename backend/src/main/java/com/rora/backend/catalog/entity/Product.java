package com.rora.backend.catalog.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rora.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(name = "slug", length = 255, nullable = false, unique = true)
    private String slug;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Column(name = "subtitle", length = 255)
    private String subtitle;

    @Column(name = "tagline", columnDefinition = "TEXT")
    private String tagline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "category_name", length = 128)
    private String categoryName;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "original_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal originalPrice;

    @Column(name = "compare_at_price", precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(name = "discount", nullable = false)
    @Builder.Default
    private int discount = 0;

    @Column(name = "currency", length = 16, nullable = false)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "rating", precision = 3, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "badge", length = 64)
    private String badge;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private int stock = 0;

    @Column(name = "in_stock", nullable = false)
    @Builder.Default
    private boolean inStock = true;

    @Column(name = "is_new_arrival", nullable = false)
    @Builder.Default
    private boolean isNewArrival = false;

    @Column(name = "is_best_seller", nullable = false)
    @Builder.Default
    private boolean isBestSeller = false;

    @Column(name = "is_curated", nullable = false)
    @Builder.Default
    private boolean isCurated = false;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "story", columnDefinition = "TEXT")
    private String story;

    @Column(name = "material", length = 255)
    private String material;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "specifications", columnDefinition = "jsonb")
    private Map<String, String> specifications;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "care_instructions", columnDefinition = "jsonb")
    private List<String> careInstructions;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb")
    private List<String> features;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(name = "dimensions", length = 255)
    private String dimensions;

    @Column(name = "weight", length = 64)
    private String weight;

    @Column(name = "capacity", length = 64)
    private String capacity;

    @Column(name = "sku", length = 128)
    private String sku;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
}
