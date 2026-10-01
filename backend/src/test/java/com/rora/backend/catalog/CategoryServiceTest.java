package com.rora.backend.catalog;

import com.rora.backend.catalog.dto.CategoryDto;
import com.rora.backend.catalog.dto.CategoryRequest;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.service.CategoryService;
import com.rora.backend.common.exception.DuplicateResourceException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id("backpacks")
                .slug("backpacks")
                .name("Backpacks")
                .headline("Everyday & Travel Backpacks")
                .description("Crafted for modern explorers")
                .heroImage("https://example.com/backpacks.jpg")
                .productCount(5)
                .build();
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(mockCategory));

        List<CategoryDto> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("backpacks", result.get(0).getSlug());
        assertEquals("Backpacks", result.get(0).getName());
    }

    @Test
    void testGetCategoryBySlug_Success() {
        when(categoryRepository.findBySlug("backpacks")).thenReturn(Optional.of(mockCategory));

        CategoryDto result = categoryService.getCategoryBySlugOrId("backpacks");

        assertNotNull(result);
        assertEquals("backpacks", result.getSlug());
        assertEquals(5, result.getCount());
    }

    @Test
    void testGetCategoryBySlug_NotFound() {
        when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());
        when(categoryRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryBySlugOrId("unknown"));
    }

    @Test
    void testCreateCategory_Success() {
        CategoryRequest request = CategoryRequest.builder()
                .slug("tote-bags")
                .name("Tote Bags")
                .headline("Market Totes")
                .build();

        when(categoryRepository.existsBySlug("tote-bags")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId("tote-bags");
            return c;
        });

        CategoryDto result = categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals("tote-bags", result.getSlug());
        assertEquals("Tote Bags", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_DuplicateSlug() {
        CategoryRequest request = CategoryRequest.builder()
                .slug("backpacks")
                .name("Backpacks")
                .build();

        when(categoryRepository.existsBySlug("backpacks")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).save(any(Category.class));
    }
}
