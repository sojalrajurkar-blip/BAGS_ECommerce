package com.rora.backend.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.settings.dto.UpdateSingleSettingRequest;
import com.rora.backend.settings.dto.UpdateStoreSettingsRequest;
import com.rora.backend.settings.entity.StoreSetting;
import com.rora.backend.settings.repository.StoreSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SettingsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreSettingRepository storeSettingRepository;

    @BeforeEach
    void setUp() {
        storeSettingRepository.deleteAll();

        storeSettingRepository.save(StoreSetting.builder()
                .settingKey("storeName")
                .settingValue("RÓRA Studios")
                .settingType("STRING")
                .description("Store display brand name")
                .build());

        storeSettingRepository.save(StoreSetting.builder()
                .settingKey("freeShippingThreshold")
                .settingValue("1999")
                .settingType("NUMBER")
                .description("Free shipping spend threshold")
                .build());

        storeSettingRepository.save(StoreSetting.builder()
                .settingKey("currency")
                .settingValue("INR (₹)")
                .settingType("STRING")
                .description("Primary currency")
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/settings - Should return public store settings")
    void testGetPublicSettings() throws Exception {
        mockMvc.perform(get("/api/v1/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.storeName").value("RÓRA Studios"))
                .andExpect(jsonPath("$.data.freeShippingThreshold").value(1999.0))
                .andExpect(jsonPath("$.data.currency").value("INR (₹)"));
    }

    @Test
    @DisplayName("GET /api/v1/settings/public - Should return public operational settings")
    void testGetPublicSettingsAlias() throws Exception {
        mockMvc.perform(get("/api/v1/settings/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.storeName").value("RÓRA Studios"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/settings - Admin should retrieve full store settings")
    void testAdminGetSettings() throws Exception {
        mockMvc.perform(get("/api/v1/admin/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.storeName").value("RÓRA Studios"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/settings - Admin should update store settings")
    void testAdminUpdateSettings() throws Exception {
        UpdateStoreSettingsRequest req = UpdateStoreSettingsRequest.builder()
                .storeName("RÓRA Maison Luxe")
                .freeShippingThreshold(2499.0)
                .standardShippingFee(249.0)
                .supportEmail("vip@roramaison.com")
                .build();

        mockMvc.perform(put("/api/v1/admin/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.storeName").value("RÓRA Maison Luxe"))
                .andExpect(jsonPath("$.data.freeShippingThreshold").value(2499.0))
                .andExpect(jsonPath("$.data.supportEmail").value("vip@roramaison.com"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/settings/all - Admin should retrieve raw settings list")
    void testAdminGetAllRawSettings() throws Exception {
        mockMvc.perform(get("/api/v1/admin/settings/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/settings/{key} - Admin should update single setting")
    void testAdminUpdateSingleSetting() throws Exception {
        UpdateSingleSettingRequest req = UpdateSingleSettingRequest.builder()
                .value("3000")
                .type("NUMBER")
                .description("Updated minimum spend for complimentary shipping")
                .build();

        mockMvc.perform(put("/api/v1/admin/settings/freeShippingThreshold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.key").value("freeShippingThreshold"))
                .andExpect(jsonPath("$.data.value").value("3000"));
    }

    @Test
    @DisplayName("PUT /api/v1/admin/settings - Unauthenticated user should receive 401")
    void testUnauthenticatedAdminSettingsAccess() throws Exception {
        UpdateStoreSettingsRequest req = UpdateStoreSettingsRequest.builder()
                .storeName("Hacked Store")
                .build();

        mockMvc.perform(put("/api/v1/admin/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
