package com.company.storybook.controller;

import com.company.storybook.dto.OrderResponseDTO;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.dto.WalletBalanceDTO;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserLibraryRepository;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.service.OrderService;
import com.company.storybook.service.WalletService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Get wallet balance for logged-in user
     */
    @GetMapping("/balance")
    public ResponseEntity<WalletBalanceDTO> getWalletBalance() throws StoryBookException {
        Long userId = getCurrentUserId();
        WalletBalanceDTO response = new WalletBalanceDTO();
        response.setUserId(userId);
        response.setBalance(walletService.getBalance(userId));
        return ResponseEntity.ok(response);
    }

    /**
     * Checkout - Process payment from cart using wallet
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout() throws StoryBookException {
        Long userId = getCurrentUserId();
        OrderResponseDTO order = orderService.checkout(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", messageSource.getMessage("checkout.success", null, Locale.ENGLISH));
        response.put("order", order);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get user's library (purchased storybooks)
     */
    @GetMapping("/library")
    public ResponseEntity<Map<String, Object>> getUserLibrary() throws StoryBookException {
        Long userId = getCurrentUserId();
        
        List<StorybookResponse> library = userLibraryRepository.findByUserId(userId).stream()
                .map(userLibrary -> mapStorybookToResponse(userLibrary.getStorybook()))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User library retrieved successfully");
        response.put("items", library);
        response.put("total", library.size());

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

        String email = (String) authentication.getPrincipal();

        if (email == null || email.isEmpty()) {
            throw new StoryBookException("user.not.authenticated");
        }

        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new StoryBookException("user.not.found"));
    }

    /**
     * Helper method to map Storybook entity to StorybookResponse
     */
    private StorybookResponse mapStorybookToResponse(com.company.storybook.entity.Storybook storybook) {
        StorybookResponse response = new StorybookResponse();
        response.setId(storybook.getId());
        response.setTitle(storybook.getTitle());
        response.setDescription(storybook.getDescription());
        response.setPrice(storybook.getPrice());
        response.setAudioUrl(storybook.getAudioUrl());
        response.setCoverImageUrl(storybook.getCoverImageUrl());
        response.setCreatedAt(storybook.getCreatedAt());

        if (storybook.getAuthor() != null) {
            response.setAuthorId(storybook.getAuthor().getId());
            response.setAuthorName(storybook.getAuthor().getName());
        }

        if (storybook.getCategory() != null) {
            response.setCategoryId(storybook.getCategory().getId());
            response.setCategoryName(storybook.getCategory().getName());
        }

        return response;
    }
}
