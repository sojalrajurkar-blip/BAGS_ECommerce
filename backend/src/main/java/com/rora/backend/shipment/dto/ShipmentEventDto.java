package com.rora.backend.shipment.dto;

import com.rora.backend.shipment.entity.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentEventDto {

    private Long id;
    private ShipmentStatus status;
    private String statusDisplay;
    private String location;
    private String activity;
    private Instant eventTimestamp;
    private String formattedTimestamp;
    private String notes;
}
