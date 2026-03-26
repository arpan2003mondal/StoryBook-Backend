package com.company.storybook.dto;

import lombok.Data;

import java.util.List;

@Data
public class WishlistResponseDTO {
    private List<WishlistItemDTO> wishlistItems;
    private Integer totalItems;
}
