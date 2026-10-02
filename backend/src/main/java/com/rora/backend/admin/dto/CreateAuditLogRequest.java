package com.rora.backend.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditLogRequest {

    @NotBlank(message = "Action name is required")
    private String action;

    @NotBlank(message = "Actor username or email is required")
    private String actor;

    private String target;
    private String entityType;
    private String ipAddress;
    private String status;
    private String severity;
    private Map<String, Object> details;
}
