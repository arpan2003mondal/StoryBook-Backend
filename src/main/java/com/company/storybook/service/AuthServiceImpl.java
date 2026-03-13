package com.company.storybook.service;

import com.company.storybook.dto.RegisterRequest;
import com.company.storybook.dto.LoginRequest;
import com.company.storybook.entity.User;
import com.company.storybook.entity.Wallet;
import com.company.storybook.entity.Cart;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.repository.WalletRepository;
import com.company.storybook.repository.CartRepository;
import com.company.storybook.utility.JwtUtil;
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
public class AuthServiceImpl implements AuthService {

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

    @Override
    @Transactional
    public String registerUser(RegisterRequest registerRequest) throws StoryBookException {

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

        return "User Registration Successfull";
    }

    @Override
    public String loginUser(LoginRequest loginRequest) throws StoryBookException {
        // Fetch user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new StoryBookException("user.not.found"));

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
}
