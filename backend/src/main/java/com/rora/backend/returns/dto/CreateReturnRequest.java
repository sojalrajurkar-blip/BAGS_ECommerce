package com.rora.backend.returns.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReturnRequest {

    @NotBlank(message = "Order identifier is required")
    private String orderIdOrNumber;

    @NotBlank(message = "Return reason is required")
    private String reason;

    private String customerNotes;

    @Builder.Default
    private List<ReturnItemRequest> items = new ArrayList<>();
}
