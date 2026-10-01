package com.rora.backend.shipment.dto;

import com.rora.backend.order.dto.OrderItemDto;
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
public class ShipmentTrackingDto {

    private String awbNumber;
    private String orderNumber;
    private String customerName;
    private String courier;
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

    @Builder.Default
    private List<ShipmentEventDto> events = new ArrayList<>();

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();
}
