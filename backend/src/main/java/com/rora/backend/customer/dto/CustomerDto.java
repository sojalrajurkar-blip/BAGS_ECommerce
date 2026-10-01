package com.rora.backend.customer.dto;

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
public class CustomerDto {

    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String city;
    private String tier;
    private BigDecimal totalSpent;
    private BigDecimal lifetimeValue;
    private int ordersCount;

    @JsonProperty("orderCount")
    public int getOrderCount() {
        return ordersCount;
    }

    private String joinedDate;
    private String lastOrder;

    @Builder.Default
    private List<CustomerAddressDto> addresses = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
