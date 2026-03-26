package com.company.storybook.repository;

import com.company.storybook.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    List<Wishlist> findByUserId(Long userId);
    
    Optional<Wishlist> findByUserIdAndStorybookId(Long userId, Long storybookId);
    
    boolean existsByUserIdAndStorybookId(Long userId, Long storybookId);
    
    void deleteByUserIdAndStorybookId(Long userId, Long storybookId);
}
