package com.company.storybook.controller;

import com.company.storybook.dto.AddToCartRequest;
import com.company.storybook.dto.CartResponseDTO;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/storybooks")


public class StoryBookUserController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Get all storybooks
     */
    @GetMapping
    public ResponseEntity<List<StorybookResponse>> getAllStorybooks() {
        List<StorybookResponse> storybooks = cartService.getAllStorybooks();
        return ResponseEntity.ok(storybooks);
    }

    /**
     * Search storybooks by keyword (title, author, genre/category)
     * Must be before /{id} to avoid routing conflicts
     */
    @GetMapping("/search")
   
    public ResponseEntity<List<StorybookResponse>> searchStorybooks(
            @RequestParam(required = false) String keyword) {
        List<StorybookResponse> results = cartService.searchStorybooks(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * Get storybook by ID
     */
    @GetMapping("/{id}")
    
    public ResponseEntity<StorybookResponse> getStorybookById(@PathVariable Long id) throws StoryBookException {
        StorybookResponse storybook = cartService.getStorybookById(id);
        return ResponseEntity.ok(storybook);
    }

    /**
     * Add storybook to cart
     */
    @PostMapping("/cart/add")
   
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody AddToCartRequest request) throws StoryBookException {
        Long userId = getCurrentUserId();
        CartResponseDTO cart = cartService.addToCart(userId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.item.added.success", null, Locale.ENGLISH));
        response.put("cart", cart);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user's cart
     */
    @GetMapping("/cart")
    
    public ResponseEntity<CartResponseDTO> getCart() throws StoryBookException {
        Long userId = getCurrentUserId();
        CartResponseDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/cart/items/{cartItemId}")
    
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable Long cartItemId) throws StoryBookException {
        Long userId = getCurrentUserId();
        CartResponseDTO cart = cartService.removeFromCart(userId, cartItemId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.item.removed.success", null, Locale.ENGLISH));
        response.put("cart", cart);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Helper method to get current user ID from security context
     */
    private Long getCurrentUserId() throws StoryBookException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new StoryBookException("user.not.authenticated");
        }
        
        // Extract email from the principal (JWT token contains email)
        String email = (String) authentication.getPrincipal();
        
        if (email == null || email.isEmpty()) {
            throw new StoryBookException("user.not.authenticated");
        }
        
        // Fetch user by email and get ID
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new StoryBookException("user.not.found"));
    }
}
