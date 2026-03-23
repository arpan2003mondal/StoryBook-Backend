package com.company.storybook.utility;

import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * AuthenticationUtil - Utility class for authentication and user context operations
 */
public class AuthenticationUtil {

    private static UserRepository userRepository;

    /**
     * Constructor for setting UserRepository via static setter
     */
    public AuthenticationUtil(UserRepository userRepository) {
        AuthenticationUtil.userRepository = userRepository;
    }

    /**
     * Static method to set UserRepository (called by controller beans or via constructor injection)
     */
    public static void setUserRepository(UserRepository repository) {
        userRepository = repository;
    }

    /**
     * Get current authenticated user ID from security context
     * Extracts email from JWT token and fetches user ID from database
     * 
     * @return User ID of authenticated user
     * @throws StoryBookException if user is not authenticated or not found
     */
    public static Long getCurrentUserId() throws StoryBookException {
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
        if (userRepository == null) {
            throw new StoryBookException("user.repository.not.initialized");
        }

        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new StoryBookException("user.not.found"));
    }
}
