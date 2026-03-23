package com.company.storybook.service;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;

import java.util.List;

/**
 * StorybookService Interface - Contains all storybook-related operations
 */
public interface StorybookService {

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
}
