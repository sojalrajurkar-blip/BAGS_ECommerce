package com.rora.backend.settings.service.impl;

import com.rora.backend.common.exception.ResourceNotFoundException;
import com.rora.backend.settings.dto.SingleSettingDto;
import com.rora.backend.settings.dto.StoreSettingsDto;
import com.rora.backend.settings.dto.UpdateSingleSettingRequest;
import com.rora.backend.settings.dto.UpdateStoreSettingsRequest;
import com.rora.backend.settings.entity.StoreSetting;
import com.rora.backend.settings.repository.StoreSettingRepository;
import com.rora.backend.settings.service.StoreSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreSettingsServiceImpl implements StoreSettingsService {

    public static final String KEY_STORE_NAME = "storeName";
    public static final String KEY_TAGLINE = "tagline";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_SUPPORT_EMAIL = "supportEmail";
    public static final String KEY_SUPPORT_PHONE = "supportPhone";
    public static final String KEY_WAREHOUSE_ADDRESS = "warehouseAddress";
    public static final String KEY_FREE_SHIPPING_THRESHOLD = "freeShippingThreshold";
    public static final String KEY_STANDARD_SHIPPING_FEE = "standardShippingFee";
    public static final String KEY_TAX_RATE = "taxRate";
    public static final String KEY_ORDER_PREFIX = "orderPrefix";
    public static final String KEY_INVENTORY_ALERT_THRESHOLD = "inventoryAlertThreshold";

    private final StoreSettingRepository storeSettingRepository;

    @Override
    @Transactional(readOnly = true)
    public StoreSettingsDto getStoreSettings() {
        return StoreSettingsDto.builder()
                .storeName(getSettingValue(KEY_STORE_NAME, "RÓRA Studios"))
                .tagline(getSettingValue(KEY_TAGLINE, "Thoughtfully Designed Bags for Modern Journeys"))
                .currency(getSettingValue(KEY_CURRENCY, "INR (₹)"))
                .supportEmail(getSettingValue(KEY_SUPPORT_EMAIL, "concierge@rorastudios.com"))
                .supportPhone(getSettingValue(KEY_SUPPORT_PHONE, "+91 22 6944 8000"))
                .warehouseAddress(getSettingValue(KEY_WAREHOUSE_ADDRESS, "Studio 4B, Mathuradas Mills Compound, Lower Parel, Mumbai 400013, India"))
                .freeShippingThreshold(getDoubleSetting(KEY_FREE_SHIPPING_THRESHOLD, 1999.0))
                .standardShippingFee(getDoubleSetting(KEY_STANDARD_SHIPPING_FEE, 199.0))
                .taxRate(getSettingValue(KEY_TAX_RATE, "18% GST (Included in MRP)"))
                .orderPrefix(getSettingValue(KEY_ORDER_PREFIX, "RRA"))
                .inventoryAlertThreshold(getIntSetting(KEY_INVENTORY_ALERT_THRESHOLD, 5))
                .updatedAt(Instant.now())
                .build();
    }

    @Override
    @Transactional
    public StoreSettingsDto updateStoreSettings(UpdateStoreSettingsRequest request, String adminEmail) {
        log.info("Updating store settings by admin: {}", adminEmail);

        if (request.getStoreName() != null) {
            saveOrUpdateSetting(KEY_STORE_NAME, request.getStoreName(), "STRING", "Store display brand name");
        }
        if (request.getTagline() != null) {
            saveOrUpdateSetting(KEY_TAGLINE, request.getTagline(), "STRING", "Brand marketing tagline");
        }
        if (request.getCurrency() != null) {
            saveOrUpdateSetting(KEY_CURRENCY, request.getCurrency(), "STRING", "Store primary currency");
        }
        if (request.getSupportEmail() != null) {
            saveOrUpdateSetting(KEY_SUPPORT_EMAIL, request.getSupportEmail(), "STRING", "Customer concierge support email");
        }
        if (request.getSupportPhone() != null) {
            saveOrUpdateSetting(KEY_SUPPORT_PHONE, request.getSupportPhone(), "STRING", "Customer support phone hotline");
        }
        if (request.getWarehouseAddress() != null) {
            saveOrUpdateSetting(KEY_WAREHOUSE_ADDRESS, request.getWarehouseAddress(), "STRING", "Primary fulfillment atelier address");
        }
        if (request.getFreeShippingThreshold() != null) {
            saveOrUpdateSetting(KEY_FREE_SHIPPING_THRESHOLD, String.valueOf(request.getFreeShippingThreshold()), "NUMBER", "Complimentary shipping order spend threshold");
        }
        if (request.getStandardShippingFee() != null) {
            saveOrUpdateSetting(KEY_STANDARD_SHIPPING_FEE, String.valueOf(request.getStandardShippingFee()), "NUMBER", "Standard nationwide shipping fee");
        }
        if (request.getTaxRate() != null) {
            saveOrUpdateSetting(KEY_TAX_RATE, request.getTaxRate(), "STRING", "Applicable GST rate explanation");
        }
        if (request.getOrderPrefix() != null) {
            saveOrUpdateSetting(KEY_ORDER_PREFIX, request.getOrderPrefix(), "STRING", "Canonical order ID prefix");
        }
        if (request.getInventoryAlertThreshold() != null) {
            saveOrUpdateSetting(KEY_INVENTORY_ALERT_THRESHOLD, String.valueOf(request.getInventoryAlertThreshold()), "NUMBER", "Low stock inventory warning threshold");
        }

        return getStoreSettings();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SingleSettingDto> getAllSettings() {
        return storeSettingRepository.findAll().stream()
                .map(this::mapToSingleSettingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SingleSettingDto getSettingByKey(String key) {
        StoreSetting setting = storeSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Store setting not found for key: " + key));
        return mapToSingleSettingDto(setting);
    }

    @Override
    @Transactional
    public SingleSettingDto updateSingleSetting(String key, UpdateSingleSettingRequest request, String adminEmail) {
        log.info("Updating setting key '{}' by admin: {}", key, adminEmail);

        StoreSetting setting = storeSettingRepository.findBySettingKey(key)
                .orElse(StoreSetting.builder()
                        .settingKey(key)
                        .settingType(request.getType() != null ? request.getType() : "STRING")
                        .description(request.getDescription())
                        .build());

        setting.setSettingValue(request.getValue());
        if (request.getType() != null) {
            setting.setSettingType(request.getType());
        }
        if (request.getDescription() != null) {
            setting.setDescription(request.getDescription());
        }

        StoreSetting saved = storeSettingRepository.save(setting);
        return mapToSingleSettingDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public String getSettingValue(String key, String defaultValue) {
        return storeSettingRepository.findBySettingKey(key)
                .map(StoreSetting::getSettingValue)
                .orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getDoubleSetting(String key, Double defaultValue) {
        return storeSettingRepository.findBySettingKey(key)
                .map(s -> {
                    try {
                        return Double.parseDouble(s.getSettingValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getIntSetting(String key, Integer defaultValue) {
        return storeSettingRepository.findBySettingKey(key)
                .map(s -> {
                    try {
                        return Integer.parseInt(s.getSettingValue());
                    } catch (NumberFormatException e) {
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    private void saveOrUpdateSetting(String key, String value, String type, String description) {
        Optional<StoreSetting> existing = storeSettingRepository.findBySettingKey(key);
        if (existing.isPresent()) {
            StoreSetting s = existing.get();
            s.setSettingValue(value);
            if (type != null) s.setSettingType(type);
            if (description != null) s.setDescription(description);
            storeSettingRepository.save(s);
        } else {
            StoreSetting s = StoreSetting.builder()
                    .settingKey(key)
                    .settingValue(value)
                    .settingType(type != null ? type : "STRING")
                    .description(description)
                    .build();
            storeSettingRepository.save(s);
        }
    }

    private SingleSettingDto mapToSingleSettingDto(StoreSetting setting) {
        return SingleSettingDto.builder()
                .id(setting.getId())
                .key(setting.getSettingKey())
                .value(setting.getSettingValue())
                .type(setting.getSettingType())
                .description(setting.getDescription())
                .updatedAt(setting.getUpdatedAt())
                .build();
    }
}
