package com.company.storybook.service;

import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.entity.Storybook;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.StorybookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StorybookServiceImpl - Implementation for storybook-related operations
 */
@Service(value = "storybookService")
public class StorybookServiceImpl implements StorybookService {

    @Autowired
    private StorybookRepository storybookRepository;

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
}
