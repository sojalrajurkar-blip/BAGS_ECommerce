package com.rora.backend.auth;

import com.rora.backend.auth.dto.AuthResponse;
import com.rora.backend.auth.dto.LoginRequest;
import com.rora.backend.auth.dto.RegisterRequest;
import com.rora.backend.auth.dto.UserSummaryDto;
import com.rora.backend.common.ApiResponse;
import com.rora.backend.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & Access Control", description = "Endpoints for customer registration, authentication, JWT session verification, and user profile.")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account", description = "Creates a new user with ROLE_CUSTOMER and returns a signed JWT access token.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(ApiResponse.success("Account registered successfully", response), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials", description = "Validates user email and password, returning user profile metadata and a signed JWT access token.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get current authenticated user profile", description = "Returns active user information and assigned RBAC permissions based on Bearer token.")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserSummaryDto userSummary = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched successfully", userSummary));
    }
}
