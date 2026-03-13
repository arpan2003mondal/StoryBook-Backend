package com.company.storybook.repository;

import com.company.storybook.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndStorybookId(Long cartId, Long storybookId);
}
