package com.company.storybook.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long storybookId;
    private String title;
    private String description;
    private String authorName;
    private String categoryName;
    private BigDecimal price;
    private String coverImageUrl;
    private Integer quantity;
}
