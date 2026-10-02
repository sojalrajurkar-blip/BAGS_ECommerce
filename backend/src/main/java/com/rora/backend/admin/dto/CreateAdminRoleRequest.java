package com.rora.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminRoleRequest {

    @NotBlank(message = "Role name is required")
    private String name;

    private String description;
    private List<String> permissions;
}
