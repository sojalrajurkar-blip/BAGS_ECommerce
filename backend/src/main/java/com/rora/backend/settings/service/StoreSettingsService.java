package com.rora.backend.settings.service;

import com.rora.backend.settings.dto.SingleSettingDto;
import com.rora.backend.settings.dto.StoreSettingsDto;
import com.rora.backend.settings.dto.UpdateSingleSettingRequest;
import com.rora.backend.settings.dto.UpdateStoreSettingsRequest;

import java.util.List;

public interface StoreSettingsService {

    StoreSettingsDto getStoreSettings();

    StoreSettingsDto updateStoreSettings(UpdateStoreSettingsRequest request, String adminEmail);

    List<SingleSettingDto> getAllSettings();

    SingleSettingDto getSettingByKey(String key);

    SingleSettingDto updateSingleSetting(String key, UpdateSingleSettingRequest request, String adminEmail);

    String getSettingValue(String key, String defaultValue);

    Double getDoubleSetting(String key, Double defaultValue);

    Integer getIntSetting(String key, Integer defaultValue);
}
