package com.rora.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.auth.dto.LoginRequest;
import com.rora.backend.auth.dto.RegisterRequest;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        if (roleRepository.findByName("ROLE_CUSTOMER").isEmpty()) {
            roleRepository.save(Role.builder()
                    .id("role-customer")
                    .name("ROLE_CUSTOMER")
                    .description("Customer role")
                    .build());
        }
    }

    @Test
    void shouldRegisterNewCustomerSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Aria Montgomery")
                .email("aria@rora-luxury.com")
                .password("Password123!")
                .phone("+919876543210")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("aria@rora-luxury.com")))
                .andExpect(jsonPath("$.data.user.name", is("Aria Montgomery")));
    }

    @Test
    void shouldRejectDuplicateEmailRegistration() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Aria Montgomery")
                .email("aria@rora-luxury.com")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void shouldLoginAndFetchCurrentProfileSuccessfully() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .name("Elena Gilbert")
                .email("elena@rora-luxury.com")
                .password("SecretPass123!")
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        LoginRequest loginReq = LoginRequest.builder()
                .email("elena@rora-luxury.com")
                .password("SecretPass123!")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("data").get("token").asText();

        // Fetch /me with Bearer token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("elena@rora-luxury.com")))
                .andExpect(jsonPath("$.data.name", is("Elena Gilbert")));
    }

    @Test
    void shouldRejectInvalidCredentialsOnLogin() throws Exception {
        LoginRequest loginReq = LoginRequest.builder()
                .email("nonexistent@rora-luxury.com")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectBlankGoogleIdToken() throws Exception {
        com.rora.backend.auth.dto.GoogleAuthRequest request = com.rora.backend.auth.dto.GoogleAuthRequest.builder()
                .idToken("")
                .build();

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void shouldRejectInvalidGoogleIdToken() throws Exception {
        com.rora.backend.auth.dto.GoogleAuthRequest request = com.rora.backend.auth.dto.GoogleAuthRequest.builder()
                .idToken("invalid-google-token-payload")
                .build();

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
