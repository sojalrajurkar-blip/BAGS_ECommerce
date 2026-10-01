package com.rora.backend.returns.dto;

import com.rora.backend.returns.entity.InspectionStatus;
import com.rora.backend.returns.entity.ReturnStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRecordDto {

    private String id;
    private String orderId;
    private String orderNumber;
    private String customer;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String item;
    private String reason;
    private String customerNotes;
    private String inspectionStatus;
    private InspectionStatus inspectionStatusCode;
    private BigDecimal amount;
    private String status;
    private ReturnStatus statusCode;
    private Instant requestDate;
    private String formattedRequestDate;
    private String adminNotes;
    private String refundId;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder.Default
    private List<ReturnItemDto> items = new ArrayList<>();
}
