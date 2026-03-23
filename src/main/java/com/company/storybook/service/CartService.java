package com.company.storybook.service;

import com.company.storybook.dto.AddToCartRequest;
import com.company.storybook.dto.CartResponseDTO;
import com.company.storybook.exception.StoryBookException;

/**
 * CartService Interface - Contains all cart-related operations
 */
public interface CartService {

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

    /**
     * Update quantity of a cart item
     */
    CartResponseDTO updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) throws StoryBookException;

    /**
     * Clear all items from user's cart
     */
    void clearCart(Long userId) throws StoryBookException;
}
