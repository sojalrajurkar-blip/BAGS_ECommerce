package com.rora.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRoleDto {
    private String id;
    private String name;
    private String description;
    private Integer usersCount;
    private List<String> permissions;
}
