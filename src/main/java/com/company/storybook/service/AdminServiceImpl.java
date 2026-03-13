package com.company.storybook.service;

import com.company.storybook.dto.LoginRequest;
import com.company.storybook.dto.StorybookRequest;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.entity.Author;
import com.company.storybook.entity.Category;
import com.company.storybook.entity.Role;
import com.company.storybook.entity.Storybook;
import com.company.storybook.entity.User;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.AuthorRepository;
import com.company.storybook.repository.CategoryRepository;
import com.company.storybook.repository.StorybookRepository;
import com.company.storybook.repository.UserRepository;
import com.company.storybook.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorybookRepository storybookRepository;

    @Override
    public String adminLogin(LoginRequest loginRequest) throws StoryBookException {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new StoryBookException("user.invalid.credentials");
        }

        if (!Role.ADMIN.equals(user.getRole())) {
            throw new StoryBookException("admin.access.denied");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public StorybookResponse addStorybook(StorybookRequest request) throws StoryBookException {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new StoryBookException("author.not.found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new StoryBookException("category.not.found"));

        Storybook storybook = new Storybook();
        storybook.setTitle(request.getTitle());
        storybook.setDescription(request.getDescription());
        storybook.setAuthor(author);
        storybook.setCategory(category);
        storybook.setPrice(request.getPrice());
        storybook.setAudioUrl(request.getAudioUrl());
        storybook.setCoverImageUrl(request.getCoverImageUrl());

        Storybook saved = storybookRepository.save(storybook);
        return toResponse(saved);
    }

    private StorybookResponse toResponse(Storybook storybook) {
        StorybookResponse response = new StorybookResponse();
        response.setId(storybook.getId());
        response.setTitle(storybook.getTitle());
        response.setDescription(storybook.getDescription());
        response.setAuthorId(storybook.getAuthor().getId());
        response.setAuthorName(storybook.getAuthor().getName());
        response.setCategoryId(storybook.getCategory().getId());
        response.setCategoryName(storybook.getCategory().getName());
        response.setPrice(storybook.getPrice());
        response.setAudioUrl(storybook.getAudioUrl());
        response.setCoverImageUrl(storybook.getCoverImageUrl());
        response.setCreatedAt(storybook.getCreatedAt());
        return response;
    }
}
