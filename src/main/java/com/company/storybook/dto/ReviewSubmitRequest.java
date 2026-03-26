package com.company.storybook.dto;

import lombok.Data;

@Data
public class ReviewSubmitRequest {
    private Long userId;
    private Long storyBookId;
    private Integer rating; // 1-5 scale
    private String reviewText;
}
