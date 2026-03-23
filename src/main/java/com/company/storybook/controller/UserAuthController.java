package com.company.storybook.controller;

import com.company.storybook.dto.RegisterRequest;
import com.company.storybook.dto.LoginRequest;
import com.company.storybook.dto.ChangePasswordRequest;
import com.company.storybook.dto.ChangeUsernameRequest;
import com.company.storybook.dto.UserProfileDTO;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.service.UserAuthService;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.utility.AuthenticationUtil;
import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")

public class UserAuthController {

    @Autowired
    private UserAuthService authService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Initialize AuthenticationUtil with UserRepository
     */
    @PostConstruct
    public void init() {
        AuthenticationUtil.setUserRepository(userRepository);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) throws StoryBookException {

        String message = authService.registerUser(registerRequest);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws StoryBookException {
        String token = authService.loginUser(loginRequest);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) throws StoryBookException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new StoryBookException("user.logout.invalid.token");
        }
        String token = authHeader.substring(7);
        String message = authService.logout(token);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordRequest request) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        String message = authService.changePassword(userId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Change user's username/name
     * POST /users/change-username
     */
    @PostMapping("/change-username")
    public ResponseEntity<Map<String, Object>> changeUsername(@Valid @RequestBody ChangeUsernameRequest request) throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        String message = authService.changeUsername(userId, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("newUsername", request.getNewUsername());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's profile (public information only)
     * Excludes password for security
     * GET /users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile() throws StoryBookException {
        Long userId = AuthenticationUtil.getCurrentUserId();
        UserProfileDTO profile = authService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
}
