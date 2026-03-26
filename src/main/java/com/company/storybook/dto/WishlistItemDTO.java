package com.company.storybook.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WishlistItemDTO {
    private Long wishlistId;
    private Long storybookId;
    private String title;
    private String description;
    private String authorName;
    private String categoryName;
    private BigDecimal price;
    private String audioUrl;
    private String sampleAudioUrl;
    private String coverImageUrl;
    private LocalDateTime addedAt;
}
