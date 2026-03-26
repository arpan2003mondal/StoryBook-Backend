package com.company.storybook.repository;

import com.company.storybook.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find all reviews for a specific storybook
     */
    List<Review> findByStorybookId(Long storyBookId);

    /**
     * Get average rating for a storybook
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.storybook.id = :storyBookId")
    Double getAverageRatingByStorybookId(@Param("storyBookId") Long storyBookId);

    /**
     * Get total review count for a storybook
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.storybook.id = :storyBookId")
    Long getTotalReviewCountByStorybookId(@Param("storyBookId") Long storyBookId);
}
