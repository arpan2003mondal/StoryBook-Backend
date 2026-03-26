package com.company.storybook.service;

import com.company.storybook.dto.WishlistItemDTO;
import com.company.storybook.dto.WishlistResponseDTO;
import com.company.storybook.entity.Storybook;
import com.company.storybook.entity.User;
import com.company.storybook.entity.Wishlist;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.StorybookRepository;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorybookRepository storybookRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Add a storybook to user's wishlist
     */
    @Override
    public void addToWishlist(Long userId, Long storyBookId) throws StoryBookException {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("user.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Check if storybook exists
        Storybook storybook = storybookRepository.findById(storyBookId)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("storybook.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndStorybookId(userId, storyBookId)) {
            String errorMsg = messageSource.getMessage("wishlist.already.exists", null, Locale.getDefault());
            throw new StoryBookException(errorMsg);
        }

        // Add to wishlist
        Wishlist wishlist = new Wishlist(user, storybook);
        wishlistRepository.save(wishlist);
    }

    /**
     * Get user's wishlist
     */
    @Override
    public WishlistResponseDTO getUserWishlist(Long userId) throws StoryBookException {
        // Check if user exists
    userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("user.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Get wishlist items
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);

        // Convert to DTO
        List<WishlistItemDTO> itemDTOs = wishlistItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        WishlistResponseDTO response = new WishlistResponseDTO();
        response.setWishlistItems(itemDTOs);
        response.setTotalItems(itemDTOs.size());

        return response;
    }

    /**
     * Remove a storybook from user's wishlist
     */
    @Override
    public void removeFromWishlist(Long userId, Long storyBookId) throws StoryBookException {
        // Check if user exists
       userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("user.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Check if item exists in wishlist
        wishlistRepository.findByUserIdAndStorybookId(userId, storyBookId)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage("wishlist.item.not.found", null, Locale.getDefault());
                    return new StoryBookException(errorMsg);
                });

        // Remove from wishlist
        wishlistRepository.deleteByUserIdAndStorybookId(userId, storyBookId);
    }

    /**
     * Convert Wishlist entity to WishlistItemDTO
     */
    private WishlistItemDTO convertToDTO(Wishlist wishlist) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setWishlistId(wishlist.getId());
        dto.setStorybookId(wishlist.getStorybook().getId());
        dto.setTitle(wishlist.getStorybook().getTitle());
        dto.setDescription(wishlist.getStorybook().getDescription());
        dto.setAuthorName(wishlist.getStorybook().getAuthor() != null ? wishlist.getStorybook().getAuthor().getName() : "");
        dto.setCategoryName(wishlist.getStorybook().getCategory() != null ? wishlist.getStorybook().getCategory().getName() : "");
        dto.setPrice(wishlist.getStorybook().getPrice());
        dto.setAudioUrl(wishlist.getStorybook().getAudioUrl());
        dto.setSampleAudioUrl(wishlist.getStorybook().getSampleAudioUrl());
        dto.setCoverImageUrl(wishlist.getStorybook().getCoverImageUrl());
        dto.setAddedAt(wishlist.getAddedAt());
        return dto;
    }
}
