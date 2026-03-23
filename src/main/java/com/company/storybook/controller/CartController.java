package com.company.storybook.controller;

import com.company.storybook.dto.AddToCartRequest;
import com.company.storybook.dto.CartResponseDTO;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.CartService;
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
 * Cart Controller - Handles all cart-related operations
 * Endpoints: /cart/*
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

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
     * Add storybook to cart
     * POST /cart/add
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody AddToCartRequest request) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        CartResponseDTO cart = cartService.addToCart(userId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.item.added.success", null, Locale.ENGLISH));
        response.put("cart", cart);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user's cart
     * GET /cart
     */
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        CartResponseDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove item from cart
     * DELETE /cart/items/{cartItemId}
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable Long cartItemId) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        CartResponseDTO cart = cartService.removeFromCart(userId, cartItemId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.item.removed.success", null, Locale.ENGLISH));
        response.put("cart", cart);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update cart item quantity
     * PUT /cart/items/{cartItemId}
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        CartResponseDTO cart = cartService.updateCartItemQuantity(userId, cartItemId, quantity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.item.updated.success", null, Locale.ENGLISH));
        response.put("cart", cart);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear user's cart
     * DELETE /cart/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        cartService.clearCart(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("cart.cleared.success", null, Locale.ENGLISH));
        
        return ResponseEntity.ok(response);
    }
}
