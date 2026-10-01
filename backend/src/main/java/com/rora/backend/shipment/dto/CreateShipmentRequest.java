package com.rora.backend.shipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {

    @NotBlank(message = "Order ID or order number is required")
    private String orderIdOrNumber;

    @NotBlank(message = "Courier partner is required")
    private String courier;

    private String awbNumber;

    private String origin;

    private String destination;

    private String estimatedDelivery;

    private String notes;
}
