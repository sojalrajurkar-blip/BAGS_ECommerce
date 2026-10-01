package com.rora.backend.returns.dto;

import com.rora.backend.returns.entity.InspectionStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectReturnRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    @Builder.Default
    private InspectionStatus inspectionStatus = InspectionStatus.FAILED_POLICY_CHECK;

    private String notes;
}
