package com.rora.backend.customer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rora.backend.common.BaseEntity;
import com.rora.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "first_name", length = 128)
    private String firstName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "tier", length = 64)
    @Builder.Default
    private String tier = "Member";

    @Column(name = "total_spent", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "lifetime_value", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal lifetimeValue = BigDecimal.ZERO;

    @Column(name = "orders_count", nullable = false)
    @Builder.Default
    private int ordersCount = 0;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private List<CustomerAddress> addresses = new ArrayList<>();

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return (firstName.trim() + " " + lastName.trim()).trim();
        } else if (firstName != null) {
            return firstName.trim();
        } else if (lastName != null) {
            return lastName.trim();
        } else if (user != null && user.getName() != null) {
            return user.getName();
        }
        return "Customer";
    }

    public void addAddress(CustomerAddress address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(CustomerAddress address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
}
