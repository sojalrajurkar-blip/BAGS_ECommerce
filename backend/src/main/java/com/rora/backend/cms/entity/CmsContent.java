package com.rora.backend.cms.entity;

import com.rora.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cms_content")
public class CmsContent extends BaseEntity {

    @Column(name = "content_key", length = 128, nullable = false, unique = true)
    private String contentKey;

    @Column(name = "title", length = 255)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "content_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> contentData;
}
