package com.company.storybook.dto;

import lombok.Data;

@Data
public class AverageRatingResponse {
    private Long storyBookId;
    private Double averageRating;
    private Long totalReviews;

    public AverageRatingResponse(Long storyBookId, Double averageRating, Long totalReviews) {
        this.storyBookId = storyBookId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }
}
