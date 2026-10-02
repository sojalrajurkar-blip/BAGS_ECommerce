package com.rora.backend.settings.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.settings.dto.StoreSettingsDto;
import com.rora.backend.settings.service.StoreSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Store Settings", description = "Public store operational parameters, shipping thresholds, and contact information")
public class SettingsController {

    private final StoreSettingsService storeSettingsService;

    @GetMapping
    @Operation(summary = "Get Store Settings", description = "Retrieve public store configuration parameters")
    public ResponseEntity<ApiResponse<StoreSettingsDto>> getStoreSettings() {
        StoreSettingsDto settings = storeSettingsService.getStoreSettings();
        return ResponseEntity.ok(ApiResponse.success("Store settings retrieved successfully", settings));
    }

    @GetMapping("/public")
    @Operation(summary = "Get Public Operational Settings", description = "Alias endpoint for public checkout and navigation settings")
    public ResponseEntity<ApiResponse<StoreSettingsDto>> getPublicStoreSettings() {
        StoreSettingsDto settings = storeSettingsService.getStoreSettings();
        return ResponseEntity.ok(ApiResponse.success("Public store settings retrieved successfully", settings));
    }
}
