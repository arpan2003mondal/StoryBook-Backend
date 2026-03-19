package com.company.storybook.service;

import com.company.storybook.dto.AddToCartRequest;
import com.company.storybook.dto.CartItemDTO;
import com.company.storybook.dto.CartResponseDTO;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.entity.Cart;
import com.company.storybook.entity.CartItem;
import com.company.storybook.entity.Storybook;
import com.company.storybook.entity.User;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.CartItemRepository;
import com.company.storybook.repository.CartRepository;
import com.company.storybook.repository.StorybookRepository;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.repository.UserLibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service(value = "cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private StorybookRepository storybookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Override
    public List<StorybookResponse> getAllStorybooks() {
        return storybookRepository.findAll().stream()
                .map(this::mapStorybookToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StorybookResponse getStorybookById(Long storybookId) throws StoryBookException {
        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StoryBookException("storybook.not.found"));
        return mapStorybookToResponse(storybook);
    }

    @Override
    public List<StorybookResponse> searchStorybooks(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllStorybooks();
        }
        return storybookRepository.searchStorybooks(keyword).stream()
                .map(this::mapStorybookToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartResponseDTO addToCart(Long userId, AddToCartRequest request) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify storybook exists
        Storybook storybook = storybookRepository.findById(request.getStorybookId())
                .orElseThrow(() -> new StoryBookException("storybook.not.found"));

        // Check if storybook is already purchased (in user library)
        if (userLibraryRepository.existsByUserIdAndStorybookId(userId, request.getStorybookId())) {
            throw new StoryBookException("cart.item.already.purchased");
        }

        // Get or create cart for user
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    return newCart;
                });

        // Check if storybook already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndStorybookId(cart.getId(), request.getStorybookId());
        if (existingItem.isPresent()) {
            throw new StoryBookException("cart.item.already.exists");
        }

        // Create new cart item with quantity 1
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setStorybook(storybook);
        cartItem.setQuantity(1);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

        return buildCartResponse(cartRepository.findByUserId(userId).get());
    }

    @Override
    @Transactional
    public CartResponseDTO removeFromCart(Long userId, Long cartItemId) throws StoryBookException {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify cart exists
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("cart.not.found"));

        // Find and remove cart item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new StoryBookException("cart.item.not.found"));

        // Verify cart item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new StoryBookException("unauthorized.operation");
        }

        cartItemRepository.delete(cartItem);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Reload cart from database to get updated cartItems list
        Cart updatedCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("cart.not.found"));
        
        return buildCartResponse(updatedCart);
    }

    @Override
    public CartResponseDTO getCart(Long userId) throws StoryBookException {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("cart.not.found"));

        return buildCartResponse(cart);
    }

    /**
     * Helper method to build CartResponseDTO from Cart entity
     */
    private CartResponseDTO buildCartResponse(Cart cart) {
        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getId());

        List<CartItemDTO> items = cart.getCartItems().stream()
                .map(this::mapCartItemToDTO)
                .collect(Collectors.toList());

        response.setCartItems(items);
        response.setTotalItems(items.size());

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalPrice(totalPrice);
        return response;
    }

    /**
     * Helper method to map Storybook entity to StorybookResponse
     */
    private StorybookResponse mapStorybookToResponse(Storybook storybook) {
        StorybookResponse response = new StorybookResponse();
        response.setId(storybook.getId());
        response.setTitle(storybook.getTitle());
        response.setDescription(storybook.getDescription());
        response.setPrice(storybook.getPrice());
        response.setAudioUrl(storybook.getAudioUrl());
        response.setSampleAudioUrl(storybook.getSampleAudioUrl());
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

    /**
     * Helper method to map CartItem entity to CartItemDTO
     */
    private CartItemDTO mapCartItemToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setStorybookId(cartItem.getStorybook().getId());
        dto.setTitle(cartItem.getStorybook().getTitle());
        dto.setDescription(cartItem.getStorybook().getDescription());
        dto.setPrice(cartItem.getStorybook().getPrice());
        dto.setAudioUrl(cartItem.getStorybook().getAudioUrl());
        dto.setSampleAudioUrl(cartItem.getStorybook().getSampleAudioUrl());
        dto.setCoverImageUrl(cartItem.getStorybook().getCoverImageUrl());
        dto.setQuantity(cartItem.getQuantity());
        
        if (cartItem.getStorybook().getAuthor() != null) {
            dto.setAuthorName(cartItem.getStorybook().getAuthor().getName());
        }
        
        if (cartItem.getStorybook().getCategory() != null) {
            dto.setCategoryName(cartItem.getStorybook().getCategory().getName());
        }
        
        return dto;
    }
}
