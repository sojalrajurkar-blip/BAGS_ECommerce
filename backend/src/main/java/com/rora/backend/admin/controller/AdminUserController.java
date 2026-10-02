package com.rora.backend.admin.controller;

import com.rora.backend.admin.dto.AdminUserDto;
import com.rora.backend.admin.dto.CreateAdminUserRequest;
import com.rora.backend.admin.dto.UpdateAdminUserRequest;
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
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Backoffice team member accounts, invitations, and status administration")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "List Admin Users", description = "Retrieve list of all active backoffice administrators, managers, and editors")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getAllAdminUsers() {
        List<AdminUserDto> users = adminUserService.getAllAdminUsers();
        return ResponseEntity.ok(ApiResponse.success("Admin users retrieved successfully", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get Admin User by ID", description = "Retrieve single admin team member profile and permissions")
    public ResponseEntity<ApiResponse<AdminUserDto>> getAdminUserById(@PathVariable String id) {
        AdminUserDto user = adminUserService.getAdminUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Admin user retrieved", user));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create / Invite Admin User", description = "Provision a new backoffice administrator or manager account")
    public ResponseEntity<ApiResponse<AdminUserDto>> createAdminUser(
            @Valid @RequestBody CreateAdminUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        AdminUserDto created = adminUserService.createAdminUser(request, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin user created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Admin User", description = "Modify role assignments, account status, or name of an administrator")
    public ResponseEntity<ApiResponse<AdminUserDto>> updateAdminUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateAdminUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        AdminUserDto updated = adminUserService.updateAdminUser(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Admin user updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete / Deactivate Admin User", description = "Remove access for a backoffice user account")
    public ResponseEntity<ApiResponse<Void>> deleteAdminUser(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        adminUserService.deleteAdminUser(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Admin user deleted successfully", null));
    }
}
