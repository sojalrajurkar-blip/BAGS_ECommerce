package com.rora.backend.health;

import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health & Diagnostics", description = "Endpoints for inspecting backend system status, uptime, and database connectivity.")
public class HealthController {

    private final DataSource dataSource;
    private final long startupTime = System.currentTimeMillis();

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    @Operation(summary = "Check backend system health", description = "Returns operational health metrics, database connection status, and system uptime.")
    public ResponseEntity<ApiResponse<HealthStatus>> checkHealth() {
        boolean dbHealthy = false;
        String dbDetails = "Unavailable";

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                dbHealthy = true;
                dbDetails = "Connected: " + connection.getMetaData().getDatabaseProductName() + " " + connection.getMetaData().getDatabaseProductVersion();
            }
        } catch (Exception e) {
            dbDetails = "Error: " + e.getMessage();
        }

        long uptimeSeconds = (System.currentTimeMillis() - startupTime) / 1000;

        Map<String, Object> systemMetrics = new HashMap<>();
        systemMetrics.put("jvmUptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());
        systemMetrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        systemMetrics.put("totalMemoryBytes", Runtime.getRuntime().totalMemory());
        systemMetrics.put("freeMemoryBytes", Runtime.getRuntime().freeMemory());

        HealthStatus status = HealthStatus.builder()
                .status(dbHealthy ? "UP" : "DEGRADED")
                .application("RÓRA Luxury Bags & Carry Essentials Backend")
                .version("1.0.0")
                .environment("local")
                .uptimeSeconds(uptimeSeconds)
                .databaseStatus(dbHealthy ? "UP" : "DOWN")
                .databaseDetails(dbDetails)
                .systemMetrics(systemMetrics)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("System is fully operational", status));
    }

    @Data
    @Builder
    public static class HealthStatus {
        private String status;
        private String application;
        private String version;
        private String environment;
        private long uptimeSeconds;
        private String databaseStatus;
        private String databaseDetails;
        private Map<String, Object> systemMetrics;
        private Instant timestamp;
    }
}
