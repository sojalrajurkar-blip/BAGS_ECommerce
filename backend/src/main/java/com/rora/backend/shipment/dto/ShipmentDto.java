package com.rora.backend.shipment.dto;

import com.rora.backend.shipment.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDto {

    private String id;
    private String orderId;
    private String orderNumber;
    private String customer;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String courier;
    private String awbNumber;
    private String origin;
    private String destination;
    private ShipmentStatus status;
    private String statusDisplay;
    private Instant dispatchDate;
    private String formattedDispatchDate;
    private String estimatedDelivery;
    private Instant actualDeliveryDate;
    private String formattedActualDeliveryDate;
    private String trackingUrl;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private List<ShipmentEventDto> events = new ArrayList<>();
}
