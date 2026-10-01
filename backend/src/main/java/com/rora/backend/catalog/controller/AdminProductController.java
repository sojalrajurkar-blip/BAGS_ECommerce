package com.rora.backend.catalog.controller;

import com.rora.backend.catalog.dto.*;
import com.rora.backend.catalog.service.ProductService;
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
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Products", description = "Admin CRUD operations for Products and Variants")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new catalog product with variants and specifications")
    public ResponseEntity<ApiResponse<ProductDetailDto>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductDetailDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update existing catalog product")
    public ResponseEntity<ApiResponse<ProductDetailDto>> updateProduct(
            @PathVariable String id,
            @RequestBody ProductUpdateRequest request
    ) {
        ProductDetailDto updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete product by ID")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    @PostMapping("/{id}/variants")
    @Operation(summary = "Add variant", description = "Add a new color/size variant to product")
    public ResponseEntity<ApiResponse<ProductVariantDto>> addVariant(
            @PathVariable String id,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        ProductVariantDto created = productService.addVariant(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Variant added successfully", created));
    }

    @PutMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Update variant", description = "Update existing product variant")
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateVariant(
            @PathVariable String id,
            @PathVariable String variantId,
            @Valid @RequestBody ProductVariantRequest request
    ) {
        ProductVariantDto updated = productService.updateVariant(id, variantId, request);
        return ResponseEntity.ok(ApiResponse.success("Variant updated successfully", updated));
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    @Operation(summary = "Delete variant", description = "Delete product variant")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable String id,
            @PathVariable String variantId
    ) {
        productService.deleteVariant(id, variantId);
        return ResponseEntity.ok(ApiResponse.success("Variant deleted successfully", null));
    }
}
