package com.rora.backend.catalog.controller;

import com.rora.backend.catalog.dto.CategoryDto;
import com.rora.backend.catalog.service.CategoryService;
import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Public Category browsing and metadata endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all product categories", description = "Returns active luxury bag categories with product counts and hero images")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{slugOrId}")
    @Operation(summary = "Get category by slug or ID", description = "Returns single category detail by identifier")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategory(@PathVariable String slugOrId) {
        CategoryDto category = categoryService.getCategoryBySlugOrId(slugOrId);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
}
