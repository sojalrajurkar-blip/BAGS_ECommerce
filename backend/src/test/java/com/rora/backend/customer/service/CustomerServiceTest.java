package com.rora.backend.customer.service;

import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.customer.dto.*;
import com.rora.backend.customer.entity.Customer;
import com.rora.backend.customer.entity.CustomerAddress;
import com.rora.backend.customer.repository.CustomerAddressRepository;
import com.rora.backend.customer.repository.CustomerRepository;
import com.rora.backend.order.repository.OrderRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerAddressRepository customerAddressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerService customerService;

    private User testUser;
    private Customer testCustomer;
    private CustomerAddress testAddress;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-100")
                .email("sarah@rora-luxury.com")
                .name("Sarah Johnson")
                .passwordHash("hashed-pass")
                .build();

        testCustomer = Customer.builder()
                .id("cust-100")
                .user(testUser)
                .email("sarah@rora-luxury.com")
                .firstName("Sarah")
                .lastName("Johnson")
                .tier("VIP")
                .totalSpent(BigDecimal.valueOf(28450.00))
                .lifetimeValue(BigDecimal.valueOf(28450.00))
                .ordersCount(5)
                .addresses(new ArrayList<>())
                .build();

        testAddress = CustomerAddress.builder()
                .id("addr-1")
                .customer(testCustomer)
                .fullName("Sarah Johnson")
                .street("142 Bandra West, Hill Road")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400050")
                .country("India")
                .phone("+91 98201 44521")
                .isDefault(true)
                .build();

        testCustomer.getAddresses().add(testAddress);
    }

    @Test
    @DisplayName("Get profile returns existing customer DTO")
    void testGetProfile_ExistingCustomer_Success() {
        when(customerRepository.findByUserId("user-100")).thenReturn(Optional.of(testCustomer));

        CustomerDto profile = customerService.getProfile("user-100");

        assertNotNull(profile);
        assertEquals("Sarah Johnson", profile.getName());
        assertEquals("VIP", profile.getTier());
        assertEquals(5, profile.getOrdersCount());
        assertEquals(1, profile.getAddresses().size());
        assertEquals("Mumbai", profile.getCity());
    }

    @Test
    @DisplayName("Update profile modifies customer first and last name")
    void testUpdateProfile_Success() {
        when(customerRepository.findByUserId("user-100")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerProfileUpdateRequest req = CustomerProfileUpdateRequest.builder()
                .name("Sarah J. Williams")
                .phone("+91 98200 99887")
                .build();

        CustomerDto updated = customerService.updateProfile("user-100", req);

        assertNotNull(updated);
        assertEquals("Sarah", testCustomer.getFirstName());
        assertEquals("J. Williams", testCustomer.getLastName());
        assertEquals("+91 98200 99887", testCustomer.getPhone());
    }

    @Test
    @DisplayName("Change password succeeds when current password is valid")
    void testChangePassword_Success() {
        ChangePasswordRequest req = ChangePasswordRequest.builder()
                .currentPassword("OldPass123!")
                .newPassword("NewPass123!")
                .confirmPassword("NewPass123!")
                .build();

        when(userRepository.findById("user-100")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("OldPass123!", "hashed-pass")).thenReturn(true);
        when(passwordEncoder.encode("NewPass123!")).thenReturn("new-hashed-pass");

        assertDoesNotThrow(() -> customerService.changePassword("user-100", req));
        verify(userRepository).save(testUser);
        assertEquals("new-hashed-pass", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Change password throws BadRequestException when current password does not match")
    void testChangePassword_WrongPassword_ThrowsBadRequest() {
        ChangePasswordRequest req = ChangePasswordRequest.builder()
                .currentPassword("WrongPass!")
                .newPassword("NewPass123!")
                .build();

        when(userRepository.findById("user-100")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPass!", "hashed-pass")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> customerService.changePassword("user-100", req));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Add address sets default if it's the first address")
    void testAddAddress_FirstAddress_BecomesDefault() {
        when(customerRepository.findByUserId("user-100")).thenReturn(Optional.of(testCustomer));
        when(customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc("cust-100"))
                .thenReturn(new ArrayList<>());
        when(customerAddressRepository.save(any(CustomerAddress.class))).thenAnswer(i -> {
            CustomerAddress a = i.getArgument(0);
            a.setId("addr-new");
            return a;
        });

        AddressRequest req = AddressRequest.builder()
                .fullName("Sarah Johnson")
                .street("Worli Sea Face")
                .city("Mumbai")
                .state("MH")
                .postalCode("400018")
                .country("India")
                .build();

        CustomerAddressDto result = customerService.addAddress("user-100", req);

        assertNotNull(result);
        assertTrue(result.isDefault());
        assertEquals("Mumbai", result.getCity());
    }

    @Test
    @DisplayName("Set default address unsets all other addresses")
    void testSetDefaultAddress_UnsetsOthers() {
        CustomerAddress addr2 = CustomerAddress.builder()
                .id("addr-2")
                .customer(testCustomer)
                .fullName("Sarah Johnson")
                .street("Worli Sea Face")
                .city("Mumbai")
                .state("MH")
                .postalCode("400018")
                .isDefault(false)
                .build();

        List<CustomerAddress> addresses = new ArrayList<>(List.of(testAddress, addr2));

        when(customerRepository.findByUserId("user-100")).thenReturn(Optional.of(testCustomer));
        when(customerAddressRepository.findByIdAndCustomerId("addr-2", "cust-100")).thenReturn(Optional.of(addr2));
        when(customerAddressRepository.findByCustomerIdOrderByIsDefaultDescCreatedAtDesc("cust-100")).thenReturn(addresses);

        CustomerAddressDto result = customerService.setDefaultAddress("user-100", "addr-2");

        assertTrue(result.isDefault());
        assertFalse(testAddress.isDefault());
        assertTrue(addr2.isDefault());
    }

    @Test
    @DisplayName("Admin search customers returns paginated customer DTOs")
    void testAdminSearchCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(testCustomer), pageable, 1);

        when(customerRepository.searchByKeywordAndTier("Sarah", "VIP", pageable)).thenReturn(page);

        Page<CustomerDto> result = customerService.searchCustomersAdmin("Sarah", "VIP", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Sarah Johnson", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Admin update customer tier succeeds")
    void testAdminUpdateCustomerTier() {
        when(customerRepository.findById("cust-100")).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        CustomerTierUpdateRequest req = CustomerTierUpdateRequest.builder()
                .tier("Gold Patron")
                .build();

        CustomerDto updated = customerService.updateCustomerTierAdmin("cust-100", req);

        assertNotNull(updated);
        assertEquals("Gold Patron", updated.getTier());
    }
}
