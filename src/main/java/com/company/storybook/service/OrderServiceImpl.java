package com.company.storybook.service;

import com.company.storybook.dto.OrderResponseDTO;
import com.company.storybook.entity.*;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private WalletService walletService;

    @Override
    @Transactional
    public OrderResponseDTO checkout(Long userId) throws StoryBookException {
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        // Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("cart.not.found"));

        // Check if cart is empty
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new StoryBookException("cart.empty");
        }

        // Calculate total amount
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getStorybook().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Check wallet balance
        if (!walletService.hasSufficientBalance(userId, totalAmount)) {
            throw new StoryBookException("insufficient.wallet.balance");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setStorybook(cartItem.getStorybook());
            orderItem.setPrice(cartItem.getStorybook().getPrice());
            orderItems.add(orderItem);

            // Add storybook to user library using LibraryService
            try {
                libraryService.addToLibrary(userId, cartItem.getStorybook().getId());
            } catch (StoryBookException e) {
                // Ignore if already in library or other errors
                if (!e.getMessage().contains("library.item.already.exists")) {
                    throw e;
                }
            }
        }

        order.setOrderItems(orderItems);

        // Debit wallet
        walletService.debitWallet(userId, totalAmount);

        // Update order status to PAID
        order.setStatus(Order.OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Build response
        return buildOrderResponse(savedOrder);
    }

    private OrderResponseDTO buildOrderResponse(Order order) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setOrderId(order.getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderStatus(order.getStatus().toString());
        response.setCreatedAt(order.getCreatedAt());
        response.setItemCount(order.getOrderItems().size());

        return response;
    }
}
