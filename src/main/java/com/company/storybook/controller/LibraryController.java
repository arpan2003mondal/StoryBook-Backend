package com.company.storybook.controller;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.LibraryService;
import com.company.storybook.utility.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Library Controller - Handles all user library operations
 * Endpoints: /library/*
 */
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Initialize AuthenticationUtil with UserRepository
     */
    @PostConstruct
    public void init() {
        AuthenticationUtil.setUserRepository(userRepository);
    }

    /**
     * Get user's library (purchased storybooks)
     * GET /library
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserLibrary() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        
        List<StorybookResponse> library = libraryService.getUserLibrary(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("library.retrieved.success", null, Locale.ENGLISH));
        response.put("items", library);
        response.put("total", library.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Check if user owns a storybook
     * GET /library/owns/{storybookId}
     */
    @GetMapping("/owns/{storybookId}")
    public ResponseEntity<Map<String, Object>> checkOwnership(@PathVariable Long storybookId) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        
        boolean owns = libraryService.userOwnsStorybook(userId, storybookId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("storybookId", storybookId);
        response.put("owns", owns);

        return ResponseEntity.ok(response);
    }

    /**
     * Get library statistics
     * GET /library/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLibraryStats() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        
        Map<String, Object> stats = libraryService.getLibraryStats(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("library.stats.retrieved", null, Locale.ENGLISH));
        response.putAll(stats);

        return ResponseEntity.ok(response);
    }
}
