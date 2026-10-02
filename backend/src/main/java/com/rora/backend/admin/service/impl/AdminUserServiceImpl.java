package com.rora.backend.admin.service.impl;

import com.rora.backend.admin.dto.*;
import com.rora.backend.admin.service.AdminUserService;
import com.rora.backend.admin.service.AuditLogService;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.user.Permission;
import com.rora.backend.user.PermissionRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserDto> getAllAdminUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> !r.getName().equalsIgnoreCase("ROLE_CUSTOMER")))
                .map(this::mapToAdminUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDto getAdminUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with ID: " + id));
        return mapToAdminUserDto(user);
    }

    @Override
    @Transactional
    public AdminUserDto createAdminUser(CreateAdminUserRequest request, String creatorEmail) {
        log.info("Creating new admin user: {} by {}", request.getEmail(), creatorEmail);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("User with email already exists: " + request.getEmail());
        }

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                roleRepository.findByName(roleName).ifPresent(roles::add);
                roleRepository.findById(roleName).ifPresent(roles::add);
            }
        } else if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            roleRepository.findByName(request.getRole()).ifPresent(roles::add);
            roleRepository.findById(request.getRole()).ifPresent(roles::add);
        }

        if (roles.isEmpty()) {
            roleRepository.findByName("ROLE_MANAGER").ifPresent(roles::add);
        }

        String rawPassword = (request.getPassword() != null && !request.getPassword().trim().isEmpty())
                ? request.getPassword()
                : "Password123!";

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .name(request.getName().trim())
                .passwordHash(passwordEncoder.encode(rawPassword))
                .status("ACTIVE")
                .avatarUrl(request.getAvatar() != null ? request.getAvatar() : "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=400")
                .lastActive(Instant.now())
                .roles(roles)
                .build();

        User saved = userRepository.save(user);

        auditLogService.recordAction(
                "Admin User Created",
                creatorEmail,
                saved.getName() + " (" + saved.getEmail() + ")",
                "User",
                "Info",
                "SUCCESS"
        );

        return mapToAdminUserDto(saved);
    }

    @Override
    @Transactional
    public AdminUserDto updateAdminUser(String id, UpdateAdminUserRequest request, String adminEmail) {
        log.info("Updating admin user ID: {} by {}", id, adminEmail);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with ID: " + id));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            user.setStatus(request.getStatus().toUpperCase());
        }
        if (request.getAvatar() != null) {
            user.setAvatarUrl(request.getAvatar());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                roleRepository.findByName(roleName).ifPresent(roles::add);
                roleRepository.findById(roleName).ifPresent(roles::add);
            }
            if (!roles.isEmpty()) {
                user.setRoles(roles);
            }
        } else if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(request.getRole()).ifPresent(roles::add);
            roleRepository.findById(request.getRole()).ifPresent(roles::add);
            if (!roles.isEmpty()) {
                user.setRoles(roles);
            }
        }

        User updated = userRepository.save(user);

        auditLogService.recordAction(
                "Admin User Updated",
                adminEmail,
                updated.getName() + " (" + updated.getEmail() + ")",
                "User",
                "Info",
                "SUCCESS"
        );

        return mapToAdminUserDto(updated);
    }

    @Override
    @Transactional
    public void deleteAdminUser(String id, String adminEmail) {
        log.info("Deleting admin user ID: {} by {}", id, adminEmail);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with ID: " + id));

        userRepository.delete(user);

        auditLogService.recordAction(
                "Admin User Deleted",
                adminEmail,
                user.getName() + " (" + user.getEmail() + ")",
                "User",
                "Warning",
                "SUCCESS"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminRoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToAdminRoleDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AdminRoleDto getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .or(() -> roleRepository.findByName(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID or name: " + id));
        return mapToAdminRoleDto(role);
    }

    @Override
    @Transactional
    public AdminRoleDto createRole(CreateAdminRoleRequest request, String adminEmail) {
        log.info("Creating role '{}' by {}", request.getName(), adminEmail);

        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Role with name already exists: " + request.getName());
        }

        String roleId = "role-" + request.getName().toLowerCase().replace("role_", "").replace("_", "-");

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissions() != null) {
            for (String permName : request.getPermissions()) {
                permissionRepository.findByName(permName).ifPresent(permissions::add);
            }
        }

        Role role = Role.builder()
                .id(roleId)
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(Instant.now())
                .permissions(permissions)
                .build();

        Role saved = roleRepository.save(role);

        auditLogService.recordAction(
                "Role Created",
                adminEmail,
                saved.getName(),
                "Role",
                "Info",
                "SUCCESS"
        );

        return mapToAdminRoleDto(saved);
    }

    @Override
    @Transactional
    public AdminRoleDto updateRole(String id, UpdateAdminRoleRequest request, String adminEmail) {
        log.info("Updating role ID: {} by {}", id, adminEmail);

        Role role = roleRepository.findById(id)
                .or(() -> roleRepository.findByName(id))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }

        if (request.getPermissions() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (String permName : request.getPermissions()) {
                permissionRepository.findByName(permName).ifPresent(permissions::add);
            }
            role.setPermissions(permissions);
        }

        Role updated = roleRepository.save(role);

        auditLogService.recordAction(
                "Role Permissions Updated",
                adminEmail,
                updated.getName(),
                "Role",
                "Warning",
                "SUCCESS"
        );

        return mapToAdminRoleDto(updated);
    }

    private AdminUserDto mapToAdminUserDto(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());

        String displayRole = roleNames.isEmpty() ? "User" : humanizeRoleName(roleNames.get(0));

        return AdminUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(displayRole)
                .roles(roleNames)
                .permissions(permissions)
                .status(user.getStatus() != null ? user.getStatus() : "Active")
                .lastActive(formatLastActive(user.getLastActive()))
                .avatar(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AdminRoleDto mapToAdminRoleDto(Role role) {
        List<String> permNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return AdminRoleDto.builder()
                .id(role.getId())
                .name(humanizeRoleName(role.getName()))
                .description(role.getDescription())
                .usersCount(1)
                .permissions(permNames)
                .build();
    }

    private String humanizeRoleName(String roleName) {
        if (roleName == null) return "";
        if (roleName.equals("ROLE_ADMIN")) return "Super Admin";
        if (roleName.equals("ROLE_MANAGER")) return "Store Manager";
        if (roleName.equals("ROLE_PRODUCT_MANAGER")) return "Product Manager";
        if (roleName.equals("ROLE_ORDER_MANAGER")) return "Order & Fulfillment Manager";
        if (roleName.equals("ROLE_CUSTOMER")) return "Customer";
        return roleName.replace("ROLE_", "").replace("_", " ");
    }

    private String formatLastActive(Instant instant) {
        if (instant == null) return "Recently";
        Duration duration = Duration.between(instant, Instant.now());
        long minutes = duration.toMinutes();
        if (minutes < 60) return minutes <= 1 ? "Just now" : minutes + " mins ago";
        long hours = duration.toHours();
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");
        long days = duration.toDays();
        if (days == 1) return "Yesterday";
        return days + " days ago";
    }
}
