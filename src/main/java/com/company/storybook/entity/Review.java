package com.company.storybook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    private Storybook storybook;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 scale

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review(User user, Storybook storybook, Integer rating, String reviewText) {
        this.user = user;
        this.storybook = storybook;
        this.rating = rating;
        this.reviewText = reviewText;
    }
}
