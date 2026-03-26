package com.company.storybook.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long reviewId;
    private String userName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime createdAt;

    public ReviewResponse(Long reviewId, String userName, Integer rating, String reviewText, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.userName = userName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
    }
}
