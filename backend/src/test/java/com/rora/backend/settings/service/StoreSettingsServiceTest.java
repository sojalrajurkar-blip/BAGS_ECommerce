package com.rora.backend.settings.service;

import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.settings.dto.SingleSettingDto;
import com.rora.backend.settings.dto.StoreSettingsDto;
import com.rora.backend.settings.dto.UpdateSingleSettingRequest;
import com.rora.backend.settings.dto.UpdateStoreSettingsRequest;
import com.rora.backend.settings.entity.StoreSetting;
import com.rora.backend.settings.repository.StoreSettingRepository;
import com.rora.backend.settings.service.impl.StoreSettingsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreSettingsServiceTest {

    @Mock
    private StoreSettingRepository storeSettingRepository;

    @InjectMocks
    private StoreSettingsServiceImpl storeSettingsService;

    @Test
    @DisplayName("Should return defaults when settings not found in database")
    void testGetStoreSettings_Defaults() {
        when(storeSettingRepository.findBySettingKey(anyString())).thenReturn(Optional.empty());

        StoreSettingsDto settings = storeSettingsService.getStoreSettings();

        assertThat(settings.getStoreName()).isEqualTo("RÓRA Studios");
        assertThat(settings.getFreeShippingThreshold()).isEqualTo(1999.0);
        assertThat(settings.getStandardShippingFee()).isEqualTo(199.0);
        assertThat(settings.getCurrency()).isEqualTo("INR (₹)");
    }

    @Test
    @DisplayName("Should return stored values from database")
    void testGetStoreSettings_FromDatabase() {
        lenient().when(storeSettingRepository.findBySettingKey("storeName")).thenReturn(Optional.of(
                StoreSetting.builder().settingKey("storeName").settingValue("RÓRA Atelier Luxe").build()));
        lenient().when(storeSettingRepository.findBySettingKey("freeShippingThreshold")).thenReturn(Optional.of(
                StoreSetting.builder().settingKey("freeShippingThreshold").settingValue("2499").build()));

        StoreSettingsDto settings = storeSettingsService.getStoreSettings();

        assertThat(settings.getStoreName()).isEqualTo("RÓRA Atelier Luxe");
        assertThat(settings.getFreeShippingThreshold()).isEqualTo(2499.0);
    }

    @Test
    @DisplayName("Should update store settings")
    void testUpdateStoreSettings() {
        UpdateStoreSettingsRequest req = UpdateStoreSettingsRequest.builder()
                .storeName("RÓRA House of Leather")
                .freeShippingThreshold(2999.0)
                .build();

        when(storeSettingRepository.findBySettingKey("storeName")).thenReturn(Optional.empty());
        when(storeSettingRepository.findBySettingKey("freeShippingThreshold")).thenReturn(Optional.empty());
        when(storeSettingRepository.save(any(StoreSetting.class))).thenAnswer(inv -> inv.getArgument(0));

        StoreSettingsDto result = storeSettingsService.updateStoreSettings(req, "admin@rora.com");

        assertThat(result).isNotNull();
        verify(storeSettingRepository, atLeastOnce()).save(any(StoreSetting.class));
    }

    @Test
    @DisplayName("Should update single key-value setting")
    void testUpdateSingleSetting() {
        StoreSetting existing = StoreSetting.builder()
                .id("set-1")
                .settingKey("freeShippingThreshold")
                .settingValue("1999")
                .settingType("NUMBER")
                .updatedAt(Instant.now())
                .build();

        when(storeSettingRepository.findBySettingKey("freeShippingThreshold")).thenReturn(Optional.of(existing));
        when(storeSettingRepository.save(any(StoreSetting.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateSingleSettingRequest req = UpdateSingleSettingRequest.builder()
                .value("2500")
                .type("NUMBER")
                .description("Updated free shipping spend threshold")
                .build();

        SingleSettingDto updated = storeSettingsService.updateSingleSetting("freeShippingThreshold", req, "admin@rora.com");

        assertThat(updated).isNotNull();
        assertThat(updated.getValue()).isEqualTo("2500");
    }

    @Test
    @DisplayName("Should get single setting or throw ResourceNotFoundException")
    void testGetSettingByKey() {
        when(storeSettingRepository.findBySettingKey("unknown_key")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> storeSettingsService.getSettingByKey("unknown_key"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
