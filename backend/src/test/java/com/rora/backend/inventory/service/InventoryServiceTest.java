package com.rora.backend.inventory.service;

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
import com.rora.backend.inventory.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product testProduct;
    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id("prod-1")
                .name("The Nomad Backpack")
                .slug("the-nomad-backpack")
                .price(BigDecimal.valueOf(4899.00))
                .stock(10)
                .inStock(true)
                .images(new ArrayList<>())
                .build();

        testVariant = ProductVariant.builder()
                .id("var-1-1")
                .product(testProduct)
                .name("Olive Green")
                .colorName("Olive Green")
                .colorHex("#555E48")
                .sku("RRA-NMD-01-OLV")
                .stock(10)
                .image("https://images.unsplash.com/photo-1553062407-98eeb64c6a62")
                .build();

        testInventory = Inventory.builder()
                .id("inv-1-1")
                .product(testProduct)
                .variant(testVariant)
                .sku("RRA-NMD-01-OLV")
                .quantityAvailable(10)
                .quantityReserved(0)
                .lowStockThreshold(4)
                .warehouseLocation("Main Atelier Vault, Mumbai")
                .binLocation("A-01-01")
                .build();
    }

    @Test
    @DisplayName("Get inventory summary aggregates KPI metrics accurately")
    void testGetInventorySummary_Success() {
        when(inventoryRepository.count()).thenReturn(24L);
        when(inventoryRepository.sumAvailableUnits()).thenReturn(180L);
        when(inventoryRepository.sumReservedUnits()).thenReturn(15L);
        when(inventoryRepository.countLowStockItems()).thenReturn(3L);
        when(inventoryRepository.countOutOfStockItems()).thenReturn(1L);
        when(movementRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of());

        InventorySummaryDto summary = inventoryService.getInventorySummary();

        assertNotNull(summary);
        assertEquals(24L, summary.getTotalSkus());
        assertEquals(180L, summary.getTotalAvailableUnits());
        assertEquals(15L, summary.getTotalReservedUnits());
        assertEquals(195L, summary.getTotalUnits());
        assertEquals(3L, summary.getLowStockAlertCount());
        assertEquals(1L, summary.getOutOfStockCount());
        assertEquals(20L, summary.getInStockCount());
    }

    @Test
    @DisplayName("Search inventory returns paginated DTO results")
    void testSearchInventory_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Inventory> page = new PageImpl<>(List.of(testInventory), pageable, 1);

        when(inventoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<InventoryDto> result = inventoryService.searchInventory("Nomad", "IN_STOCK", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("RRA-NMD-01-OLV", result.getContent().get(0).getSku());
        assertEquals(10, result.getContent().get(0).getQuantityAvailable());
        assertEquals(StockStatus.IN_STOCK, result.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("Get low stock alerts fetches items below threshold")
    void testGetLowStockAlerts_Success() {
        testInventory.setQuantityAvailable(2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Inventory> page = new PageImpl<>(List.of(testInventory), pageable, 1);

        when(inventoryRepository.findLowStockItems(pageable)).thenReturn(page);

        Page<InventoryDto> alerts = inventoryService.getLowStockAlerts(pageable);

        assertNotNull(alerts);
        assertEquals(1, alerts.getTotalElements());
        assertEquals(StockStatus.LOW_STOCK, alerts.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("Get inventory by SKU succeeds")
    void testGetInventoryByIdOrSku_BySku_Success() {
        when(inventoryRepository.findById("RRA-NMD-01-OLV")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));

        InventoryDto dto = inventoryService.getInventoryByIdOrSku("RRA-NMD-01-OLV");

        assertNotNull(dto);
        assertEquals("inv-1-1", dto.getId());
        assertEquals("RRA-NMD-01-OLV", dto.getSku());
    }

    @Test
    @DisplayName("Get inventory throws ResourceNotFoundException when SKU does not exist")
    void testGetInventoryByIdOrSku_NotFound_ThrowsException() {
        when(inventoryRepository.findById("NON_EXISTENT")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("NON_EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryByIdOrSku("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Adjust stock increases quantity and records RESTOCK movement")
    void testAdjustStock_Positive_Success() {
        when(inventoryRepository.findById("RRA-NMD-01-OLV")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                .sku("RRA-NMD-01-OLV")
                .quantityChange(5)
                .movementType(MovementType.RESTOCK)
                .reason("New batch intake")
                .referenceId("PO-2026-99")
                .build();

        InventoryDto updated = inventoryService.adjustStock(request, "admin@rora-luxury.com");

        assertNotNull(updated);
        assertEquals(15, updated.getQuantityAvailable());
        verify(movementRepository).save(any(InventoryMovement.class));
        verify(productVariantRepository).save(testVariant);
        verify(productRepository).save(testProduct);
        assertEquals(15, testVariant.getStock());
        assertEquals(15, testProduct.getStock());
    }

    @Test
    @DisplayName("Adjust stock throws BadRequestException when resulting quantity is negative")
    void testAdjustStock_NegativeExceedsStock_ThrowsBadRequest() {
        when(inventoryRepository.findById("RRA-NMD-01-OLV")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));

        StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                .sku("RRA-NMD-01-OLV")
                .quantityChange(-15) // current is 10
                .movementType(MovementType.DAMAGE)
                .reason("Damaged in transit")
                .build();

        assertThrows(BadRequestException.class, () -> inventoryService.adjustStock(request, "admin@rora-luxury.com"));
        verify(movementRepository, never()).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Batch adjust stock applies multiple adjustments")
    void testBatchAdjustStock_Success() {
        when(inventoryRepository.findById("RRA-NMD-01-OLV")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        BatchStockAdjustmentRequest batchReq = BatchStockAdjustmentRequest.builder()
                .adjustments(List.of(
                        StockAdjustmentRequest.builder()
                                .sku("RRA-NMD-01-OLV")
                                .quantityChange(3)
                                .movementType(MovementType.RESTOCK)
                                .build()
                ))
                .globalReason("Reconciliation")
                .build();

        List<InventoryDto> results = inventoryService.batchAdjustStock(batchReq, "admin@rora-luxury.com");

        assertEquals(1, results.size());
        assertEquals(13, results.get(0).getQuantityAvailable());
    }

    @Test
    @DisplayName("Update threshold modifies low stock threshold and warehouse bin")
    void testUpdateThreshold_Success() {
        when(inventoryRepository.findById("RRA-NMD-01-OLV")).thenReturn(Optional.empty());
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        UpdateThresholdRequest req = UpdateThresholdRequest.builder()
                .lowStockThreshold(8)
                .warehouseLocation("South Logistics Hub, Bengaluru")
                .binLocation("D-04-09")
                .build();

        InventoryDto updated = inventoryService.updateThreshold("RRA-NMD-01-OLV", req);

        assertNotNull(updated);
        assertEquals(8, updated.getLowStockThreshold());
        assertEquals("South Logistics Hub, Bengaluru", updated.getWarehouseLocation());
        assertEquals("D-04-09", updated.getBinLocation());
    }

    @Test
    @DisplayName("Process sale deducts stock and records SALE movement")
    void testProcessSale_Success() {
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        inventoryService.processSale("RRA-NMD-01-OLV", 2, "#RRA89241", "sarah@rora-luxury.com");

        assertEquals(8, testInventory.getQuantityAvailable());
        verify(movementRepository).save(any(InventoryMovement.class));
        verify(productVariantRepository).save(testVariant);
    }

    @Test
    @DisplayName("Process order cancellation restores stock and logs RETURN movement")
    void testProcessOrderCancellation_Success() {
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        inventoryService.processOrderCancellation("RRA-NMD-01-OLV", 2, "#RRA88940", "system");

        assertEquals(12, testInventory.getQuantityAvailable());
        verify(movementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Reserve stock moves available quantity to reserved")
    void testReserveStock_Success() {
        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        inventoryService.reserveStock("RRA-NMD-01-OLV", 3, "#RRA99001");

        assertEquals(7, testInventory.getQuantityAvailable());
        assertEquals(3, testInventory.getQuantityReserved());
        verify(movementRepository).save(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Release reservation restores available stock and unreserves")
    void testReleaseReservation_Success() {
        testInventory.setQuantityAvailable(7);
        testInventory.setQuantityReserved(3);

        when(inventoryRepository.findBySku("RRA-NMD-01-OLV")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.findAllByProductId("prod-1")).thenReturn(List.of(testInventory));

        inventoryService.releaseReservation("RRA-NMD-01-OLV", 3, "#RRA99001");

        assertEquals(10, testInventory.getQuantityAvailable());
        assertEquals(0, testInventory.getQuantityReserved());
        verify(movementRepository).save(any(InventoryMovement.class));
    }
}
