package com.rora.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {

    private String id;
    private String name;
    private String email;
    private String status;
    private String avatarUrl;
    private List<String> roles;
    private List<String> permissions;
}
