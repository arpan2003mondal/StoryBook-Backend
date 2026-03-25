package com.company.storybook.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * EmailService - Service for sending emails
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@storybook.com}")
    private String fromEmail;

    @Value("${app.name:StoryBook}")
    private String appName;

    /**
     * Send OTP email to user
     * @param recipientEmail - Email address to send OTP to
     * @param otp - One-Time Password (6 digits)
     * @throws MessagingException if email sending fails
     */
    public void sendOtpEmail(String recipientEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(recipientEmail);
        helper.setSubject(appName + " - Email Verification OTP");

        String htmlContent = buildOtpEmailContent(otp);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Build HTML content for OTP email
     */
    private String buildOtpEmailContent(String otp) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;">
                <div style="background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); max-width: 500px; margin: 0 auto;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #667eea; margin: 0;">%s</h1>
                        <p style="color: #999; margin: 5px 0 0 0;">Email Verification</p>
                    </div>
                    
                    <p style="color: #333; font-size: 16px; margin-bottom: 20px;">
                        Your OTP (One-Time Password) for email verification is:
                    </p>
                    
                    <div style="background-color: #f9f9f9; border: 2px dashed #667eea; padding: 20px; text-align: center; margin-bottom: 20px; border-radius: 8px;">
                        <p style="font-size: 32px; font-weight: bold; color: #667eea; margin: 0; letter-spacing: 5px;">%s</p>
                    </div>
                    
                    <p style="color: #666; font-size: 14px; margin: 20px 0;">
                        <strong>⏱️ This OTP will expire in 10 minutes.</strong>
                    </p>
                    
                    <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px;">
                        <p style="color: #856404; margin: 0; font-size: 14px;">
                            <strong>⚠️ Security Warning:</strong> Do not share this OTP with anyone. %s staff will never ask for it.
                        </p>
                    </div>
                    
                    <p style="color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;">
                        If you didn't request this OTP, please ignore this email or contact our support team immediately.
                    </p>
                    
                    <p style="color: #999; font-size: 12px; margin-top: 10px;">
                        © %s 2026. All rights reserved.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(appName, otp, appName, appName);
    }
}
