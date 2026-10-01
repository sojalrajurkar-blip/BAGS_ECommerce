package com.rora.backend.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileUpdateRequest {

    private String name;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
}
