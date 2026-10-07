package com.rora.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RoraBackendApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RoraBackendApplication.class);
        app.addInitializers(new com.rora.backend.config.DatasourceDiagnosticInitializer());
        app.run(args);
    }
}
