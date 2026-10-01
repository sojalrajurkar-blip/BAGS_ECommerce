package com.rora.backend.catalog;

import com.rora.backend.catalog.dto.ProductDetailDto;
import com.rora.backend.catalog.dto.ProductDto;
import com.rora.backend.catalog.dto.ProductFilterParams;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductImage;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductImageRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.catalog.service.CategoryService;
import com.rora.backend.catalog.service.ProductService;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product mockProduct;
    private Category mockCategory;

    @BeforeEach
    void setUp() {
        mockCategory = Category.builder()
                .id("backpacks")
                .slug("backpacks")
                .name("Backpacks")
                .build();

        ProductVariant variant1 = ProductVariant.builder()
                .id("var-1")
                .sku("RRA-NMD-01-OLV")
                .name("Olive Green")
                .colorName("Olive Green")
                .colorHex("#555E48")
                .image("https://example.com/olive.jpg")
                .stock(10)
                .build();

        ProductImage img1 = ProductImage.builder()
                .id("img-1")
                .imageUrl("https://example.com/nomad-1.jpg")
                .displayOrder(0)
                .build();

        mockProduct = Product.builder()
                .id("prod-1")
                .slug("the-nomad-backpack")
                .name("The Nomad Backpack")
                .tagline("Adventure-ready. Everyday style.")
                .category(mockCategory)
                .categoryName("Backpacks")
                .price(BigDecimal.valueOf(4899))
                .originalPrice(BigDecimal.valueOf(5499))
                .discount(11)
                .rating(BigDecimal.valueOf(4.8))
                .reviewCount(304)
                .badge("Best Seller")
                .stock(24)
                .inStock(true)
                .isFeatured(true)
                .isBestSeller(true)
                .material("900D Recycled Nylon")
                .sku("RRA-NMD-01")
                .specifications(Map.of("Volume", "20L"))
                .features(List.of("Dual-access zipper", "Padded laptop sleeve"))
                .variants(new ArrayList<>(List.of(variant1)))
                .images(new ArrayList<>(List.of(img1)))
                .build();
    }

    @Test
    void testGetFilteredProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        ProductFilterParams filter = ProductFilterParams.builder()
                .category("backpacks")
                .page(0)
                .limit(10)
                .build();

        PagedResponse<ProductDto> result = productService.getFilteredProducts(filter);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("the-nomad-backpack", result.getContent().get(0).getSlug());
        assertEquals(BigDecimal.valueOf(4899), result.getContent().get(0).getPrice());
    }

    @Test
    void testGetProductByIdOrSlug_Success() {
        when(productRepository.findBySlug("the-nomad-backpack")).thenReturn(Optional.of(mockProduct));

        ProductDetailDto result = productService.getProductByIdOrSlug("the-nomad-backpack");

        assertNotNull(result);
        assertEquals("the-nomad-backpack", result.getSlug());
        assertEquals("The Nomad Backpack", result.getName());
        assertEquals(1, result.getColors().size());
        assertEquals("Olive Green", result.getColors().get(0).getColorName());
    }

    @Test
    void testGetProductByIdOrSlug_NotFound() {
        when(productRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductByIdOrSlug("nonexistent"));
    }

    @Test
    void testGetFeaturedProducts() {
        when(productRepository.findByIsFeaturedTrue()).thenReturn(List.of(mockProduct));

        List<ProductDto> featured = productService.getFeaturedProducts();

        assertNotNull(featured);
        assertEquals(1, featured.size());
        assertTrue(featured.get(0).isFeatured());
    }

    @Test
    void testGetBestSellers() {
        when(productRepository.findByIsBestSellerTrue()).thenReturn(List.of(mockProduct));

        List<ProductDto> bestSellers = productService.getBestSellers();

        assertNotNull(bestSellers);
        assertEquals(1, bestSellers.size());
        assertTrue(bestSellers.get(0).isBestSeller());
    }

    @Test
    void testSearchQuick() {
        when(productRepository.searchQuick("nomad")).thenReturn(List.of(mockProduct));

        List<ProductDto> results = productService.searchQuick("nomad");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("the-nomad-backpack", results.get(0).getSlug());
    }
}
