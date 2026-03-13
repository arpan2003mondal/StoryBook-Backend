package com.company.storybook.service;

import com.company.storybook.entity.*;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public BigDecimal getBalance(Long userId) throws StoryBookException {
        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("wallet.not.found"));

        return wallet.getBalance();
    }

    @Override
    @Transactional
    public void debitWallet(Long userId, BigDecimal amount) throws StoryBookException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoryBookException("invalid.amount");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("wallet.not.found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new StoryBookException("insufficient.wallet.balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void creditWallet(Long userId, BigDecimal amount) throws StoryBookException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoryBookException("invalid.amount");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new StoryBookException("user.not.found"));

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new StoryBookException("wallet.not.found"));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    @Override
    public boolean hasSufficientBalance(Long userId, BigDecimal amount) throws StoryBookException {
        return getBalance(userId).compareTo(amount) >= 0;
    }
}
