package com.rora.backend.settings.repository;

import com.rora.backend.settings.entity.StoreSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreSettingRepository extends JpaRepository<StoreSetting, String> {
    Optional<StoreSetting> findBySettingKey(String settingKey);
    boolean existsBySettingKey(String settingKey);
}
