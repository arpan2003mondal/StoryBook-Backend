package com.company.storybook.controller;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.service.StorybookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Storybook Controller - Handles all storybook-related operations
 * Endpoints: /storybooks/*
 */
@RestController
@RequestMapping("/storybooks")
public class StoryBookUserController {

    @Autowired
    private StorybookService storybookService;

    /**
     * Get all storybooks
     * GET /storybooks
     */
    @GetMapping
    public ResponseEntity<List<StorybookResponse>> getAllStorybooks() {
        List<StorybookResponse> storybooks = storybookService.getAllStorybooks();
        return ResponseEntity.ok(storybooks);
    }

    /**
     * Search storybooks by keyword (title, author, genre/category)
     * Must be before /{id} to avoid routing conflicts
     * GET /storybooks/search?keyword=search-term
     */
    @GetMapping("/search")
    public ResponseEntity<List<StorybookResponse>> searchStorybooks(
            @RequestParam(required = false) String keyword) {
        List<StorybookResponse> results = storybookService.searchStorybooks(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * Get storybook by ID
     * GET /storybooks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StorybookResponse> getStorybookById(@PathVariable Long id) throws StoryBookException {
        StorybookResponse storybook = storybookService.getStorybookById(id);
        return ResponseEntity.ok(storybook);
    }
}

