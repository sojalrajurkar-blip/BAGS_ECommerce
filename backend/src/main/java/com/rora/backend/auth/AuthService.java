package com.rora.backend.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.rora.backend.auth.dto.AuthResponse;
import com.rora.backend.auth.dto.GoogleAuthRequest;
import com.rora.backend.auth.dto.LoginRequest;
import com.rora.backend.auth.dto.RegisterRequest;
import com.rora.backend.auth.dto.UserSummaryDto;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.security.JwtTokenProvider;
import com.rora.backend.security.UserPrincipal;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Value("${rora.google.client-id:647579746380-rsd8nvl7o86octd3ioscc4a5kbhvc5cn.apps.googleusercontent.com}")
    private String googleClientId;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .id("role-customer")
                            .name("ROLE_CUSTOMER")
                            .description("Standard customer role")
                            .build();
                    return roleRepository.save(newRole);
                });

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .email(request.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName().trim())
                .status("ACTIVE")
                .avatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200")
                .lastActive(Instant.now())
                .roles(new HashSet<>(Collections.singletonList(customerRole)))
                .build();

        User savedUser = userRepository.save(user);

        List<String> roles = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String jwt = tokenProvider.generateTokenFromUser(savedUser.getId(), savedUser.getEmail(), savedUser.getName(), roles);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationInMs())
                .user(mapToSummaryDto(savedUser))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        user.setLastActive(Instant.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationInMs())
                .user(mapToSummaryDto(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new BadRequestException("No authenticated user found in session.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", principal.getId()));

        return mapToSummaryDto(user);
    }

    @Transactional
    public AuthResponse loginWithGoogle(GoogleAuthRequest request) {
        if (request == null || request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw new BadRequestException("Google ID token is required.");
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
        .setAudience(Collections.singletonList(googleClientId))
        .build();

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(request.getIdToken());
        } catch (Exception e) {
            throw new BadRequestException("Failed to verify Google ID token cryptographic signature: " + e.getMessage());
        }

        if (idToken == null) {
            throw new BadRequestException("Invalid, tampered, or expired Google ID token.");
        }

        Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Google account payload did not provide a verified email address.");
        }

        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        if (name == null || name.isBlank()) {
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            if (givenName != null && !givenName.isBlank()) {
                name = givenName + (familyName != null && !familyName.isBlank() ? " " + familyName : "");
            } else {
                name = email.split("@")[0];
            }
        }

        // Auto-match existing user or auto-provision new customer profile
        final String finalName = name;
        final String finalEmail = email.toLowerCase().trim();
        final String finalPicture = picture != null && !picture.isBlank() ? picture : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200";

        User user = userRepository.findByEmailIgnoreCase(finalEmail)
                .map(existingUser -> {
                    existingUser.setLastActive(Instant.now());
                    if (picture != null && (existingUser.getAvatarUrl() == null || existingUser.getAvatarUrl().isBlank())) {
                        existingUser.setAvatarUrl(picture);
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                            .orElseGet(() -> roleRepository.save(Role.builder()
                                    .id("role-customer")
                                    .name("ROLE_CUSTOMER")
                                    .description("Standard customer role")
                                    .build()));

                    User newUser = User.builder()
                            .id(UUID.randomUUID().toString())
                            .email(finalEmail)
                            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .name(finalName)
                            .status("ACTIVE")
                            .avatarUrl(finalPicture)
                            .lastActive(Instant.now())
                            .roles(new HashSet<>(Collections.singletonList(customerRole)))
                            .build();

                    return userRepository.save(newUser);
                });

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String jwt = tokenProvider.generateTokenFromUser(user.getId(), user.getEmail(), user.getName(), roles);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationInMs())
                .user(mapToSummaryDto(user))
                .build();
    }

    public UserSummaryDto mapToSummaryDto(User user) {
        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                : Collections.emptyList();

        List<String> permissions = user.getRoles() != null
                ? user.getRoles().stream()
                    .filter(r -> r.getPermissions() != null)
                    .flatMap(r -> r.getPermissions().stream())
                    .map(p -> p.getName())
                    .distinct()
                    .collect(Collectors.toList())
                : Collections.emptyList();

        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .status(user.getStatus())
                .avatarUrl(user.getAvatarUrl())
                .roles(roleNames)
                .permissions(permissions)
                .build();
    }
}
