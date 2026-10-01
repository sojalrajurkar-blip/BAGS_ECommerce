package com.rora.backend.catalog.repository;

import com.rora.backend.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByIsFeaturedTrue();

    List<Product> findByIsBestSellerTrue();

    List<Product> findByIsNewArrivalTrue();

    List<Product> findByIsCuratedTrue();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.id <> :productId ORDER BY p.rating DESC")
    List<Product> findRelatedProducts(@Param("categoryId") String categoryId, @Param("productId") String productId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.tagline) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.categoryName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchQuick(@Param("query") String query);
}
