package com.rora.backend.customer.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.customer.dto.*;
import com.rora.backend.customer.entity.Customer;
import com.rora.backend.customer.entity.CustomerAddress;
import com.rora.backend.customer.repository.CustomerAddressRepository;
import com.rora.backend.customer.repository.CustomerRepository;
import com.rora.backend.order.entity.Order;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(ZoneId.of("Asia/Kolkata"));

    private final CustomerRepository customerRepository;
    private final CustomerAddressRepository customerAddressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Customer getOrCreateCustomer(String userId) {
        return customerRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                    // Check if a customer already exists with this email
                    return customerRepository.findByEmailIgnoreCase(user.getEmail())
                            .map(existing -> {
                                existing.setUser(user);
                                return customerRepository.save(existing);
                            })
                            .orElseGet(() -> {
                                String[] parts = user.getName() != null ? user.getName().split(" ", 2) : new String[]{"", ""};
                                String first = parts[0];
                                String last = parts.length > 1 ? parts[1] : "";

                                Customer newCustomer = Customer.builder()
                                        .user(user)
                                        .email(user.getEmail())
                                        .firstName(first)
                                        .lastName(last)
                                        .tier("Member")
                                        .totalSpent(BigDecimal.ZERO)
                                        .lifetimeValue(BigDecimal.ZERO)
                                        .ordersCount(0)
                                        .build();
                                return customerRepository.save(newCustomer);
                            });
                });
    }

    @Transactional(readOnly = true)
    public CustomerDto getProfile(String userId) {
        Customer customer = getOrCreateCustomer(userId);
        return mapToDto(customer);
    }

    @Transactional
    public CustomerDto updateProfile(String userId, CustomerProfileUpdateRequest request) {
        Customer customer = getOrCreateCustomer(userId);

        if (request.getFirstName() != null) customer.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null) customer.setLastName(request.getLastName().trim());
        if (request.getPhone() != null) customer.setPhone(request.getPhone().trim());

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            String[] parts = request.getName().trim().split(" ", 2);
            customer.setFirstName(parts[0]);
            customer.setLastName(parts.length > 1 ? parts[1] : "");

            if (customer.getUser() != null) {
                User user = customer.getUser();
                user.setName(request.getName().trim());
                userRepository.save(user);
            }
        }

        Customer updated = customerRepository.save(customer);
        log.info("Updated profile for customer: {}", updated.getEmail());
        return mapToDto(updated);
    }

    @Transactional
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match.");
        }

        if (request.getConfirmPassword() != null && !request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation do not match.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Successfully changed password for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<CustomerAddressDto> getAddresses(String userId) {
        Customer customer = getOrCreateCustomer(userId);
        return customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapAddressToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAddressDto addAddress(String userId, AddressRequest request) {
        Customer customer = getOrCreateCustomer(userId);
        List<CustomerAddress> existing = customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(customer.getId());

        boolean isDefault = (request.getIsDefault() != null && request.getIsDefault()) || existing.isEmpty();

        if (isDefault) {
            existing.forEach(a -> a.setDefault(false));
            customerAddressRepository.saveAll(existing);
        }

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .fullName(request.getFullName().trim())
                .street(request.getStreet().trim())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .postalCode(request.getPostalCode().trim())
                .country(request.getCountry() != null ? request.getCountry().trim() : "India")
                .phone(request.getPhone())
                .isDefault(isDefault)
                .build();

        CustomerAddress saved = customerAddressRepository.save(address);
        log.info("Saved new address for customer: {}", customer.getEmail());
        return mapAddressToDto(saved);
    }

    @Transactional
    public CustomerAddressDto updateAddress(String userId, String addressId, AddressRequest request) {
        Customer customer = getOrCreateCustomer(userId);
        CustomerAddress address = customerAddressRepository.findByIdAndCustomerId(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (request.getIsDefault() != null && request.getIsDefault() && !address.isDefault()) {
            List<CustomerAddress> existing = customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(customer.getId());
            existing.forEach(a -> a.setDefault(false));
            customerAddressRepository.saveAll(existing);
            address.setDefault(true);
        }

        address.setFullName(request.getFullName().trim());
        address.setStreet(request.getStreet().trim());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        if (request.getCountry() != null) address.setCountry(request.getCountry().trim());
        if (request.getPhone() != null) address.setPhone(request.getPhone().trim());

        CustomerAddress updated = customerAddressRepository.save(address);
        log.info("Updated address: {} for customer: {}", addressId, customer.getEmail());
        return mapAddressToDto(updated);
    }

    @Transactional
    public void deleteAddress(String userId, String addressId) {
        Customer customer = getOrCreateCustomer(userId);
        CustomerAddress address = customerAddressRepository.findByIdAndCustomerId(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        boolean wasDefault = address.isDefault();
        customerAddressRepository.delete(address);

        if (wasDefault) {
            List<CustomerAddress> remaining = customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(customer.getId());
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
                customerAddressRepository.save(remaining.get(0));
            }
        }
        log.info("Deleted address: {} for customer: {}", addressId, customer.getEmail());
    }

    @Transactional
    public CustomerAddressDto setDefaultAddress(String userId, String addressId) {
        Customer customer = getOrCreateCustomer(userId);
        CustomerAddress target = customerAddressRepository.findByIdAndCustomerId(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        List<CustomerAddress> existing = customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc(customer.getId());
        for (CustomerAddress addr : existing) {
            addr.setDefault(addr.getId().equals(addressId));
        }
        customerAddressRepository.saveAll(existing);

        target.setDefault(true);
        log.info("Set default address: {} for customer: {}", addressId, customer.getEmail());
        return mapAddressToDto(target);
    }

    // Admin Customer 360 Operations
    @Transactional(readOnly = true)
    public Page<CustomerDto> searchCustomersAdmin(String search, String tier, Pageable pageable) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanTier = (tier != null && !tier.trim().isEmpty() && !tier.equalsIgnoreCase("all")) ? tier.trim() : null;

        Page<Customer> page;
        if (cleanSearch == null && cleanTier == null) {
            page = customerRepository.findAll(pageable);
        } else if (cleanSearch == null) {
            page = customerRepository.findByTierIgnoreCase(cleanTier, pageable);
        } else if (cleanTier == null) {
            page = customerRepository.searchByKeyword(cleanSearch, pageable);
        } else {
            page = customerRepository.searchByKeywordAndTier(cleanSearch, cleanTier, pageable);
        }
        return page.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerByIdAdmin(String customerId) {
        Customer customer = customerRepository.findById(customerId.trim())
                .or(() -> customerRepository.findByEmailIgnoreCase(customerId.trim()))
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id or email: " + customerId));
        return mapToDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomerTierAdmin(String customerId, CustomerTierUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        customer.setTier(request.getTier().trim());
        Customer updated = customerRepository.save(customer);
        log.info("Updated customer tier to {} for customer {}", updated.getTier(), updated.getEmail());
        return mapToDto(updated);
    }

    public CustomerDto mapToDto(Customer customer) {
        if (customer == null) return null;

        List<CustomerAddressDto> addressDtos = customer.getAddresses() != null
                ? customer.getAddresses().stream().map(this::mapAddressToDto).collect(Collectors.toList())
                : List.of();

        String city = null;
        if (!addressDtos.isEmpty()) {
            city = addressDtos.get(0).getCity();
        }

        String joinedDate = customer.getCreatedAt() != null ? DATE_FORMATTER.format(customer.getCreatedAt()) : null;

        // Query last order date if exists
        String lastOrder = null;
        if (customer.getUser() != null) {
            List<Order> userOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(customer.getUser().getId());
            if (!userOrders.isEmpty()) {
                lastOrder = DATE_FORMATTER.format(userOrders.get(0).getCreatedAt());
            }
        }

        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getFullName())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .city(city)
                .tier(customer.getTier())
                .totalSpent(customer.getTotalSpent())
                .lifetimeValue(customer.getLifetimeValue())
                .ordersCount(customer.getOrdersCount())
                .joinedDate(joinedDate)
                .lastOrder(lastOrder)
                .addresses(addressDtos)
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public CustomerAddressDto mapAddressToDto(CustomerAddress address) {
        if (address == null) return null;
        return CustomerAddressDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .street(address.getStreet())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .build();
    }
}
