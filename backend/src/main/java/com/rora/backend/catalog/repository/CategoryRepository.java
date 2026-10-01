package com.rora.backend.catalog.repository;

import com.rora.backend.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
