package com.company.storybook.service;

import com.company.storybook.dto.OrderResponseDTO;
import com.company.storybook.exception.StoryBookException;

public interface OrderService {
    
    /**
     * Checkout from cart - process payment using wallet
     */
    OrderResponseDTO checkout(Long userId) throws StoryBookException;
}
