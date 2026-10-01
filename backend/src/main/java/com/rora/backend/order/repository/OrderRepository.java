package com.rora.backend.order.repository;

import com.rora.backend.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o WHERE o.id = :idOrNumber OR o.orderNumber = :idOrNumber OR o.orderNumber = CONCAT('#', :idOrNumber) OR REPLACE(o.orderNumber, '#', '') = :idOrNumber")
    Optional<Order> findByIdOrOrderNumber(@Param("idOrNumber") String idOrNumber);

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Order> findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(String customerEmail);

    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
           "(:search IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR LOWER(o.status) = LOWER(:status))")
    Page<Order> searchOrders(@Param("search") String search, @Param("status") String status, Pageable pageable);
}
