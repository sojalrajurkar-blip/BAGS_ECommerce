package com.rora.backend.catalog.service;

import com.rora.backend.catalog.dto.*;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductImage;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.*;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.common.exception.DuplicateResourceException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public PagedResponse<ProductDto> getFilteredProducts(ProductFilterParams filter) {
        Sort sort = parseSort(filter.getSortBy());
        int page = Math.max(0, filter.getPage());
        int limit = filter.getLimit() > 0 ? Math.min(filter.getLimit(), 100) : 20;

        Pageable pageable = PageRequest.of(page, limit, sort);
        Specification<Product> spec = ProductSpecification.withFilters(filter);

        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<ProductDto> dtoList = productPage.getContent().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());

        return PagedResponse.of(productPage, dtoList);
    }

    @Transactional(readOnly = true)
    public ProductDetailDto getProductByIdOrSlug(String idOrSlug) {
        Product product = findEntityByIdOrSlug(idOrSlug);
        return mapToDetailDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getBestSellers() {
        return productRepository.findByIsBestSellerTrue().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getNewArrivals() {
        return productRepository.findByIsNewArrivalTrue().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getCuratedProducts() {
        return productRepository.findByIsCuratedTrue().stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getRelatedProducts(String productIdOrSlug) {
        Product product = findEntityByIdOrSlug(productIdOrSlug);
        if (product.getCategory() == null) {
            return Collections.emptyList();
        }
        return productRepository.findRelatedProducts(product.getCategory().getId(), product.getId())
                .stream()
                .limit(8)
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDto> searchQuick(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.searchQuick(query.trim()).stream()
                .limit(10)
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDetailDto createProduct(ProductCreateRequest request) {
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Product slug already exists: " + request.getSlug());
        }
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + request.getSku());
        }

        Category category = categoryService.getCategoryEntity(request.getCategoryId());

        Product product = Product.builder()
                .slug(request.getSlug())
                .name(request.getName())
                .subtitle(request.getSubtitle())
                .tagline(request.getTagline())
                .category(category)
                .categoryName(category.getName())
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice() != null ? request.getOriginalPrice() : request.getPrice())
                .compareAtPrice(request.getCompareAtPrice())
                .discount(request.getDiscount() != null ? request.getDiscount() : 0)
                .currency(request.getCurrency() != null ? request.getCurrency() : "INR")
                .rating(request.getRating() != null ? request.getRating() : BigDecimal.valueOf(5.0))
                .reviewCount(request.getReviewCount() != null ? request.getReviewCount() : 0)
                .badge(request.getBadge())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .inStock(request.getInStock() != null ? request.getInStock() : true)
                .isNewArrival(request.getIsNewArrival() != null ? request.getIsNewArrival() : false)
                .isBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : false)
                .isCurated(request.getIsCurated() != null ? request.getIsCurated() : false)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .description(request.getDescription())
                .story(request.getStory())
                .material(request.getMaterial())
                .dimensions(request.getDimensions())
                .weight(request.getWeight())
                .capacity(request.getCapacity())
                .sku(request.getSku())
                .specifications(request.getSpecifications())
                .careInstructions(request.getCareInstructions())
                .features(request.getFeatures())
                .tags(request.getTags())
                .build();

        // Process Variants
        if (request.getVariants() != null) {
            for (ProductVariantRequest vr : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .sku(vr.getSku())
                        .name(vr.getName() != null ? vr.getName() : vr.getColorName())
                        .colorName(vr.getColorName())
                        .colorHex(vr.getColorHex())
                        .image(vr.getImage())
                        .stock(vr.getStock())
                        .priceOverride(vr.getPriceOverride())
                        .build();
                product.addVariant(variant);
            }
        }

        // Process Images
        if (request.getImages() != null) {
            int order = 0;
            for (String imgUrl : request.getImages()) {
                ProductImage img = ProductImage.builder()
                        .imageUrl(imgUrl)
                        .displayOrder(order++)
                        .build();
                product.addImage(img);
            }
        }

        Product saved = productRepository.save(product);

        // Update category product count
        category.setProductCount(category.getProductCount() + 1);
        categoryRepository.save(category);

        log.info("Created product: {} ({})", saved.getName(), saved.getSlug());
        return mapToDetailDto(saved);
    }

    @Transactional
    public ProductDetailDto updateProduct(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getSlug() != null && !request.getSlug().equalsIgnoreCase(product.getSlug())) {
            if (productRepository.existsBySlug(request.getSlug())) {
                throw new DuplicateResourceException("Product slug already exists: " + request.getSlug());
            }
            product.setSlug(request.getSlug());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryEntity(request.getCategoryId());
            product.setCategory(category);
            product.setCategoryName(category.getName());
        }

        if (request.getName() != null) product.setName(request.getName());
        if (request.getSubtitle() != null) product.setSubtitle(request.getSubtitle());
        if (request.getTagline() != null) product.setTagline(request.getTagline());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) product.setOriginalPrice(request.getOriginalPrice());
        if (request.getCompareAtPrice() != null) product.setCompareAtPrice(request.getCompareAtPrice());
        if (request.getDiscount() != null) product.setDiscount(request.getDiscount());
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getRating() != null) product.setRating(request.getRating());
        if (request.getReviewCount() != null) product.setReviewCount(request.getReviewCount());
        if (request.getBadge() != null) product.setBadge(request.getBadge());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getInStock() != null) product.setInStock(request.getInStock());
        if (request.getIsNewArrival() != null) product.setNewArrival(request.getIsNewArrival());
        if (request.getIsBestSeller() != null) product.setBestSeller(request.getIsBestSeller());
        if (request.getIsCurated() != null) product.setCurated(request.getIsCurated());
        if (request.getIsFeatured() != null) product.setFeatured(request.getIsFeatured());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getStory() != null) product.setStory(request.getStory());
        if (request.getMaterial() != null) product.setMaterial(request.getMaterial());
        if (request.getDimensions() != null) product.setDimensions(request.getDimensions());
        if (request.getWeight() != null) product.setWeight(request.getWeight());
        if (request.getCapacity() != null) product.setCapacity(request.getCapacity());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getSpecifications() != null) product.setSpecifications(request.getSpecifications());
        if (request.getCareInstructions() != null) product.setCareInstructions(request.getCareInstructions());
        if (request.getFeatures() != null) product.setFeatures(request.getFeatures());
        if (request.getTags() != null) product.setTags(request.getTags());

        // Update Images if provided
        if (request.getImages() != null) {
            product.getImages().clear();
            int order = 0;
            for (String imgUrl : request.getImages()) {
                ProductImage img = ProductImage.builder()
                        .imageUrl(imgUrl)
                        .displayOrder(order++)
                        .build();
                product.addImage(img);
            }
        }

        Product updated = productRepository.save(product);
        log.info("Updated product id: {} ({})", updated.getId(), updated.getSlug());
        return mapToDetailDto(updated);
    }

    @Transactional
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (product.getCategory() != null) {
            Category cat = product.getCategory();
            cat.setProductCount(Math.max(0, cat.getProductCount() - 1));
            categoryRepository.save(cat);
        }

        productRepository.delete(product);
        log.info("Deleted product id: {}", id);
    }

    @Transactional
    public ProductVariantDto addVariant(String productId, ProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (productVariantRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Variant SKU already exists: " + request.getSku());
        }

        ProductVariant variant = ProductVariant.builder()
                .sku(request.getSku())
                .name(request.getName() != null ? request.getName() : request.getColorName())
                .colorName(request.getColorName())
                .colorHex(request.getColorHex())
                .image(request.getImage())
                .stock(request.getStock())
                .priceOverride(request.getPriceOverride())
                .build();

        product.addVariant(variant);
        ProductVariant saved = productVariantRepository.save(variant);
        return mapToVariantDto(saved);
    }

    @Transactional
    public ProductVariantDto updateVariant(String productId, String variantId, ProductVariantRequest request) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Variant does not belong to product with id: " + productId);
        }

        if (!variant.getSku().equalsIgnoreCase(request.getSku()) && productVariantRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Variant SKU already exists: " + request.getSku());
        }

        variant.setSku(request.getSku());
        variant.setName(request.getName() != null ? request.getName() : request.getColorName());
        variant.setColorName(request.getColorName());
        variant.setColorHex(request.getColorHex());
        variant.setImage(request.getImage());
        variant.setStock(request.getStock());
        variant.setPriceOverride(request.getPriceOverride());

        ProductVariant updated = productVariantRepository.save(variant);
        return mapToVariantDto(updated);
    }

    @Transactional
    public void deleteVariant(String productId, String variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Variant does not belong to product with id: " + productId);
        }

        productVariantRepository.delete(variant);
    }

    // Helper finders & mappers
    public Product findEntityByIdOrSlug(String idOrSlug) {
        return productRepository.findBySlug(idOrSlug)
                .or(() -> productRepository.findById(idOrSlug))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with identifier: " + idOrSlug));
    }

    public ProductDto mapToSummaryDto(Product product) {
        if (product == null) return null;

        List<ProductVariantDto> variantDtos = product.getVariants().stream()
                .map(this::mapToVariantDto)
                .collect(Collectors.toList());

        List<String> imageUrls = product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        String primaryImage = !imageUrls.isEmpty() ? imageUrls.get(0)
                : (!variantDtos.isEmpty() ? variantDtos.get(0).getImage() : null);

        return ProductDto.builder()
                .id(product.getId())
                .slug(product.getSlug())
                .name(product.getName())
                .subtitle(product.getSubtitle())
                .tagline(product.getTagline())
                .category(product.getCategory() != null ? product.getCategory().getSlug() : product.getCategoryName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategoryName())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .discount(product.getDiscount())
                .currency(product.getCurrency())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .badge(product.getBadge())
                .stock(product.getStock())
                .inStock(product.isInStock())
                .isNewArrival(product.isNewArrival())
                .isBestSeller(product.isBestSeller())
                .isCurated(product.isCurated())
                .isFeatured(product.isFeatured())
                .material(product.getMaterial())
                .dimensions(product.getDimensions())
                .weight(product.getWeight())
                .capacity(product.getCapacity())
                .sku(product.getSku())
                .image(primaryImage)
                .colors(variantDtos)
                .images(imageUrls)
                .features(product.getFeatures())
                .tags(product.getTags())
                .createdAt(product.getCreatedAt())
                .build();
    }

    public ProductDetailDto mapToDetailDto(Product product) {
        if (product == null) return null;

        List<ProductVariantDto> variantDtos = product.getVariants().stream()
                .map(this::mapToVariantDto)
                .collect(Collectors.toList());

        List<String> imageUrls = product.getImages().stream()
                .sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        String primaryImage = !imageUrls.isEmpty() ? imageUrls.get(0)
                : (!variantDtos.isEmpty() ? variantDtos.get(0).getImage() : null);

        CategoryDto catDto = product.getCategory() != null ? categoryService.mapToDto(product.getCategory()) : null;

        return ProductDetailDto.builder()
                .id(product.getId())
                .slug(product.getSlug())
                .name(product.getName())
                .subtitle(product.getSubtitle())
                .tagline(product.getTagline())
                .category(product.getCategory() != null ? product.getCategory().getSlug() : product.getCategoryName())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategoryName())
                .categoryDetails(catDto)
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .compareAtPrice(product.getCompareAtPrice())
                .discount(product.getDiscount())
                .currency(product.getCurrency())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .badge(product.getBadge())
                .stock(product.getStock())
                .inStock(product.isInStock())
                .isNewArrival(product.isNewArrival())
                .isBestSeller(product.isBestSeller())
                .isCurated(product.isCurated())
                .isFeatured(product.isFeatured())
                .description(product.getDescription())
                .story(product.getStory())
                .material(product.getMaterial())
                .dimensions(product.getDimensions())
                .weight(product.getWeight())
                .capacity(product.getCapacity())
                .size(product.getDimensions())
                .sku(product.getSku())
                .image(primaryImage)
                .specifications(product.getSpecifications())
                .careInstructions(product.getCareInstructions())
                .features(product.getFeatures())
                .tags(product.getTags())
                .colors(variantDtos)
                .variants(variantDtos)
                .images(imageUrls)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductVariantDto mapToVariantDto(ProductVariant variant) {
        if (variant == null) return null;
        return ProductVariantDto.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .colorName(variant.getColorName())
                .colorHex(variant.getColorHex())
                .image(variant.getImage())
                .stock(variant.getStock())
                .priceOverride(variant.getPriceOverride())
                .build();
    }

    private Sort parseSort(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "reviewCount", "rating");
        }
        return switch (sortBy.toLowerCase()) {
            case "price_asc", "price-asc", "low-to-high" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc", "price-desc", "high-to-low" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating", "rating_desc", "rating-desc" -> Sort.by(Sort.Direction.DESC, "rating");
            case "newest", "created_desc", "created-desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "reviewCount", "rating");
        };
    }
}
