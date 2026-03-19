package com.company.storybook.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StorybookRequest {

    @NotBlank(message = "{storybook.title.required}")
    private String title;

    private String description;

    @NotNull(message = "{storybook.author.required}")
    private Long authorId;

    @NotNull(message = "{storybook.category.required}")
    private Long categoryId;

    @NotNull(message = "{storybook.price.required}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{storybook.price.invalid}")
    private BigDecimal price;

    @NotBlank(message = "{storybook.audio.url.required}")
    private String audioUrl;

    private String sampleAudioUrl;

    private String coverImageUrl;
}
