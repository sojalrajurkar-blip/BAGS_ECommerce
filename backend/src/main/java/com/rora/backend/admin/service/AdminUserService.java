package com.rora.backend.admin.service;

import com.rora.backend.admin.dto.*;

import java.util.List;

public interface AdminUserService {

    List<AdminUserDto> getAllAdminUsers();

    AdminUserDto getAdminUserById(String id);

    AdminUserDto createAdminUser(CreateAdminUserRequest request, String creatorEmail);

    AdminUserDto updateAdminUser(String id, UpdateAdminUserRequest request, String adminEmail);

    void deleteAdminUser(String id, String adminEmail);

    List<AdminRoleDto> getAllRoles();

    AdminRoleDto getRoleById(String id);

    AdminRoleDto createRole(CreateAdminRoleRequest request, String adminEmail);

    AdminRoleDto updateRole(String id, UpdateAdminRoleRequest request, String adminEmail);
}
