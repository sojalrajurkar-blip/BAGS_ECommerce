package com.rora.backend.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.catalog.entity.Category;
import com.rora.backend.catalog.entity.Product;
import com.rora.backend.catalog.entity.ProductVariant;
import com.rora.backend.catalog.repository.CategoryRepository;
import com.rora.backend.catalog.repository.ProductRepository;
import com.rora.backend.catalog.repository.ProductVariantRepository;
import com.rora.backend.inventory.dto.BatchStockAdjustmentRequest;
import com.rora.backend.inventory.dto.StockAdjustmentRequest;
import com.rora.backend.inventory.dto.UpdateThresholdRequest;
import com.rora.backend.inventory.entity.Inventory;
import com.rora.backend.inventory.entity.MovementType;
import com.rora.backend.inventory.repository.InventoryMovementRepository;
import com.rora.backend.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminInventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementRepository movementRepository;

    private Product testProduct;
    private ProductVariant testVariant;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        Category category = categoryRepository.findById("backpacks")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .id("backpacks")
                        .slug("backpacks")
                        .name("Backpacks")
                        .build()));

        testProduct = productRepository.findById("prod-1")
                .orElseGet(() -> productRepository.save(Product.builder()
                        .id("prod-1")
                        .slug("the-nomad-backpack")
                        .name("The Nomad Backpack")
                        .category(category)
                        .categoryName("Backpacks")
                        .price(BigDecimal.valueOf(4899.00))
                        .originalPrice(BigDecimal.valueOf(5499.00))
                        .stock(20)
                        .inStock(true)
                        .images(new ArrayList<>())
                        .build()));

        testVariant = productVariantRepository.findBySku("RRA-NMD-01-OLV")
                .orElseGet(() -> productVariantRepository.save(ProductVariant.builder()
                        .id("var-1-1")
                        .product(testProduct)
                        .sku("RRA-NMD-01-OLV")
                        .name("Olive Green")
                        .colorName("Olive Green")
                        .colorHex("#555E48")
                        .image("https://images.unsplash.com/photo-1553062407-98eeb64c6a62")
                        .stock(10)
                        .build()));

        testInventory = inventoryRepository.findBySku("RRA-NMD-01-OLV")
                .orElseGet(() -> inventoryRepository.save(Inventory.builder()
                        .id("inv-1-1")
                        .product(testProduct)
                        .variant(testVariant)
                        .sku("RRA-NMD-01-OLV")
                        .quantityAvailable(10)
                        .quantityReserved(0)
                        .lowStockThreshold(4)
                        .warehouseLocation("Main Atelier Vault, Mumbai")
                        .binLocation("A-01-01")
                        .build()));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/inventory/summary - Admin gets inventory health KPIs")
    void testGetSummary_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSkus").isNumber())
                .andExpect(jsonPath("$.data.totalAvailableUnits").isNumber())
                .andExpect(jsonPath("$.data.lowStockAlertCount").isNumber());
    }

    @Test
    @WithMockUser(username = "customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/admin/inventory/summary - Customer role is forbidden (403)")
    void testGetSummary_Customer_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/summary"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager@rora-luxury.com", roles = {"MANAGER"})
    @DisplayName("GET /api/v1/admin/inventory - Manager searches inventory items")
    void testSearchInventory_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory")
                        .param("search", "Nomad")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].sku").value("RRA-NMD-01-OLV"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/inventory/low-stock - Admin fetches low stock items")
    void testGetLowStockAlerts_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/inventory/{idOrSku} - Admin gets item details by SKU")
    void testGetByIdOrSku_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/RRA-NMD-01-OLV"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sku").value("RRA-NMD-01-OLV"))
                .andExpect(jsonPath("$.data.warehouseLocation").value("Main Atelier Vault, Mumbai"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/inventory/adjust - Admin adjusts stock and creates ledger entry")
    void testAdjustStock_Success() throws Exception {
        StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                .sku("RRA-NMD-01-OLV")
                .quantityChange(5)
                .movementType(MovementType.RESTOCK)
                .reason("Seasonal restock delivery")
                .referenceId("PO-2026-NMD-99")
                .batchNumber("BATCH-2026-Q1-99")
                .build();

        mockMvc.perform(post("/api/v1/admin/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantityAvailable").value(15))
                .andExpect(jsonPath("$.data.status").value("IN_STOCK"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/inventory/batch-adjust - Admin applies batch adjustments")
    void testBatchAdjustStock_Success() throws Exception {
        BatchStockAdjustmentRequest request = BatchStockAdjustmentRequest.builder()
                .adjustments(List.of(
                        StockAdjustmentRequest.builder()
                                .sku("RRA-NMD-01-OLV")
                                .quantityChange(2)
                                .movementType(MovementType.RESTOCK)
                                .build()
                ))
                .globalReason("Batch audit update")
                .build();

        mockMvc.perform(post("/api/v1/admin/inventory/batch-adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].quantityAvailable").value(12));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/inventory/{idOrSku}/threshold - Update low stock threshold & location")
    void testUpdateThreshold_Success() throws Exception {
        UpdateThresholdRequest req = UpdateThresholdRequest.builder()
                .lowStockThreshold(6)
                .warehouseLocation("South Logistics Hub, Bengaluru")
                .binLocation("B-02-14")
                .build();

        mockMvc.perform(put("/api/v1/admin/inventory/RRA-NMD-01-OLV/threshold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.lowStockThreshold").value(6))
                .andExpect(jsonPath("$.data.warehouseLocation").value("South Logistics Hub, Bengaluru"))
                .andExpect(jsonPath("$.data.binLocation").value("B-02-14"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/inventory/movements - Global movement history ledger")
    void testGetMovements_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/inventory/{idOrSku}/movements - Item specific movement history")
    void testGetMovementsForInventory_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/inventory/RRA-NMD-01-OLV/movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}
