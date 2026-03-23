package com.company.storybook.service;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.entity.User;
import com.company.storybook.entity.UserLibrary;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.UserLibraryRepository;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.repository.StorybookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LibraryServiceImpl - Implementation for library-related operations
 */
@Service(value = "libraryService")
public class LibraryServiceImpl implements LibraryService {

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorybookRepository storybookRepository;

    @Override
    public List<StorybookResponse> getUserLibrary(Long userId) throws StoryBookException {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Retrieve all storybooks in user's library
        return userLibraryRepository.findByUserId(userId).stream()
                .map(userLibrary -> mapStorybookToResponse(userLibrary.getStorybook()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean userOwnsStorybook(Long userId, Long storybookId) throws StoryBookException {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify storybook exists
        storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StoryBookException("storybook.not.found"));

        // Check ownership
        return userLibraryRepository.existsByUserIdAndStorybookId(userId, storybookId);
    }

    @Override
    public Map<String, Object> getLibraryStats(Long userId) throws StoryBookException {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Get all library items
        List<UserLibrary> libraryItems = userLibraryRepository.findByUserId(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", libraryItems.size());
        stats.put("userId", userId);

        // Calculate total value of library
        if (!libraryItems.isEmpty()) {
            libraryItems.stream()
                    .map(item -> item.getStorybook().getPrice())
                    .reduce((a, b) -> a.add(b))
                    .ifPresent(totalPrice -> stats.put("totalLibraryValue", totalPrice));
        } else {
            stats.put("totalLibraryValue", 0);
        }

        return stats;
    }

    @Override
    @Transactional
    public void addToLibrary(Long userId, Long storybookId) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Verify storybook exists
        com.company.storybook.entity.Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StoryBookException("storybook.not.found"));

        // Check if already in library
        if (userLibraryRepository.existsByUserIdAndStorybookId(userId, storybookId)) {
            throw new StoryBookException("library.item.already.exists");
        }

        // Create and save library entry
        UserLibrary userLibrary = new UserLibrary();
        userLibrary.setUser(user);
        userLibrary.setStorybook(storybook);
        userLibrary.setPurchasedAt(LocalDateTime.now());

        userLibraryRepository.save(userLibrary);
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
}
