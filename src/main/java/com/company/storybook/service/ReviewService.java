package com.company.storybook.service;

import com.company.storybook.dto.AverageRatingResponse;
import com.company.storybook.dto.FetchReviewsRequest;
import com.company.storybook.dto.ReviewResponse;
import com.company.storybook.dto.ReviewSubmitRequest;
import com.company.storybook.exception.StoryBookException;

import java.util.List;

public interface ReviewService {

    /**
     * Submit a new review for a storybook
     */
    ReviewResponse submitReview(ReviewSubmitRequest request) throws StoryBookException;

    /**
     * Fetch all reviews for a storybook
     */
    List<ReviewResponse> fetchReviewsByStorybook(FetchReviewsRequest request) throws StoryBookException;

    /**
     * Get average rating for a storybook
     */
    AverageRatingResponse getAverageRating(Long storyBookId) throws StoryBookException;
}
