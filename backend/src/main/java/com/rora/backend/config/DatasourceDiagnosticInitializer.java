package com.rora.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diagnostic initializer that runs BEFORE HikariCP and Flyway initialize.
 * Safely logs non-secret connection parameters to isolate Render environment configuration.
 * NEVER logs passwords or credentials.
 */
public class DatasourceDiagnosticInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(DatasourceDiagnosticInitializer.class);
    private static final Pattern JDBC_PATTERN = Pattern.compile("^jdbc:postgresql://([^:/]+)(?::(\\d+))?/([^?]+)(?:\\?(.*))?$");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        String activeProfiles = String.join(",", env.getActiveProfiles());
        String rawUrl = env.getProperty("spring.datasource.url");
        String rawUsername = env.getProperty("spring.datasource.username");
        String dbUrlEnv = env.getProperty("DB_URL");
        String springDsUrlEnv = env.getProperty("SPRING_DATASOURCE_URL");
        String databaseUrlEnv = env.getProperty("DATABASE_URL");

        logger.info("================================================================================");
        logger.info("[STARTUP-DIAGNOSTIC] Active Spring Profiles: [{}]", activeProfiles.isBlank() ? "default" : activeProfiles);

        if (springDsUrlEnv != null && !springDsUrlEnv.isBlank()) {
            logger.warn("[STARTUP-DIAGNOSTIC] Detected SPRING_DATASOURCE_URL in environment: host={}", extractHostSafely(springDsUrlEnv));
        }
        if (databaseUrlEnv != null && !databaseUrlEnv.isBlank()) {
            logger.warn("[STARTUP-DIAGNOSTIC] Detected DATABASE_URL in environment: host={}", extractHostSafely(databaseUrlEnv));
        }
        if (dbUrlEnv != null && !dbUrlEnv.isBlank()) {
            logger.info("[STARTUP-DIAGNOSTIC] Detected DB_URL in environment: host={}", extractHostSafely(dbUrlEnv));
        }

        if (rawUrl != null) {
            Matcher matcher = JDBC_PATTERN.matcher(rawUrl);
            if (matcher.find()) {
                String host = matcher.group(1);
                String port = matcher.group(2) != null ? matcher.group(2) : "5432";
                String dbName = matcher.group(3);
                String queryParams = matcher.group(4) != null ? matcher.group(4) : "none";

                logger.info("[STARTUP-DIAGNOSTIC] Effective Datasource Target: host={}, port={}, database={}", host, port, dbName);
                logger.info("[STARTUP-DIAGNOSTIC] Effective JDBC Query Params: {}", sanitizeQueryParams(queryParams));
            } else {
                logger.info("[STARTUP-DIAGNOSTIC] Effective Datasource URL format: host={}", extractHostSafely(rawUrl));
            }
        } else {
            logger.error("[STARTUP-DIAGNOSTIC] No spring.datasource.url resolved!");
        }

        if (rawUsername != null) {
            boolean hasTenantSuffix = rawUsername.contains(".");
            logger.info("[STARTUP-DIAGNOSTIC] Effective Username: {} (Tenant-qualified: {})", rawUsername, hasTenantSuffix);
        } else {
            logger.error("[STARTUP-DIAGNOSTIC] No spring.datasource.username resolved!");
        }
        logger.info("================================================================================");
    }

    private String extractHostSafely(String url) {
        try {
            if (url.startsWith("jdbc:postgresql://")) {
                url = url.substring(18);
            } else if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
                URI uri = URI.create(url);
                return uri.getHost() != null ? uri.getHost() : "unknown";
            }
            int slashIndex = url.indexOf('/');
            String hostPort = slashIndex != -1 ? url.substring(0, slashIndex) : url;
            int atIndex = hostPort.indexOf('@');
            if (atIndex != -1) {
                hostPort = hostPort.substring(atIndex + 1);
            }
            return hostPort;
        } catch (Exception e) {
            return "parse_error";
        }
    }

    private String sanitizeQueryParams(String query) {
        if (query == null || query.isBlank()) {
            return "none";
        }
        String[] pairs = query.split("&");
        StringBuilder sb = new StringBuilder();
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = kv[0];
            String val = kv.length > 1 ? kv[1] : "";
            if (key.equalsIgnoreCase("password") || key.equalsIgnoreCase("secret")) {
                val = "***";
            }
            if (sb.length() > 0) sb.append("&");
            sb.append(key).append("=").append(val);
        }
        return sb.toString();
    }
}
