package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.*;
import com.rora.backend.admin.service.impl.AdminUserServiceImpl;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.user.Permission;
import com.rora.backend.user.PermissionRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User mockAdminUser;
    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = Role.builder()
                .id("role-admin")
                .name("ROLE_ADMIN")
                .description("Super administrator")
                .permissions(Set.of(Permission.builder().id("perm-1").name("PRODUCT_CREATE").build()))
                .build();

        mockAdminUser = User.builder()
                .id("usr-1")
                .name("Sarah Jenkins")
                .email("sarah.j@rorastudios.com")
                .passwordHash("hashed")
                .status("ACTIVE")
                .roles(Set.of(mockRole))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should list all admin users")
    void testGetAllAdminUsers() {
        when(userRepository.findAll()).thenReturn(List.of(mockAdminUser));

        List<AdminUserDto> users = adminUserService.getAllAdminUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("Sarah Jenkins");
        assertThat(users.get(0).getRole()).isEqualTo("Super Admin");
    }

    @Test
    @DisplayName("Should create new admin user and record audit log")
    void testCreateAdminUser() {
        CreateAdminUserRequest req = CreateAdminUserRequest.builder()
                .name("Kabir Verma")
                .email("kabir.v@rorastudios.com")
                .password("Secret123!")
                .role("ROLE_MANAGER")
                .build();

        when(userRepository.existsByEmail("kabir.v@rorastudios.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_MANAGER")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("Secret123!")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("usr-new");
            return u;
        });

        AdminUserDto created = adminUserService.createAdminUser(req, "admin@rora.com");

        assertThat(created).isNotNull();
        assertThat(created.getEmail()).isEqualTo("kabir.v@rorastudios.com");
        verify(auditLogService).recordAction(eq("Admin User Created"), anyString(), anyString(), eq("User"), anyString(), eq("SUCCESS"));
    }

    @Test
    @DisplayName("Should throw BadRequestException if email already exists")
    void testCreateAdminUser_DuplicateEmail() {
        CreateAdminUserRequest req = CreateAdminUserRequest.builder()
                .name("Existing")
                .email("sarah.j@rorastudios.com")
                .build();

        when(userRepository.existsByEmail("sarah.j@rorastudios.com")).thenReturn(true);

        assertThatThrownBy(() -> adminUserService.createAdminUser(req, "admin@rora.com"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("Should update admin user details")
    void testUpdateAdminUser() {
        UpdateAdminUserRequest req = UpdateAdminUserRequest.builder()
                .name("Sarah J. Connor")
                .status("ACTIVE")
                .build();

        when(userRepository.findById("usr-1")).thenReturn(Optional.of(mockAdminUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AdminUserDto updated = adminUserService.updateAdminUser("usr-1", req, "admin@rora.com");

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Sarah J. Connor");
    }

    @Test
    @DisplayName("Should list all system roles")
    void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(mockRole));

        List<AdminRoleDto> roles = adminUserService.getAllRoles();

        assertThat(roles).hasSize(1);
        assertThat(roles.get(0).getName()).isEqualTo("Super Admin");
    }
}
