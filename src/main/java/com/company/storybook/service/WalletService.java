package com.company.storybook.service;

import com.company.storybook.exception.StoryBookException;
import java.math.BigDecimal;

public interface WalletService {
    
    /**
     * Get wallet balance for a user
     */
    BigDecimal getBalance(Long userId) throws StoryBookException;
    
    /**
     * Debit amount from wallet
     */
    void debitWallet(Long userId, BigDecimal amount) throws StoryBookException;
    
    /**
     * Credit amount to wallet
     */
    void creditWallet(Long userId, BigDecimal amount) throws StoryBookException;
    
    /**
     * Check if user has sufficient balance
     */
    boolean hasSufficientBalance(Long userId, BigDecimal amount) throws StoryBookException;
}
