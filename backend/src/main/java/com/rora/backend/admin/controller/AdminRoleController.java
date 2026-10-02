package com.rora.backend.admin.controller;

import com.rora.backend.admin.dto.AdminRoleDto;
import com.rora.backend.admin.dto.CreateAdminRoleRequest;
import com.rora.backend.admin.dto.UpdateAdminRoleRequest;
import com.rora.backend.admin.service.AdminUserService;
import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@Tag(name = "Admin Role & Permissions Matrix", description = "Backoffice RBAC role definitions and permission mappings")
public class AdminRoleController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List All Roles", description = "Retrieve list of all system roles with assigned permissions")
    public ResponseEntity<ApiResponse<List<AdminRoleDto>>> getAllRoles() {
        List<AdminRoleDto> roles = adminUserService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", roles));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get Role by ID", description = "Retrieve single role definition and permissions")
    public ResponseEntity<ApiResponse<AdminRoleDto>> getRoleById(@PathVariable String id) {
        AdminRoleDto role = adminUserService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved", role));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Role", description = "Define a new RBAC role with customized granular permissions")
    public ResponseEntity<ApiResponse<AdminRoleDto>> createRole(
            @Valid @RequestBody CreateAdminRoleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        AdminRoleDto created = adminUserService.createRole(request, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Role Permissions", description = "Update the description and permissions list for an existing role")
    public ResponseEntity<ApiResponse<AdminRoleDto>> updateRole(
            @PathVariable String id,
            @Valid @RequestBody UpdateAdminRoleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        AdminRoleDto updated = adminUserService.updateRole(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", updated));
    }
}
