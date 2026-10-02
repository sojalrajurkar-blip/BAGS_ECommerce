package com.rora.backend.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.admin.dto.CreateAdminRoleRequest;
import com.rora.backend.admin.dto.CreateAdminUserRequest;
import com.rora.backend.admin.dto.CreateAuditLogRequest;
import com.rora.backend.admin.dto.UpdateAdminUserRequest;
import com.rora.backend.admin.entity.AuditLog;
import com.rora.backend.admin.repository.AuditLogRepository;
import com.rora.backend.user.Role;
import com.rora.backend.user.RoleRepository;
import com.rora.backend.user.User;
import com.rora.backend.user.UserRepository;
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

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminPlatformIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role adminRole;
    private User testAdminUser;

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();

        adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().id("role-admin").name("ROLE_ADMIN").description("Super Admin").build()));

        testAdminUser = userRepository.findByEmail("admin-test@rorastudios.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .email("admin-test@rorastudios.com")
                        .name("Test Admin")
                        .passwordHash("hashed")
                        .status("ACTIVE")
                        .roles(Set.of(adminRole))
                        .build()));

        auditLogRepository.save(AuditLog.builder()
                .action("Product Price Updated")
                .actor("Sarah Jenkins (Super Admin)")
                .target("The Nomad Backpack")
                .entityType("Product")
                .status("SUCCESS")
                .severity("Info")
                .build());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/dashboard/summary - Should return dashboard summary KPIs")
    void testGetDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRevenue").exists())
                .andExpect(jsonPath("$.data.salesOverview").exists())
                .andExpect(jsonPath("$.data.lowStockCount").exists());
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/dashboard/overview - Should return sales overview and category breakdown")
    void testGetSalesOverview() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.monthlyRevenue").value("₹28,45,900"))
                .andExpect(jsonPath("$.data.categoryBreakdown", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/audit-logs - Should return list of audit logs")
    void testGetAuditLogs() throws Exception {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].action").value("Product Price Updated"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/audit-logs - Should record custom audit event")
    void testRecordAuditEvent() throws Exception {
        CreateAuditLogRequest req = CreateAuditLogRequest.builder()
                .action("Coupon Code Created")
                .actor("Kabir Verma")
                .target("SUMMER10")
                .entityType("Coupon")
                .severity("Info")
                .status("SUCCESS")
                .build();

        mockMvc.perform(post("/api/v1/admin/audit-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.action").value("Coupon Code Created"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/users - Should list backoffice admin team members")
    void testGetAllAdminUsers() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/users - Should create new admin team user")
    void testCreateAdminUser() throws Exception {
        CreateAdminUserRequest req = CreateAdminUserRequest.builder()
                .name("Helena Berg")
                .email("helena.b@rorastudios.com")
                .password("StrongPass123!")
                .role("ROLE_MANAGER")
                .build();

        mockMvc.perform(post("/api/v1/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("helena.b@rorastudios.com"))
                .andExpect(jsonPath("$.data.name").value("Helena Berg"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("GET /api/v1/admin/roles - Should list all roles and permissions")
    void testGetAllRoles() throws Exception {
        mockMvc.perform(get("/api/v1/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/admin/dashboard/summary - Unauthorized requests should receive 401")
    void testUnauthorizedDashboardAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard/summary"))
                .andExpect(status().isUnauthorized());
    }
}
