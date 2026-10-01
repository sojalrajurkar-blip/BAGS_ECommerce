package com.rora.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;
    private String orderNumber;
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;

    @JsonProperty("discount")
    public BigDecimal getDiscount() {
        return discountAmount;
    }

    private BigDecimal shippingFee;

    @JsonProperty("shipping")
    public BigDecimal getShipping() {
        return shippingFee;
    }

    private BigDecimal taxAmount;

    @JsonProperty("tax")
    public BigDecimal getTax() {
        return taxAmount;
    }

    private BigDecimal total;
    private String couponCode;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String trackingNumber;
    private String carrier;
    private String estimatedDelivery;

    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();

    @Builder.Default
    private List<OrderTimelineEventDto> timeline = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @JsonProperty("date")
    public String getDate() {
        return createdAt != null ? createdAt.toString() : null;
    }
}
