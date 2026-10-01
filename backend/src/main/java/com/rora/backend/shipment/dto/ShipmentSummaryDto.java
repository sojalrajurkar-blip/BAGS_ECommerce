package com.rora.backend.shipment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentSummaryDto {

    private long totalShipments;
    private long pendingDispatch;
    private long inTransit;
    private long outForDelivery;
    private long delivered;
    private long deliveryExceptions;
}
