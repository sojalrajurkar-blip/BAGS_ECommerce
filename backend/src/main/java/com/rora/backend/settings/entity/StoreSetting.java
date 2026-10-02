package com.rora.backend.settings.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store_settings")
public class StoreSetting {

    @Id
    @Column(name = "id", length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "setting_key", length = 128, nullable = false, unique = true)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT", nullable = false)
    private String settingValue;

    @Column(name = "setting_type", length = 64, nullable = false)
    @Builder.Default
    private String settingType = "STRING";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.updatedAt == null) {
            this.updatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
