package com.rora.backend.inventory.repository;

import com.rora.backend.inventory.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String>, JpaSpecificationExecutor<Inventory> {

    Optional<Inventory> findBySku(String sku);

    Optional<Inventory> findByVariantId(String variantId);

    List<Inventory> findAllByProductId(String productId);

    Optional<Inventory> findByProductIdAndVariantIsNull(String productId);

    @Query("SELECT i FROM Inventory i WHERE i.quantityAvailable <= i.lowStockThreshold ORDER BY i.quantityAvailable ASC")
    Page<Inventory> findLowStockItems(Pageable pageable);

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantityAvailable <= i.lowStockThreshold AND i.quantityAvailable > 0")
    long countLowStockItems();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantityAvailable <= 0")
    long countOutOfStockItems();

    @Query("SELECT COALESCE(SUM(i.quantityAvailable), 0) FROM Inventory i")
    long sumAvailableUnits();

    @Query("SELECT COALESCE(SUM(i.quantityReserved), 0) FROM Inventory i")
    long sumReservedUnits();
}
