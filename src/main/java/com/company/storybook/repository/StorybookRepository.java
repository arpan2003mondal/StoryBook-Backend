package com.company.storybook.repository;

import com.company.storybook.entity.Storybook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StorybookRepository extends JpaRepository<Storybook, Long> {

    @Query("SELECT s FROM Storybook s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Storybook> searchByTitleOrDescription(@Param("keyword") String keyword);

    @Query("SELECT s FROM Storybook s WHERE LOWER(s.author.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Storybook> searchByAuthorName(@Param("authorName") String authorName);

    @Query("SELECT s FROM Storybook s WHERE LOWER(s.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Storybook> searchByCategory(@Param("categoryName") String categoryName);

    @Query("SELECT s FROM Storybook s WHERE " +
            "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.author.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Storybook> searchStorybooks(@Param("keyword") String keyword);
}
