package com.rora.backend.catalog.controller;

import com.rora.backend.catalog.dto.CategoryDto;
import com.rora.backend.catalog.dto.CategoryRequest;
import com.rora.backend.catalog.service.CategoryService;
import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Categories", description = "Admin CRUD operations for Product Categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new product category")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryDto created = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update existing category details")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryDto updated = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete category by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
