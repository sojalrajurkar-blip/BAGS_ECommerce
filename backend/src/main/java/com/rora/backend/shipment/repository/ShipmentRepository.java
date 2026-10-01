package com.rora.backend.shipment.repository;

import com.rora.backend.shipment.entity.Shipment;
import com.rora.backend.shipment.entity.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String>, JpaSpecificationExecutor<Shipment> {

    Optional<Shipment> findByAwbNumberIgnoreCase(String awbNumber);

    List<Shipment> findByOrderNumber(String orderNumber);

    List<Shipment> findByOrderId(String orderId);

    List<Shipment> findByCustomerEmailIgnoreCase(String customerEmail);

    long countByStatus(ShipmentStatus status);

    @Query("SELECT s FROM Shipment s WHERE " +
            "(:status IS NULL OR s.status = :status) AND (" +
            ":query IS NULL OR :query = '' OR " +
            "LOWER(s.awbNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.orderNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.courier) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.destination) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Shipment> searchShipments(
            @Param("query") String query,
            @Param("status") ShipmentStatus status,
            Pageable pageable
    );
}
