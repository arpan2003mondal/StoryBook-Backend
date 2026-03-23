package com.company.storybook.controller;

import com.company.storybook.dto.OrderResponseDTO;
import com.company.storybook.dto.WalletBalanceDTO;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.OrderService;
import com.company.storybook.service.WalletService;
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
 * Wallet Controller - Handles wallet balance and checkout operations
 * Library operations have been moved to LibraryController
 * Endpoints: /wallet/*
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

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
     * Get wallet balance for logged-in user
     * GET /wallet/balance
     */
    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        WalletBalanceDTO response = new WalletBalanceDTO();
        response.setUserId(userId);
        response.setBalance(walletService.getBalance(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Checkout - Process payment from cart using wallet
     * POST /wallet/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        OrderResponseDTO order = orderService.checkout(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("checkout.success", null, Locale.ENGLISH));
        response.put("order", order);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
