package com.rora.backend.cms.repository;

import com.rora.backend.cms.entity.JournalArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JournalArticleRepository extends JpaRepository<JournalArticle, String> {

    Optional<JournalArticle> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<JournalArticle> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    List<JournalArticle> findAllByOrderByCreatedAtDesc();

    @Query("SELECT j FROM JournalArticle j WHERE " +
           "(:category IS NULL OR :category = '' OR :category = 'all' OR LOWER(j.category) = LOWER(:category)) AND " +
           "(:query IS NULL OR :query = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.excerpt) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY j.createdAt DESC")
    Page<JournalArticle> searchArticles(@Param("category") String category,
                                        @Param("query") String query,
                                        Pageable pageable);
}
