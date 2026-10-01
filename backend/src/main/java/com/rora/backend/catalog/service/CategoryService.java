package com.rora.backend.catalog.service;

import com.rora.backend.catalog.dto.CategoryDto;
import com.rora.backend.catalog.dto.CategoryRequest;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.common.exception.DuplicateResourceException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryBySlugOrId(String slugOrId) {
        Category category = categoryRepository.findBySlug(slugOrId)
                .or(() -> categoryRepository.findById(slugOrId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with identifier: " + slugOrId));
        return mapToDto(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntity(String slugOrId) {
        return categoryRepository.findBySlug(slugOrId)
                .or(() -> categoryRepository.findById(slugOrId))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with identifier: " + slugOrId));
    }

    @Transactional
    public CategoryDto createCategory(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category slug already exists: " + request.getSlug());
        }

        Category category = Category.builder()
                .slug(request.getSlug())
                .name(request.getName())
                .title(request.getTitle() != null ? request.getTitle() : request.getName())
                .headline(request.getHeadline())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .image(request.getImage())
                .heroImage(request.getHeroImage())
                .productCount(0)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created new category: {} ({})", saved.getName(), saved.getSlug());
        return mapToDto(saved);
    }

    @Transactional
    public CategoryDto updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getSlug().equalsIgnoreCase(request.getSlug()) && categoryRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Category slug already exists: " + request.getSlug());
        }

        category.setSlug(request.getSlug());
        category.setName(request.getName());
        category.setTitle(request.getTitle() != null ? request.getTitle() : request.getName());
        category.setHeadline(request.getHeadline());
        category.setSubtitle(request.getSubtitle());
        category.setDescription(request.getDescription());
        if (request.getImage() != null) category.setImage(request.getImage());
        if (request.getHeroImage() != null) category.setHeroImage(request.getHeroImage());

        Category updated = categoryRepository.save(category);
        log.info("Updated category: {}", updated.getSlug());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
        log.info("Deleted category id: {}", id);
    }

    public CategoryDto mapToDto(Category category) {
        if (category == null) return null;
        return CategoryDto.builder()
                .id(category.getId())
                .slug(category.getSlug())
                .name(category.getName())
                .title(category.getTitle())
                .headline(category.getHeadline())
                .subtitle(category.getSubtitle())
                .description(category.getDescription())
                .image(category.getImage())
                .heroImage(category.getHeroImage())
                .count(category.getProductCount())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
