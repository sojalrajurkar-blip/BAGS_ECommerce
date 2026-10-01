package com.rora.backend.catalog.repository;

import com.rora.backend.catalog.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(String productId);
}
