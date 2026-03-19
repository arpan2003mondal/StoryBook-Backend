package com.company.storybook.service;

import com.company.storybook.dto.LoginRequest;
import com.company.storybook.dto.StorybookRequest;
import com.company.storybook.dto.StorybookResponse;
import com.company.storybook.dto.AuthorRequest;
import com.company.storybook.dto.CategoryRequest;
import com.company.storybook.dto.UpdatePriceRequest;
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
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorybookRepository storybookRepository;

    @Autowired
    private MessageSource messageSource;

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
    public String logout(String token) {
        tokenBlacklistService.blacklist(token);
        return messageSource.getMessage("admin.logout.success", null, Locale.ENGLISH);
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
        storybook.setSampleAudioUrl(request.getSampleAudioUrl());
        storybook.setCoverImageUrl(request.getCoverImageUrl());

        Storybook saved = storybookRepository.save(storybook);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public StorybookResponse updateStorybookPrice(Long storybookId, UpdatePriceRequest request) throws StoryBookException {
        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StoryBookException("storybook.not.found"));

        storybook.setPrice(request.getPrice());
        Storybook updated = storybookRepository.save(storybook);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public Object addAuthor(AuthorRequest authorRequest) throws StoryBookException {
        Author author = new Author();
        author.setName(authorRequest.getName());
        author.setBio(authorRequest.getBio());
        
        Author saved = authorRepository.save(author);
        
        return mapAuthorToDTO(saved);
    }

    @Override
    @Transactional
    public Object updateAuthor(Long authorId, AuthorRequest authorRequest) throws StoryBookException {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new StoryBookException("author.not.found"));

        author.setName(authorRequest.getName());
        author.setBio(authorRequest.getBio());
        
        Author updated = authorRepository.save(author);
        return mapAuthorToDTO(updated);
    }

    @Override
    @Transactional
    public String deleteAuthor(Long authorId) throws StoryBookException {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new StoryBookException("author.not.found"));

        authorRepository.delete(author);
        return messageSource.getMessage("author.deleted.success", null, Locale.ENGLISH);
    }

    @Override
    @Transactional
    public Object addCategory(CategoryRequest categoryRequest) throws StoryBookException {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        
        Category saved = categoryRepository.save(category);
        return mapCategoryToDTO(saved);
    }

    @Override
    @Transactional
    public Object updateCategory(Long categoryId, CategoryRequest categoryRequest) throws StoryBookException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StoryBookException("category.not.found"));

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());
        
        Category updated = categoryRepository.save(category);
        return mapCategoryToDTO(updated);
    }

    @Override
    @Transactional
    public String deleteCategory(Long categoryId) throws StoryBookException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new StoryBookException("category.not.found"));

        categoryRepository.delete(category);
        return messageSource.getMessage("category.deleted.success", null, Locale.ENGLISH);
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
        response.setSampleAudioUrl(storybook.getSampleAudioUrl());
        response.setCoverImageUrl(storybook.getCoverImageUrl());
        response.setCreatedAt(storybook.getCreatedAt());
        return response;
    }

    private Object mapAuthorToDTO(Author author) {
        return new com.company.storybook.dto.AuthorRequest() {{
            setName(author.getName());
            setBio(author.getBio());
        }};
    }

    private Object mapCategoryToDTO(Category category) {
        return new com.company.storybook.dto.CategoryRequest() {{
            setName(category.getName());
            setDescription(category.getDescription());
        }};
    }
}
