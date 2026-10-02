package com.rora.backend.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreSettingsRequest {
    private String storeName;
    private String tagline;
    private String currency;
    private String supportEmail;
    private String supportPhone;
    private String warehouseAddress;
    private Double freeShippingThreshold;
    private Double standardShippingFee;
    private String taxRate;
    private String orderPrefix;
    private Integer inventoryAlertThreshold;
}
