package com.company.storybook.service;

import com.company.storybook.dto.WishlistResponseDTO;
import com.company.storybook.exception.StoryBookException;

public interface WishlistService {

    /**
     * Add a storybook to user's wishlist
     */
    void addToWishlist(Long userId, Long storyBookId) throws StoryBookException;

    /**
     * Get user's wishlist
     */
    WishlistResponseDTO getUserWishlist(Long userId) throws StoryBookException;

    /**
     * Remove a storybook from user's wishlist
     */
    void removeFromWishlist(Long userId, Long storyBookId) throws StoryBookException;
}
