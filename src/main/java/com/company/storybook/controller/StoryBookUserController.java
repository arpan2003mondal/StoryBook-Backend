package com.company.storybook.controller;

import com.company.storybook.dto.*;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.service.ReviewService;
import com.company.storybook.service.StorybookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Storybook Controller - Handles all storybook-related operations
 * Endpoints: /storybooks/*
 */
@RestController
@RequestMapping("/storybooks")
public class StoryBookUserController {

    @Autowired
    private StorybookService storybookService;

    @Autowired
    private ReviewService reviewService;

    /**
     * Get all storybooks
     * GET /storybooks
     */
    @GetMapping
    public ResponseEntity<List<StorybookResponse>> getAllStorybooks() {
        List<StorybookResponse> storybooks = storybookService.getAllStorybooks();
        return ResponseEntity.ok(storybooks);
    }

    /**
     * Search storybooks by keyword (title, author, genre/category)
     * Must be before /{id} to avoid routing conflicts
     * GET /storybooks/search?keyword=search-term
     */
    @GetMapping("/search")
    public ResponseEntity<List<StorybookResponse>> searchStorybooks(
            @RequestParam(required = false) String keyword) {
        List<StorybookResponse> results = storybookService.searchStorybooks(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * Get storybook by ID
     * GET /storybooks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StorybookResponse> getStorybookById(@PathVariable Long id) throws StoryBookException {
        StorybookResponse storybook = storybookService.getStorybookById(id);
        return ResponseEntity.ok(storybook);
    }

    // ==================== REVIEW AND RATING ENDPOINTS ====================

    /**
     * Submit a new review for a storybook
     * POST /storybooks/reviews/add
     */
    @PostMapping("/reviews/add")
    public ResponseEntity<ReviewResponse> submitReview(@RequestBody ReviewSubmitRequest request) throws StoryBookException {
        ReviewResponse response = reviewService.submitReview(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetch all reviews for a specific storybook
     * POST /storybooks/reviews/fetch
     */
    @PostMapping("/reviews/fetch")
    public ResponseEntity<List<ReviewResponse>> fetchReviews(@RequestBody FetchReviewsRequest request) throws StoryBookException {
        List<ReviewResponse> reviews = reviewService.fetchReviewsByStorybook(request);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get average rating for a storybook
     * GET /storybooks/reviews/rating/{storyBookId}
     */
    @GetMapping("/reviews/rating/{storyBookId}")
    public ResponseEntity<AverageRatingResponse> getAverageRating(@PathVariable Long storyBookId) throws StoryBookException {
        AverageRatingResponse response = reviewService.getAverageRating(storyBookId);
        return ResponseEntity.ok(response);
    }
}


