package com.rora.backend.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.customer.dto.AddressRequest;
import com.rora.backend.customer.dto.CustomerProfileUpdateRequest;
import com.rora.backend.customer.dto.CustomerTierUpdateRequest;
import com.rora.backend.customer.entity.Customer;
import com.rora.backend.customer.entity.CustomerAddress;
import com.rora.backend.customer.repository.CustomerAddressRepository;
import com.rora.backend.customer.repository.CustomerRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAddressRepository customerAddressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .id("role-customer")
                        .name("ROLE_CUSTOMER")
                        .description("Customer role")
                        .build()));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .id("role-admin")
                        .name("ROLE_ADMIN")
                        .description("Admin role")
                        .build()));

        testUser = userRepository.findByEmailIgnoreCase("sarah.customer@rora-luxury.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("sarah.customer@rora-luxury.com")
                        .passwordHash(passwordEncoder.encode("Password123!"))
                        .name("Sarah Customer")
                        .roles(new HashSet<>(Set.of(customerRole)))
                        .build()));

        testCustomer = customerRepository.findByUserId(testUser.getId())
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .user(testUser)
                        .email("sarah.customer@rora-luxury.com")
                        .firstName("Sarah")
                        .lastName("Customer")
                        .tier("VIP")
                        .totalSpent(BigDecimal.valueOf(25000.00))
                        .lifetimeValue(BigDecimal.valueOf(25000.00))
                        .ordersCount(4)
                        .addresses(new ArrayList<>())
                        .build()));

        if (testCustomer.getAddresses().isEmpty()) {
            CustomerAddress address = CustomerAddress.builder()
                    .customer(testCustomer)
                    .fullName("Sarah Customer")
                    .street("142 Bandra West, Hill Road")
                    .city("Mumbai")
                    .state("Maharashtra")
                    .postalCode("400050")
                    .country("India")
                    .phone("+91 98201 44521")
                    .isDefault(true)
                    .build();
            customerAddressRepository.save(address);
        }
    }

    @Test
    @WithMockUser(username = "sarah.customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/account/profile - Customer fetches their own profile")
    void testGetProfile_Success() throws Exception {
        mockMvc.perform(get("/api/v1/account/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("sarah.customer@rora-luxury.com"))
                .andExpect(jsonPath("$.data.name").value("Sarah Customer"))
                .andExpect(jsonPath("$.data.tier").value("VIP"));
    }

    @Test
    @WithMockUser(username = "sarah.customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("PUT /api/v1/account/profile - Customer updates their personal profile")
    void testUpdateProfile_Success() throws Exception {
        CustomerProfileUpdateRequest request = CustomerProfileUpdateRequest.builder()
                .name("Sarah Williams")
                .phone("+91 98765 43210")
                .build();

        mockMvc.perform(put("/api/v1/account/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Sarah Williams"))
                .andExpect(jsonPath("$.data.phone").value("+91 98765 43210"));
    }

    @Test
    @WithMockUser(username = "sarah.customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("GET /api/v1/account/addresses - Customer retrieves saved addresses")
    void testGetAddresses_Success() throws Exception {
        mockMvc.perform(get("/api/v1/account/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].city").value("Mumbai"));
    }

    @Test
    @WithMockUser(username = "sarah.customer@rora-luxury.com", roles = {"CUSTOMER"})
    @DisplayName("POST /api/v1/account/addresses - Customer adds a new saved address")
    void testAddAddress_Success() throws Exception {
        AddressRequest request = AddressRequest.builder()
                .fullName("Sarah Studio")
                .street("Studio 18, Worli Sea Face")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400018")
                .country("India")
                .phone("+91 98201 44521")
                .isDefault(false)
                .build();

        mockMvc.perform(post("/api/v1/account/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Sarah Studio"))
                .andExpect(jsonPath("$.data.postalCode").value("400018"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/customers - Admin searches customers")
    void testAdminSearchCustomers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/customers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/customers/{id}/tier - Admin updates customer tier")
    void testAdminUpdateCustomerTier() throws Exception {
        CustomerTierUpdateRequest req = CustomerTierUpdateRequest.builder()
                .tier("Gold Patron")
                .build();

        mockMvc.perform(put("/api/v1/admin/customers/" + testCustomer.getId() + "/tier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.tier").value("Gold Patron"));
    }
}
