package com.rora.backend.order.repository;

import com.rora.backend.order.entity.OrderTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTimelineEventRepository extends JpaRepository<OrderTimelineEvent, Long> {
    List<OrderTimelineEvent> findByOrderIdOrderByDisplayOrderAsc(String orderId);
}
