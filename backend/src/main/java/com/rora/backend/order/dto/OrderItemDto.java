package com.rora.backend.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {

    private String id;
    private String productId;
    private String variantId;
    private String productName;

    @JsonProperty("name")
    public String getName() {
        return productName;
    }

    private String colorName;

    @JsonProperty("color")
    public String getColor() {
        return colorName;
    }

    private String imageUrl;

    @JsonProperty("image")
    public String getImage() {
        return imageUrl;
    }

    private BigDecimal unitPrice;

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return unitPrice;
    }

    private int quantity;
    private BigDecimal totalPrice;
}
