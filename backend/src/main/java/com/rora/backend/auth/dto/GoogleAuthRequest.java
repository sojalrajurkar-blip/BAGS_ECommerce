package com.rora.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for Google OAuth 2.0 Single Sign-On ID Token exchange")
public class GoogleAuthRequest {

    @NotBlank(message = "Google ID token must not be blank")
    @Schema(description = "Cryptographically signed JWT ID Token issued by Google Identity Services", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMyJ9...")
    private String idToken;
}
