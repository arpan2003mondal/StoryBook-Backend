package com.company.storybook.controller;

import com.company.storybook.dto.AddToWishlistRequest;
import com.company.storybook.dto.RemoveFromWishlistRequest;
import com.company.storybook.dto.WishlistResponseDTO;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.WishlistService;
import com.company.storybook.utility.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Wishlist Controller - Handles all wishlist-related operations
 * Endpoints: /wishlist/*
 */
@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

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
     * Add storybook to wishlist
     * POST /wishlist/add
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToWishlist(@RequestBody AddToWishlistRequest request) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        wishlistService.addToWishlist(userId, request.getStoryBookId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("wishlist.item.added.success", null, Locale.ENGLISH));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user's wishlist
     * GET /wishlist
     */
    @GetMapping
    public ResponseEntity<WishlistResponseDTO> getWishlist() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        WishlistResponseDTO wishlist = wishlistService.getUserWishlist(userId);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * Remove item from wishlist
     * POST /wishlist/remove
     */
    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@RequestBody RemoveFromWishlistRequest request) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        wishlistService.removeFromWishlist(userId, request.getStoryBookId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("wishlist.item.removed.success", null, Locale.ENGLISH));
        
        return ResponseEntity.ok(response);
    }
}
