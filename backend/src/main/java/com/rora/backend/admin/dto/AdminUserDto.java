package com.rora.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private String id;
    private String name;
    private String email;
    // Frontend expects 'role'
    private String role;
    private List<String> roles;
    private List<String> permissions;
    private String status;
    private String lastActive;
    private String avatar;
    private Instant createdAt;
}
