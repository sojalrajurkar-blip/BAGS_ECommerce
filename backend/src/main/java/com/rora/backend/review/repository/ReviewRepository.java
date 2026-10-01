package com.rora.backend.review.repository;

import com.rora.backend.review.entity.Review;
import com.rora.backend.review.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String>, JpaSpecificationExecutor<Review> {

    Page<Review> findByProductIdAndStatus(String productId, ReviewStatus status, Pageable pageable);

    List<Review> findByProductIdAndStatus(String productId, ReviewStatus status);

    List<Review> findByIsFeaturedTrueAndStatus(ReviewStatus status);

    List<Review> findByUserId(String userId);

    long countByProductIdAndStatus(String productId, ReviewStatus status);

    long countByStatus(ReviewStatus status);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'PUBLISHED'")
    Double getAverageRatingForProduct(@Param("productId") String productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.status = 'PUBLISHED'")
    Double getGlobalAverageRating();

    @Query("SELECT r FROM Review r WHERE " +
            "(:status IS NULL OR r.status = :status) AND (" +
            ":query IS NULL OR :query = '' OR " +
            "LOWER(r.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.productName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(r.comment) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Review> searchReviews(
            @Param("query") String query,
            @Param("status") ReviewStatus status,
            Pageable pageable
    );
}
