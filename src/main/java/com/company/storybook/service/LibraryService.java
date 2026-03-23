package com.company.storybook.service;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;

import java.util.List;
import java.util.Map;

/**
 * LibraryService Interface - Contains all library-related operations
 */
public interface LibraryService {

    /**
     * Get all storybooks in user's library
     */
    List<StorybookResponse> getUserLibrary(Long userId) throws StoryBookException;

    /**
     * Check if user owns a specific storybook
     */
    boolean userOwnsStorybook(Long userId, Long storybookId) throws StoryBookException;

    /**
     * Get library statistics for user
     */
    Map<String, Object> getLibraryStats(Long userId) throws StoryBookException;

    /**
     * Add storybook to user's library (called during checkout)
     * Package-private method for internal use by OrderService
     */
    void addToLibrary(Long userId, Long storybookId) throws StoryBookException;
}
