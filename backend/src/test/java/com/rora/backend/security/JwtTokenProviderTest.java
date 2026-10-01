package com.rora.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationInMs", testExpiration);
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        String userId = "user-123";
        String email = "test@rora-luxury.com";
        String name = "Test User";
        List<String> roles = List.of("ROLE_CUSTOMER");

        String token = tokenProvider.generateTokenFromUser(userId, email, name, roles);

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validateToken(token)).isTrue();
        assertThat(tokenProvider.getUserIdFromJWT(token)).isEqualTo(userId);
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThat(tokenProvider.validateToken("invalid.token.structure")).isFalse();
    }
}
