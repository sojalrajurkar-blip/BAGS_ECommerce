package com.rora.backend.catalog.repository;

import com.rora.backend.catalog.dto.ProductFilterParams;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withFilters(ProductFilterParams filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            // Ensure distinct when joining
            query.distinct(true);

            // 1. Category Filter (by slug or id)
            if (filter.getCategory() != null && !filter.getCategory().trim().isEmpty()) {
                String cat = filter.getCategory().trim().toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("category").get("slug")), cat),
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("category").get("id")), cat),
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("categoryName")), cat)
                ));
            }

            // 2. Search Keyword
            if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
                String keyword = "%" + filter.getSearch().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("tagline")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("subtitle")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("material")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("categoryName")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("sku")), keyword)
                ));
            }

            // 3. Price Range
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            // 4. In Stock
            if (filter.getInStock() != null) {
                predicates.add(criteriaBuilder.equal(root.get("inStock"), filter.getInStock()));
            }

            // 5. Badges / Flags
            if (filter.getIsNewArrival() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isNewArrival"), filter.getIsNewArrival()));
            }
            if (filter.getIsBestSeller() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isBestSeller"), filter.getIsBestSeller()));
            }
            if (filter.getIsCurated() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCurated"), filter.getIsCurated()));
            }
            if (filter.getIsFeatured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isFeatured"), filter.getIsFeatured()));
            }

            // 6. Minimum Rating
            if (filter.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), filter.getMinRating()));
            }

            // 7. Material
            if (filter.getMaterial() != null && !filter.getMaterial().trim().isEmpty()) {
                String mat = "%" + filter.getMaterial().trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("material")), mat));
            }

            // 8. Color Filter (join with variants)
            if (filter.getColor() != null && !filter.getColor().trim().isEmpty()) {
                Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.LEFT);
                String col = filter.getColor().trim().toLowerCase();
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(criteriaBuilder.lower(variantJoin.get("colorName")), col),
                        criteriaBuilder.equal(criteriaBuilder.lower(variantJoin.get("colorHex")), col)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
