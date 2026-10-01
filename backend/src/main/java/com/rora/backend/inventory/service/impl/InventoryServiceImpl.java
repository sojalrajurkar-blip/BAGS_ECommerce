package com.rora.backend.inventory.service.impl;

import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.inventory.dto.*;
import com.rora.backend.inventory.entity.Inventory;
import com.rora.backend.inventory.entity.InventoryMovement;
import com.rora.backend.inventory.entity.MovementType;
import com.rora.backend.inventory.entity.StockStatus;
import com.rora.backend.inventory.repository.InventoryMovementRepository;
import com.rora.backend.inventory.repository.InventoryRepository;
import com.rora.backend.inventory.service.InventoryService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryDto getInventorySummary() {
        long totalSkus = inventoryRepository.count();
        long totalAvailable = inventoryRepository.sumAvailableUnits();
        long totalReserved = inventoryRepository.sumReservedUnits();
        long lowStockCount = inventoryRepository.countLowStockItems();
        long outOfStockCount = inventoryRepository.countOutOfStockItems();
        long inStockCount = Math.max(0, totalSkus - lowStockCount - outOfStockCount);

        List<InventoryMovementDto> recentMovements = movementRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToMovementDto)
                .toList();

        return InventorySummaryDto.builder()
                .totalSkus(totalSkus)
                .totalAvailableUnits(totalAvailable)
                .totalReservedUnits(totalReserved)
                .totalUnits(totalAvailable + totalReserved)
                .lowStockAlertCount(lowStockCount)
                .outOfStockCount(outOfStockCount)
                .inStockCount(inStockCount)
                .recentMovements(recentMovements)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDto> searchInventory(String search, String status, String categoryId, Pageable pageable) {
        Specification<Inventory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Inventory, Product> productJoin = root.join("product", JoinType.INNER);
            Join<Inventory, ProductVariant> variantJoin = root.join("variant", JoinType.LEFT);

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate skuMatch = cb.like(cb.lower(root.get("sku")), pattern);
                Predicate prodNameMatch = cb.like(cb.lower(productJoin.get("name")), pattern);
                Predicate prodSlugMatch = cb.like(cb.lower(productJoin.get("slug")), pattern);
                Predicate varNameMatch = cb.like(cb.lower(variantJoin.get("name")), pattern);
                Predicate colorMatch = cb.like(cb.lower(variantJoin.get("colorName")), pattern);
                Predicate locMatch = cb.like(cb.lower(root.get("warehouseLocation")), pattern);

                predicates.add(cb.or(skuMatch, prodNameMatch, prodSlugMatch, varNameMatch, colorMatch, locMatch));
            }

            if (status != null && !status.trim().isEmpty()) {
                String st = status.trim().toUpperCase();
                if ("OUT_OF_STOCK".equals(st)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("quantityAvailable"), 0));
                } else if ("LOW_STOCK".equals(st)) {
                    predicates.add(cb.and(
                            cb.greaterThan(root.get("quantityAvailable"), 0),
                            cb.lessThanOrEqualTo(root.get("quantityAvailable"), root.get("lowStockThreshold"))
                    ));
                } else if ("IN_STOCK".equals(st)) {
                    predicates.add(cb.greaterThan(root.get("quantityAvailable"), root.get("lowStockThreshold")));
                }
            }

            if (categoryId != null && !categoryId.trim().isEmpty()) {
                String cat = categoryId.trim();
                predicates.add(cb.or(
                        cb.equal(productJoin.get("category").get("id"), cat),
                        cb.equal(productJoin.get("category").get("slug"), cat)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return inventoryRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDto> getLowStockAlerts(Pageable pageable) {
        return inventoryRepository.findLowStockItems(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDto getInventoryByIdOrSku(String idOrSku) {
        Inventory inventory = findEntityByIdOrSku(idOrSku);
        return mapToDto(inventory);
    }

    @Override
    @Transactional
    public InventoryDto adjustStock(StockAdjustmentRequest request, String actor) {
        Inventory inventory = findEntityByIdOrSku(request.getSku());

        int prevQty = inventory.getQuantityAvailable();
        int change = request.getQuantityChange();
        int newQty = prevQty + change;

        if (newQty < 0) {
            throw new BadRequestException("Adjustment would cause stock to drop below zero (Current: " + prevQty + ", Requested change: " + change + ")");
        }

        inventory.setQuantityAvailable(newQty);
        Inventory saved = inventoryRepository.save(inventory);

        // Record movement ledger
        InventoryMovement movement = InventoryMovement.builder()
                .inventory(saved)
                .sku(saved.getSku())
                .movementType(request.getMovementType())
                .quantityChange(change)
                .previousQuantity(prevQty)
                .newQuantity(newQty)
                .reason(request.getReason() != null ? request.getReason() : "Manual stock adjustment by " + actor)
                .referenceId(request.getReferenceId())
                .batchNumber(request.getBatchNumber())
                .createdBy(actor != null ? actor : "system")
                .build();
        movementRepository.save(movement);

        // Sync Product & ProductVariant stock
        syncCatalogStock(saved);

        log.info("Stock adjusted for SKU {}: {} -> {} (change: {}) by {}", saved.getSku(), prevQty, newQty, change, actor);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public List<InventoryDto> batchAdjustStock(BatchStockAdjustmentRequest request, String actor) {
        List<InventoryDto> results = new ArrayList<>();
        for (StockAdjustmentRequest item : request.getAdjustments()) {
            if (item.getReason() == null || item.getReason().trim().isEmpty()) {
                item.setReason(request.getGlobalReason());
            }
            results.add(adjustStock(item, actor));
        }
        return results;
    }

    @Override
    @Transactional
    public InventoryDto updateThreshold(String idOrSku, UpdateThresholdRequest request) {
        Inventory inventory = findEntityByIdOrSku(idOrSku);

        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getWarehouseLocation() != null && !request.getWarehouseLocation().trim().isEmpty()) {
            inventory.setWarehouseLocation(request.getWarehouseLocation().trim());
        }
        if (request.getBinLocation() != null && !request.getBinLocation().trim().isEmpty()) {
            inventory.setBinLocation(request.getBinLocation().trim());
        }

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Updated inventory metadata for SKU {}: threshold={}, location={}, bin={}",
                updated.getSku(), updated.getLowStockThreshold(), updated.getWarehouseLocation(), updated.getBinLocation());
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovementDto> getMovements(String sku, MovementType movementType, Pageable pageable) {
        Specification<InventoryMovement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (sku != null && !sku.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("sku")), sku.trim().toLowerCase()));
            }

            if (movementType != null) {
                predicates.add(cb.equal(root.get("movementType"), movementType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return movementRepository.findAll(spec, pageable).map(this::mapToMovementDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryMovementDto> getMovementsForInventory(String idOrSku, Pageable pageable) {
        Inventory inventory = findEntityByIdOrSku(idOrSku);
        return movementRepository.findByInventoryIdOrderByCreatedAtDesc(inventory.getId(), pageable)
                .map(this::mapToMovementDto);
    }

    @Override
    @Transactional
    public void processSale(String sku, int quantity, String orderId, String customerEmail) {
        Optional<Inventory> opt = inventoryRepository.findBySku(sku);
        if (opt.isEmpty()) {
            log.warn("Inventory record not found for SKU: {}. Skipping inventory movement ledger.", sku);
            return;
        }

        Inventory inventory = opt.get();
        int prevQty = inventory.getQuantityAvailable();
        int newQty = Math.max(0, prevQty - quantity);
        inventory.setQuantityAvailable(newQty);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .sku(sku)
                .movementType(MovementType.SALE)
                .quantityChange(-quantity)
                .previousQuantity(prevQty)
                .newQuantity(newQty)
                .reason("Customer Order Checkout: " + orderId)
                .referenceId(orderId)
                .createdBy(customerEmail != null ? customerEmail : "checkout")
                .build();
        movementRepository.save(movement);

        syncCatalogStock(inventory);
        log.info("Processed sale for SKU {}: qty {} (Order: {})", sku, quantity, orderId);
    }

    @Override
    @Transactional
    public void processOrderCancellation(String sku, int quantity, String orderId, String actor) {
        Optional<Inventory> opt = inventoryRepository.findBySku(sku);
        if (opt.isEmpty()) {
            return;
        }

        Inventory inventory = opt.get();
        int prevQty = inventory.getQuantityAvailable();
        int newQty = prevQty + quantity;
        inventory.setQuantityAvailable(newQty);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .sku(sku)
                .movementType(MovementType.RETURN)
                .quantityChange(quantity)
                .previousQuantity(prevQty)
                .newQuantity(newQty)
                .reason("Order Cancelled Restoral: " + orderId)
                .referenceId(orderId)
                .createdBy(actor != null ? actor : "system")
                .build();
        movementRepository.save(movement);

        syncCatalogStock(inventory);
        log.info("Processed order cancellation restoral for SKU {}: qty {} (Order: {})", sku, quantity, orderId);
    }

    @Override
    @Transactional
    public void processReturn(String sku, int quantity, String orderId, String reason, String actor) {
        Optional<Inventory> opt = inventoryRepository.findBySku(sku);
        if (opt.isEmpty()) {
            return;
        }

        Inventory inventory = opt.get();
        int prevQty = inventory.getQuantityAvailable();
        int newQty = prevQty + quantity;
        inventory.setQuantityAvailable(newQty);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .sku(sku)
                .movementType(MovementType.RETURN)
                .quantityChange(quantity)
                .previousQuantity(prevQty)
                .newQuantity(newQty)
                .reason(reason != null ? reason : "Customer Return Inspection Approved: " + orderId)
                .referenceId(orderId)
                .createdBy(actor != null ? actor : "returns-desk")
                .build();
        movementRepository.save(movement);

        syncCatalogStock(inventory);
        log.info("Processed customer return for SKU {}: qty {} (Order: {})", sku, quantity, orderId);
    }

    @Override
    @Transactional
    public void reserveStock(String sku, int quantity, String orderId) {
        Optional<Inventory> opt = inventoryRepository.findBySku(sku);
        if (opt.isEmpty()) {
            return;
        }

        Inventory inventory = opt.get();
        if (inventory.getQuantityAvailable() < quantity) {
            throw new BadRequestException("Insufficient available stock for SKU " + sku + " to reserve " + quantity + " units.");
        }

        int prevQty = inventory.getQuantityAvailable();
        inventory.setQuantityAvailable(prevQty - quantity);
        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .sku(sku)
                .movementType(MovementType.RESERVATION)
                .quantityChange(-quantity)
                .previousQuantity(prevQty)
                .newQuantity(inventory.getQuantityAvailable())
                .reason("Checkout Stock Hold: " + orderId)
                .referenceId(orderId)
                .createdBy("checkout-engine")
                .build();
        movementRepository.save(movement);

        syncCatalogStock(inventory);
    }

    @Override
    @Transactional
    public void releaseReservation(String sku, int quantity, String orderId) {
        Optional<Inventory> opt = inventoryRepository.findBySku(sku);
        if (opt.isEmpty()) {
            return;
        }

        Inventory inventory = opt.get();
        int prevReserved = inventory.getQuantityReserved();
        int releaseQty = Math.min(prevReserved, quantity);
        inventory.setQuantityReserved(prevReserved - releaseQty);
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + releaseQty);
        inventoryRepository.save(inventory);

        InventoryMovement movement = InventoryMovement.builder()
                .inventory(inventory)
                .sku(sku)
                .movementType(MovementType.RELEASE_RESERVATION)
                .quantityChange(releaseQty)
                .previousQuantity(inventory.getQuantityAvailable() - releaseQty)
                .newQuantity(inventory.getQuantityAvailable())
                .reason("Checkout Expired / Abandoned Stock Release: " + orderId)
                .referenceId(orderId)
                .createdBy("checkout-engine")
                .build();
        movementRepository.save(movement);

        syncCatalogStock(inventory);
    }

    private Inventory findEntityByIdOrSku(String idOrSku) {
        if (idOrSku == null || idOrSku.trim().isEmpty()) {
            throw new BadRequestException("SKU or Inventory ID must not be blank.");
        }
        String query = idOrSku.trim();
        return inventoryRepository.findById(query)
                .or(() -> inventoryRepository.findBySku(query))
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found for: " + query));
    }

    private void syncCatalogStock(Inventory inventory) {
        if (inventory.getVariant() != null) {
            ProductVariant variant = inventory.getVariant();
            variant.setStock(inventory.getQuantityAvailable());
            productVariantRepository.save(variant);

            // Also recalculate total parent product stock
            if (variant.getProduct() != null) {
                Product product = variant.getProduct();
                List<Inventory> allVariantInventories = inventoryRepository.findAllByProductId(product.getId());
                int totalProductStock = allVariantInventories.stream()
                        .mapToInt(Inventory::getQuantityAvailable)
                        .sum();
                product.setStock(totalProductStock);
                product.setInStock(totalProductStock > 0);
                productRepository.save(product);
            }
        } else if (inventory.getProduct() != null) {
            Product product = inventory.getProduct();
            product.setStock(inventory.getQuantityAvailable());
            product.setInStock(inventory.getQuantityAvailable() > 0);
            productRepository.save(product);
        }
    }

    private InventoryDto mapToDto(Inventory entity) {
        Product product = entity.getProduct();
        ProductVariant variant = entity.getVariant();

        String productImage = null;
        if (variant != null && variant.getImage() != null && !variant.getImage().trim().isEmpty()) {
            productImage = variant.getImage();
        } else if (product != null && !product.getImages().isEmpty()) {
            productImage = product.getImages().get(0).getImageUrl();
        }

        return InventoryDto.builder()
                .id(entity.getId())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .productSlug(product != null ? product.getSlug() : null)
                .productImage(productImage)
                .categoryName(product != null ? product.getCategoryName() : null)
                .variantId(variant != null ? variant.getId() : null)
                .variantName(variant != null ? variant.getName() : null)
                .colorName(variant != null ? variant.getColorName() : null)
                .colorHex(variant != null ? variant.getColorHex() : null)
                .sku(entity.getSku())
                .quantityAvailable(entity.getQuantityAvailable())
                .quantityReserved(entity.getQuantityReserved())
                .totalStock(entity.getTotalStock())
                .lowStockThreshold(entity.getLowStockThreshold())
                .status(entity.getStatus())
                .warehouseLocation(entity.getWarehouseLocation())
                .binLocation(entity.getBinLocation())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private InventoryMovementDto mapToMovementDto(InventoryMovement entity) {
        String productName = null;
        String productId = null;
        if (entity.getInventory() != null && entity.getInventory().getProduct() != null) {
            productName = entity.getInventory().getProduct().getName();
            productId = entity.getInventory().getProduct().getId();
        }

        return InventoryMovementDto.builder()
                .id(entity.getId())
                .inventoryId(entity.getInventory() != null ? entity.getInventory().getId() : null)
                .sku(entity.getSku())
                .productId(productId)
                .productName(productName)
                .movementType(entity.getMovementType())
                .quantityChange(entity.getQuantityChange())
                .previousQuantity(entity.getPreviousQuantity())
                .newQuantity(entity.getNewQuantity())
                .reason(entity.getReason())
                .referenceId(entity.getReferenceId())
                .batchNumber(entity.getBatchNumber())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
