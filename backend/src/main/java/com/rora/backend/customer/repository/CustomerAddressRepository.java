package com.rora.backend.customer.repository;

import com.rora.backend.customer.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, String> {

    List<CustomerAddress> findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(String customerId);

    Optional<CustomerAddress> findByIdAndCustomerId(String id, String customerId);

    Optional<CustomerAddress> findFirstByCustomerIdAndIsDefaultTrue(String customerId);
}
