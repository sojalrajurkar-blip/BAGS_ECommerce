package com.rora.backend.returns.dto;

import com.rora.backend.returns.entity.InspectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveReturnRequest {

    @Builder.Default
    private InspectionStatus inspectionStatus = InspectionStatus.PASSED_PRISTINE;

    private String notes;

    @Builder.Default
    private boolean autoRefund = true;

    private BigDecimal customRefundAmount;

    @Builder.Default
    private String refundMethod = "Original Payment Source";

    @Builder.Default
    private boolean restockInventory = true;
}
