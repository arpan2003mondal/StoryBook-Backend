package com.company.storybook.service;

import com.company.storybook.entity.OtpVerification;
import com.company.storybook.exception.StoryBookException;
import com.company.storybook.repository.OtpVerificationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * OtpService - Service for generating, sending, and verifying OTPs
 */
@Service
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

  

    @Value("${otp.expiry.minutes:10}")
    private Integer otpExpiryMinutes;

    /**
     * Generate and send OTP to email
     * @param email - Email address to send OTP to
     * @throws StoryBookException if OTP sending fails
     */
    @Transactional
    public void sendOtp(String email) throws StoryBookException {
        try {
            // Delete existing OTP if any
            otpRepository.deleteByEmail(email);

            // Generate 6-digit OTP
            String otp = generateOtp();

            // Calculate expiry time
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

            // Save OTP to database
            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setEmail(email);
            otpVerification.setOtp(otp);
            otpVerification.setExpiresAt(expiresAt);
            otpVerification.setAttempts(0);
            otpVerification.setIsVerified(false);
            otpRepository.save(otpVerification);

            // Send OTP via email
            emailService.sendOtpEmail(email, otp);

        } catch (MessagingException e) {
            throw new StoryBookException("otp.send.failed");
        } catch (Exception e) {
            throw new StoryBookException("otp.send.failed");
        }
    }

    /**
     * Generate and send forgot password OTP to email
     * @param email - Email address to send forgot password OTP to
     * @throws StoryBookException if OTP sending fails
     */
    @Transactional
    public void sendForgotPasswordOtp(String email) throws StoryBookException {
        try {
            // Delete existing OTP if any
            otpRepository.deleteByEmail(email);

            // Generate 6-digit OTP
            String otp = generateOtp();

            // Calculate expiry time
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpiryMinutes);

            // Save OTP to database
            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setEmail(email);
            otpVerification.setOtp(otp);
            otpVerification.setExpiresAt(expiresAt);
            otpVerification.setAttempts(0);
            otpVerification.setIsVerified(false);
            otpRepository.save(otpVerification);

            // Send OTP via email with forgot password template
            emailService.sendForgotPasswordOtpEmail(email, otp);

        } catch (MessagingException e) {
            throw new StoryBookException("otp.send.failed");
        } catch (Exception e) {
            throw new StoryBookException("otp.send.failed");
        }
    }

    /**
     * Verify OTP
     * @param email - Email address
     * @param otp - OTP code to verify
     * @throws StoryBookException if OTP verification fails
     */
    @Transactional
    public void verifyOtp(String email, String otp) throws StoryBookException {
        OtpVerification otpVerification = otpRepository.findByEmail(email)
                .orElseThrow(() -> new StoryBookException("otp.not.found"));

        // Check if OTP expired
        if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
            otpRepository.deleteByEmail(email);
            throw new StoryBookException("otp.expired");
        }

        // Check attempts (max 5 wrong attempts)
        if (otpVerification.getAttempts() >= 5) {
            otpRepository.deleteByEmail(email);
            throw new StoryBookException("otp.max.attempts.exceeded");
        }

        // Verify OTP
        if (!otpVerification.getOtp().equals(otp)) {
            otpVerification.setAttempts(otpVerification.getAttempts() + 1);
            otpRepository.save(otpVerification);
            throw new StoryBookException("otp.invalid");
        }
    }

    /**
     * Delete OTP after successful verification
     * @param email - Email address
     */
    @Transactional
    public void deleteOtp(String email) {
        otpRepository.deleteByEmail(email);
    }

    /**
     * Generate 6-digit OTP
     * @return OTP code
     */
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
