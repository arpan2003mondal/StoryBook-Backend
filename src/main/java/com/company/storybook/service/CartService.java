package com.company.storybook.service;

import com.company.storybook.dto.AddToCartRequest;
import com.company.storybook.dto.CartResponseDTO;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;

import java.util.List;

public interface CartService {

    /**
     * Get all storybooks
     */
    List<StorybookResponse> getAllStorybooks();

    /**
     * Get storybook by ID
     */
    StorybookResponse getStorybookById(Long storybookId) throws StoryBookException;

    /**
     * Search storybooks by keyword (title, author, description, category)
     */
    List<StorybookResponse> searchStorybooks(String keyword);

    /**
     * Add storybook to cart for logged-in user
     */
    CartResponseDTO addToCart(Long userId, AddToCartRequest request) throws StoryBookException;

    /**
     * Remove storybook from cart
     */
    CartResponseDTO removeFromCart(Long userId, Long cartItemId) throws StoryBookException;

    /**
     * Get user's cart
     */
    CartResponseDTO getCart(Long userId) throws StoryBookException;
}
