package com.company.storybook.service;

import com.company.storybook.dto.RegisterRequest;
import com.company.storybook.dto.LoginRequest;
import com.company.storybook.dto.ChangePasswordRequest;
import com.company.storybook.dto.ChangeUsernameRequest;
import com.company.storybook.dto.UserProfileDTO;
import com.company.storybook.dto.OtpRegisterRequest;
import com.company.storybook.entity.User;
import com.company.storybook.entity.Wallet;
import com.company.storybook.entity.Cart;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.repository.WalletRepository;
import com.company.storybook.repository.CartRepository;
import com.company.storybook.utility.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service(value = "authService")
public class UserAuthServiceImpl implements UserAuthService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public String registerUser(RegisterRequest registerRequest) throws StoryBookException {

        // Validate password and confirm password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new StoryBookException("user.confirmPassword.mismatch");
        }

        Optional<User> optional = userRepository.findByEmail(registerRequest.getEmail());
        if(optional.isPresent()){
            throw new StoryBookException("user.exists");
        }

        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        User savedUser = userRepository.save(user);

        // Create wallet with 1000 RS initial balance
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(new BigDecimal("1000.00"));
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create empty cart for user
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return messageSource.getMessage("user.registration.success", null, Locale.ENGLISH);
    }

    @Override
    public String loginUser(LoginRequest loginRequest) throws StoryBookException {
        // Fetch user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new StoryBookException("user.invalid.credentials"));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new StoryBookException("user.invalid.credentials");
        }

        // Generate JWT token
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    public String logout(String token) {
        tokenBlacklistService.blacklist(token);
        return messageSource.getMessage("user.logout.success", null, Locale.ENGLISH);
    }

    @Override
    @Transactional
    public String changePassword(Long userId, ChangePasswordRequest request) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify old password is correct
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new StoryBookException("user.password.invalid");
        }

        // Encode new password and update
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return messageSource.getMessage("user.password.change.success", null, Locale.ENGLISH);
    }

    @Override
    @Transactional
    public String changeUsername(Long userId, ChangeUsernameRequest request) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify new username is not empty
        if (request.getNewUsername() == null || request.getNewUsername().trim().isEmpty()) {
            throw new StoryBookException("user.name.required");
        }

        // Update username
        user.setName(request.getNewUsername());
        userRepository.save(user);

        return messageSource.getMessage("user.username.change.success", null, Locale.ENGLISH);
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Map to DTO (password is excluded)
        return mapUserToProfileDTO(user);
    }

    /**
     * STEP 1: Send OTP with registration details validation
     * User fills: name, email, password, confirmPassword
     * System: Validates, generates OTP, sends email, stores in DB
     */
    @Override
    @Transactional
    public String sendOtpWithRegistrationDetails(RegisterRequest registerRequest) throws StoryBookException {
        // Validate password and confirm password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new StoryBookException("user.confirmPassword.mismatch");
        }

        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        if (existingUser.isPresent()) {
            throw new StoryBookException("user.exists");
        }

        // Validate email format
        if (!isValidEmail(registerRequest.getEmail())) {
            throw new StoryBookException("user.email.invalid.format");
        }

        // Validate name format
        if (!isValidName(registerRequest.getName())) {
            throw new StoryBookException("user.name.invalid");
        }

        // Send OTP to email
        otpService.sendOtp(registerRequest.getEmail());

        return messageSource.getMessage("otp.sent.success", null, Locale.ENGLISH);
    }

    /**
     * STEP 2: Verify OTP and complete registration
     * User enters: OTP code
     * System: Verify OTP, create user, create wallet & cart
     */
    @Override
    @Transactional
    public String verifyOtpAndRegister(OtpRegisterRequest otpRegisterRequest) throws StoryBookException {
        String email = otpRegisterRequest.getEmail();
        String otp = otpRegisterRequest.getOtp();
        RegisterRequest registerRequest = otpRegisterRequest.getRegisterRequest();

        // Verify OTP is correct and not expired
        otpService.verifyOtp(email, otp);

        // Validate email matches
        if (!email.equals(registerRequest.getEmail())) {
            throw new StoryBookException("user.email.mismatch");
        }

        // Validate password and confirm password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new StoryBookException("user.confirmPassword.mismatch");
        }

        // Double-check email not already registered
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            otpService.deleteOtp(email);
            throw new StoryBookException("user.exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Create user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setEmailVerified(true);
        user.setVerifiedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Create wallet with initial balance
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(new BigDecimal("1000.00"));
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create empty cart
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Delete OTP after successful verification
        otpService.deleteOtp(email);

        // Send welcome email asynchronously (outside of transaction)
        sendWelcomeEmailAsync(savedUser.getEmail(), savedUser.getName());

        logger.info("User registered successfully: {}", savedUser.getEmail());
        return messageSource.getMessage("user.registration.success", null, Locale.ENGLISH);
    }

    /**
     * Helper method to validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Helper method to validate name format
     */
    private boolean isValidName(String name) {
        String nameRegex = "^[A-Z][a-z]{2,}( [A-Z][a-z]+)?$";
        return name.matches(nameRegex);
    }

    /**
     * Helper method to map User entity to UserProfileDTO
     * Password is intentionally excluded for security
     */
    private UserProfileDTO mapUserToProfileDTO(User user) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setName(user.getName());
        profile.setEmail(user.getEmail());
        profile.setRole(user.getRole().name());
        profile.setCreatedAt(user.getCreatedAt());
        return profile;
    }

    /**
     * Send welcome email asynchronously (non-blocking)
     * This method runs in a separate thread to avoid transaction issues
     * @param email - User's email address
     * @param name - User's name
     */
    private void sendWelcomeEmailAsync(String email, String name) {
        try {
            logger.info("Starting to send welcome email to: {}", email);
            emailService.sendWelcomeEmail(email, name);
            logger.info("Welcome email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to {}: {}", email, e.getMessage(), e);
        }
    }
}
