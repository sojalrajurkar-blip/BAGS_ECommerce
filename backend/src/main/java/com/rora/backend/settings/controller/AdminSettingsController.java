package com.rora.backend.settings.controller;

import com.rora.backend.common.ApiResponse;
import com.rora.backend.settings.dto.SingleSettingDto;
import com.rora.backend.settings.dto.StoreSettingsDto;
import com.rora.backend.settings.dto.UpdateSingleSettingRequest;
import com.rora.backend.settings.dto.UpdateStoreSettingsRequest;
import com.rora.backend.settings.service.StoreSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@Tag(name = "Admin Store Settings", description = "Backoffice management of store parameters, shipping rates, and key-value configurations")
public class AdminSettingsController {

    private final StoreSettingsService storeSettingsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('SETTINGS_MANAGE')")
    @Operation(summary = "Get Store Settings", description = "Retrieve full structured store settings object")
    public ResponseEntity<ApiResponse<StoreSettingsDto>> getStoreSettings() {
        StoreSettingsDto settings = storeSettingsService.getStoreSettings();
        return ResponseEntity.ok(ApiResponse.success("Store settings retrieved", settings));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('SETTINGS_MANAGE')")
    @Operation(summary = "Update Store Settings", description = "Update store parameters including shipping rules, thresholds, and contact info")
    public ResponseEntity<ApiResponse<StoreSettingsDto>> updateStoreSettings(
            @Valid @RequestBody UpdateStoreSettingsRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        StoreSettingsDto updated = storeSettingsService.updateStoreSettings(request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Store settings updated successfully", updated));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('SETTINGS_MANAGE')")
    @Operation(summary = "Get All Raw Settings", description = "Retrieve complete list of raw key-value store setting records")
    public ResponseEntity<ApiResponse<List<SingleSettingDto>>> getAllRawSettings() {
        List<SingleSettingDto> settings = storeSettingsService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.success("All raw settings retrieved", settings));
    }

    @GetMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('SETTINGS_MANAGE')")
    @Operation(summary = "Get Single Setting by Key", description = "Lookup specific key-value setting record")
    public ResponseEntity<ApiResponse<SingleSettingDto>> getSettingByKey(@PathVariable String key) {
        SingleSettingDto setting = storeSettingsService.getSettingByKey(key);
        return ResponseEntity.ok(ApiResponse.success("Setting retrieved", setting));
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('SETTINGS_MANAGE')")
    @Operation(summary = "Update Single Setting", description = "Update value, type, and description of a specific setting key")
    public ResponseEntity<ApiResponse<SingleSettingDto>> updateSingleSetting(
            @PathVariable String key,
            @Valid @RequestBody UpdateSingleSettingRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        SingleSettingDto updated = storeSettingsService.updateSingleSetting(key, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Setting updated successfully", updated));
    }
}
