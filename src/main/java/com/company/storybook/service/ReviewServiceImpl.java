package com.company.storybook.service;

import com.company.storybook.dto.AverageRatingResponse;
import com.company.storybook.dto.FetchReviewsRequest;
import com.company.storybook.dto.ReviewResponse;
import com.company.storybook.dto.ReviewSubmitRequest;
import com.company.storybook.entity.Review;
import com.company.storybook.entity.Storybook;
import com.company.storybook.entity.User;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.ReviewRepository;
import com.company.storybook.repository.StorybookRepository;
import com.company.storybook.repository.UserLibraryRepository;
import com.company.storybook.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorybookRepository storybookRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Submit a new review for a storybook
     * User must have purchased the storybook (must be in their library)
     */
    @Override
    public ReviewResponse submitReview(ReviewSubmitRequest request) throws StoryBookException {
        // Validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            String errorMsg = messageSource.getMessage("review.rating.invalid", null, Locale.getDefault());
            throw new StoryBookException(errorMsg);
        }

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("user.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Check if storybook exists
        Storybook storybook = storybookRepository.findById(request.getStoryBookId())
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("storybook.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Check if storybook is in user's library (user must have purchased it)
        boolean isInLibrary = userLibraryRepository.existsByUserIdAndStorybookId(request.getUserId(), request.getStoryBookId());
        if (!isInLibrary) {
            String errorMsg = messageSource.getMessage("review.storybook.not.in.library", null, Locale.getDefault());
            throw new StoryBookException(errorMsg);
        }

        // Create new review
        Review review = new Review(user, storybook, request.getRating(), request.getReviewText());
        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    /**
     * Fetch all reviews for a storybook
     */
    @Override
    public List<ReviewResponse> fetchReviewsByStorybook(FetchReviewsRequest request) throws StoryBookException {
        // Check if storybook exists
        if (!storybookRepository.existsById(request.getStoryBookId())) {
            String errorMsg = messageSource.getMessage("storybook.not.found", null, Locale.getDefault());
            throw new StoryBookException(errorMsg);
        }

        List<Review> reviews = reviewRepository.findByStorybookId(request.getStoryBookId());
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get average rating for a storybook
     */
    @Override
    public AverageRatingResponse getAverageRating(Long storyBookId) throws StoryBookException {
        // Check if storybook exists
        if (!storybookRepository.existsById(storyBookId)) {
            String errorMsg = messageSource.getMessage("storybook.not.found", null, Locale.getDefault());
            throw new StoryBookException(errorMsg);
        }

        Double averageRating = reviewRepository.getAverageRatingByStorybookId(storyBookId);
        Long totalReviews = reviewRepository.getTotalReviewCountByStorybookId(storyBookId);

        // Return 0.0 if no reviews exist
        if (averageRating == null) {
            averageRating = 0.0;
        }

        return new AverageRatingResponse(storyBookId, averageRating, totalReviews);
    }

    /**
     * Convert Review entity to ReviewResponse DTO
     */
    private ReviewResponse convertToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getUser().getName(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt()
        );
    }
}
