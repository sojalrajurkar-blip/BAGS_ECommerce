package com.rora.backend.customer.repository;

import com.rora.backend.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByUserId(String userId);

    Optional<Customer> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Page<Customer> findByTierIgnoreCase(String tier, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Customer> searchByKeyword(@Param("search") String search, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND LOWER(c.tier) = LOWER(:tier)")
    Page<Customer> searchByKeywordAndTier(@Param("search") String search, @Param("tier") String tier, Pageable pageable);
}
