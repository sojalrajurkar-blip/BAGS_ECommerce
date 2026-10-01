package com.rora.backend.shipment.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rora.backend.common.BaseEntity;
import com.rora.backend.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_number", length = 64, nullable = false)
    private String orderNumber;

    @Column(name = "customer_id", length = 64)
    private String customerId;

    @Column(name = "customer_name", length = 255, nullable = false)
    private String customerName;

    @Column(name = "customer_email", length = 255, nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone", length = 32)
    private String customerPhone;

    @Column(name = "courier", length = 128, nullable = false)
    private String courier;

    @Column(name = "awb_number", length = 128, nullable = false, unique = true)
    private String awbNumber;

    @Column(name = "origin", length = 255, nullable = false)
    @Builder.Default
    private String origin = "Mumbai Central Studio";

    @Column(name = "destination", length = 255, nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 64, nullable = false)
    @Builder.Default
    private ShipmentStatus status = ShipmentStatus.CREATED;

    @Column(name = "dispatch_date")
    private Instant dispatchDate;

    @Column(name = "estimated_delivery")
    private String estimatedDelivery;

    @Column(name = "actual_delivery_date")
    private Instant actualDeliveryDate;

    @Column(name = "tracking_url", length = 512)
    private String trackingUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("eventTimestamp DESC")
    @Builder.Default
    private List<ShipmentEvent> events = new ArrayList<>();

    public void addEvent(ShipmentEvent event) {
        events.add(event);
        event.setShipment(this);
    }
}
