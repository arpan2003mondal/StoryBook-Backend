package com.company.storybook.repository;

import com.company.storybook.entity.UserLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {
    List<UserLibrary> findByUserId(Long userId);
    boolean existsByUserIdAndStorybookId(Long userId, Long storybookId);
}
