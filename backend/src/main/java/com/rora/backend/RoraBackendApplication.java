package com.rora.backend;

import com.rora.backend.config.DatasourceDiagnosticInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RoraBackendApplication {

    private static final Pattern JDBC_PATTERN = Pattern.compile("^jdbc:postgresql://([^:/]+)(?::(\\d+))?/([^?]+)(?:\\?(.*))?$");

    public static void main(String[] args) {
        // Guaranteed raw console diagnostic executed before any Spring subsystem loads
        printEarlyDiagnostics();

        SpringApplication app = new SpringApplication(RoraBackendApplication.class);
        app.addInitializers(new DatasourceDiagnosticInitializer());
        app.run(args);
    }

    private static void printEarlyDiagnostics() {
        System.out.println("================================================================================");
        System.out.println("[STARTUP-DIAGNOSTIC] RÓRA Backend Pre-Flight Environment Inspection");

        String activeProfiles = System.getenv("SPRING_PROFILES_ACTIVE");
        System.out.println("[STARTUP-DIAGNOSTIC] Active Spring Profiles: [" + (activeProfiles != null ? activeProfiles : "not set (defaults to dev)") + "]");

        String dbUrl = System.getenv("DB_URL");
        String springDsUrl = System.getenv("SPRING_DATASOURCE_URL");
        String databaseUrl = System.getenv("DATABASE_URL");
        String dbUsername = System.getenv("DB_USERNAME");
        String springDsUsername = System.getenv("SPRING_DATASOURCE_USERNAME");

        if (springDsUrl != null && !springDsUrl.isBlank()) {
            System.out.println("[STARTUP-DIAGNOSTIC] Detected SPRING_DATASOURCE_URL in environment: host=" + extractHostSafely(springDsUrl));
        }
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            System.out.println("[STARTUP-DIAGNOSTIC] Detected DATABASE_URL in environment: host=" + extractHostSafely(databaseUrl));
        }

        if (dbUrl != null && !dbUrl.isBlank()) {
            Matcher matcher = JDBC_PATTERN.matcher(dbUrl);
            if (matcher.find()) {
                String host = matcher.group(1);
                String port = matcher.group(2) != null ? matcher.group(2) : "5432";
                String dbName = matcher.group(3);
                String queryParams = matcher.group(4) != null ? matcher.group(4) : "none";

                System.out.println("[STARTUP-DIAGNOSTIC] DB_URL host: " + host);
                System.out.println("[STARTUP-DIAGNOSTIC] DB_URL port: " + port);
                System.out.println("[STARTUP-DIAGNOSTIC] Database: " + dbName);
                System.out.println("[STARTUP-DIAGNOSTIC] SSL mode / query params: " + sanitizeQueryParams(queryParams));
            } else {
                System.out.println("[STARTUP-DIAGNOSTIC] DB_URL raw host: " + extractHostSafely(dbUrl));
            }
        } else {
            System.out.println("[STARTUP-DIAGNOSTIC] DB_URL is NOT set in environment (will use profile fallback)!");
        }

        String effectiveUser = dbUsername != null ? dbUsername : springDsUsername;
        if (effectiveUser != null && !effectiveUser.isBlank()) {
            boolean isTenantQualified = effectiveUser.contains(".");
            System.out.println("[STARTUP-DIAGNOSTIC] Username: " + effectiveUser + " (Tenant-qualified: " + isTenantQualified + ")");
        } else {
            System.out.println("[STARTUP-DIAGNOSTIC] Username is NOT set in environment (will use profile fallback)!");
        }

        System.out.println("================================================================================");
        System.out.flush();
    }

    private static String extractHostSafely(String url) {
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

    private static String sanitizeQueryParams(String query) {
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
