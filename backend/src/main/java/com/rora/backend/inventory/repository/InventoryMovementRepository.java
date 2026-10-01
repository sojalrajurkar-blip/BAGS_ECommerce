package com.rora.backend.inventory.repository;

import com.rora.backend.inventory.entity.InventoryMovement;
import com.rora.backend.inventory.entity.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>, JpaSpecificationExecutor<InventoryMovement> {

    Page<InventoryMovement> findByInventoryIdOrderByCreatedAtDesc(String inventoryId, Pageable pageable);

    Page<InventoryMovement> findBySkuOrderByCreatedAtDesc(String sku, Pageable pageable);

    Page<InventoryMovement> findByMovementTypeOrderByCreatedAtDesc(MovementType movementType, Pageable pageable);

    List<InventoryMovement> findTop10ByOrderByCreatedAtDesc();
}
