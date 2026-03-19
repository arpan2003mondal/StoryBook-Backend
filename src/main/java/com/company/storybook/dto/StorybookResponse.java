package com.company.storybook.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StorybookResponse {

    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private String audioUrl;
    private String sampleAudioUrl;
    private String coverImageUrl;
    private LocalDateTime createdAt;
}
