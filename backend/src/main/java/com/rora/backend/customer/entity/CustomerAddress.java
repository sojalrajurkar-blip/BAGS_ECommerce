package com.rora.backend.customer.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rora.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class CustomerAddress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(name = "full_name", length = 255, nullable = false)
    private String fullName;

    @Column(name = "street", columnDefinition = "TEXT", nullable = false)
    private String street;

    @Column(name = "address_line2", columnDefinition = "TEXT")
    private String addressLine2;

    @Column(name = "city", length = 128, nullable = false)
    private String city;

    @Column(name = "state", length = 128, nullable = false)
    private String state;

    @Column(name = "postal_code", length = 32, nullable = false)
    private String postalCode;

    @Column(name = "country", length = 128, nullable = false)
    @Builder.Default
    private String country = "India";

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean isDefault = false;
}
