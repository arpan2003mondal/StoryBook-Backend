package com.company.storybook.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemDTO> cartItems;
    private Integer totalItems;
    private BigDecimal totalPrice;
}
