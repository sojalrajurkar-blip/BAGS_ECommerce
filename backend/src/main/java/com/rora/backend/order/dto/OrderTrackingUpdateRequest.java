package com.rora.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTrackingUpdateRequest {

    private String carrier;
    private String trackingNumber;
    private String estimatedDelivery;
    private String completedStep;
    private String eventTitle;
    private String eventDescription;
    private String eventTime;
}
