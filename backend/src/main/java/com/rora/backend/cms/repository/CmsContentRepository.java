package com.rora.backend.cms.repository;

import com.rora.backend.cms.entity.CmsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CmsContentRepository extends JpaRepository<CmsContent, String> {
    Optional<CmsContent> findByContentKey(String contentKey);
    boolean existsByContentKey(String contentKey);
}
