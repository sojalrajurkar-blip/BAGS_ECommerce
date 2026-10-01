package com.rora.backend.catalog.controller;

import com.rora.backend.catalog.dto.ProductDetailDto;
import com.rora.backend.catalog.dto.ProductDto;
import com.rora.backend.catalog.dto.ProductFilterParams;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Public Product catalog, search, filter and detail endpoints")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get filtered & paginated product catalog", description = "Query products by category, price, stock, ratings, material, color, and sort criteria")
    public ResponseEntity<ApiResponse<PagedResponse<ProductDto>>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean isNewArrival,
            @RequestParam(required = false) Boolean isBestSeller,
            @RequestParam(required = false) Boolean isCurated,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String color,
            @RequestParam(required = false, defaultValue = "popularity") String sortBy,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        ProductFilterParams filter = ProductFilterParams.builder()
                .category(category)
                .search(search)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .isNewArrival(isNewArrival)
                .isBestSeller(isBestSeller)
                .isCurated(isCurated)
                .isFeatured(isFeatured)
                .minRating(minRating)
                .material(material)
                .color(color)
                .sortBy(sortBy)
                .page(page)
                .limit(limit)
                .build();

        PagedResponse<ProductDto> result = productService.getFilteredProducts(filter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Returns curated featured luxury items")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getFeaturedProducts() {
        List<ProductDto> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "Get best seller products", description = "Returns top performing best sellers")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getBestSellers() {
        List<ProductDto> products = productService.getBestSellers();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/new-arrivals")
    @Operation(summary = "Get new arrival products", description = "Returns newest luxury arrivals")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getNewArrivals() {
        List<ProductDto> products = productService.getNewArrivals();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/curated")
    @Operation(summary = "Get curated products", description = "Returns editorially curated picks")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getCurated() {
        List<ProductDto> products = productService.getCuratedProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/search")
    @Operation(summary = "Quick autocomplete search", description = "Fast query for search bar dropdowns")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchProducts(@RequestParam("q") String query) {
        List<ProductDto> results = productService.searchQuick(query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/{idOrSlug}")
    @Operation(summary = "Get detailed product PDP", description = "Returns complete product details with color variants, images, specifications, features, and story")
    public ResponseEntity<ApiResponse<ProductDetailDto>> getProduct(@PathVariable String idOrSlug) {
        ProductDetailDto product = productService.getProductByIdOrSlug(idOrSlug);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/{idOrSlug}/related")
    @Operation(summary = "Get related products", description = "Returns products in the same category or related styling")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getRelatedProducts(@PathVariable String idOrSlug) {
        List<ProductDto> related = productService.getRelatedProducts(idOrSlug);
        return ResponseEntity.ok(ApiResponse.success(related));
    }
}
