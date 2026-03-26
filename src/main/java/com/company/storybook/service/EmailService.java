package com.company.storybook.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

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
     * @throws UnsupportedEncodingException if encoding is not supported
     */
    public void sendOtpEmail(String recipientEmail, String otp) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, appName);
        helper.setTo(recipientEmail);
        helper.setSubject(appName + " - Email Verification OTP");

        String htmlContent = buildOtpEmailContent(otp);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Send welcome email to user after successful registration
     * @param recipientEmail - Email address to send welcome email to
     * @param userName - User's name
     * @throws MessagingException if email sending fails
     * @throws UnsupportedEncodingException if encoding is not supported
     */
    public void sendWelcomeEmail(String recipientEmail, String userName) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, appName);
        helper.setTo(recipientEmail);
        helper.setSubject("Welcome to " + appName + "! Your Journey Begins Here");

        String htmlContent = buildWelcomeEmailContent(userName, recipientEmail);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    /**
     * Build HTML content for welcome email
     */
    private String buildWelcomeEmailContent(String userName, String userEmail) {
        // Use string concatenation instead of .formatted() to avoid issues with CSS color codes
        return "<html>\n" +
            "<body style=\"font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px;\">\n" +
            "    <div style=\"background-color: white; padding: 40px; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto;\">\n" +
            "        <div style=\"text-align: center; margin-bottom: 30px;\">\n" +
            "            <h1 style=\"color: #667eea; margin: 0; font-size: 28px;\">Welcome to " + appName + "! 🎉</h1>\n" +
            "            <p style=\"color: #999; margin: 10px 0 0 0; font-size: 16px;\">Account Registration Complete</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div style=\"background-color: #f0f4ff; border-left: 4px solid #667eea; padding: 20px; border-radius: 8px; margin-bottom: 25px;\">\n" +
            "            <p style=\"color: #333; font-size: 16px; margin: 0;\">\n" +
            "                Hi <strong>" + userName + "</strong>,\n" +
            "            </p>\n" +
            "            <p style=\"color: #555; font-size: 15px; margin: 15px 0 0 0; line-height: 1.6;\">\n" +
            "                Thank you for joining our community! Your account has been successfully created and is ready to use. Get ready to explore amazing stories, discover new authors, and enjoy a wonderful reading experience.\n" +
            "            </p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div style=\"margin: 30px 0;\">\n" +
            "            <h3 style=\"color: #667eea; margin-top: 0; margin-bottom: 15px;\">What's Next?</h3>\n" +
            "            <ul style=\"color: #555; font-size: 15px; line-height: 1.8;\">\n" +
            "                <li><strong>Browse Stories:</strong> Explore our vast collection of stories across different categories</li>\n" +
            "                <li><strong>Build Your Library:</strong> Add your favorite stories to your personal library</li>\n" +
            "                <li><strong>Manage Your Wallet:</strong> Start with initial credit of ₹1000 in your wallet</li>\n" +
            "                <li><strong>Create Your Cart:</strong> Add stories to your cart and manage your purchases</li>\n" +
            "            </ul>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div style=\"background-color: #e8f5e9; border-left: 4px solid #4caf50; padding: 20px; border-radius: 8px; margin: 30px 0;\">\n" +
            "            <p style=\"color: #2e7d32; margin: 0; font-size: 14px;\">\n" +
            "                <strong>💡 Pro Tip:</strong> Explore our library section to discover stories that match your interests and preferences.\n" +
            "            </p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <div style=\"background-color: #fff3cd; border-left: 4px solid #ff9800; padding: 15px; border-radius: 8px; margin: 20px 0;\">\n" +
            "            <p style=\"color: #e65100; margin: 0; font-size: 14px;\">\n" +
            "                <strong>Account Information:</strong><br>\n" +
            "                Email: <strong>" + userEmail + "</strong><br>\n" +
            "                Status: <strong>Active ✓</strong>\n" +
            "            </p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <p style=\"color: #999; font-size: 13px; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; text-align: center;\">\n" +
            "            If you have any questions or need assistance, feel free to contact our support team.\n" +
            "        </p>\n" +
            "        \n" +
            "        <p style=\"color: #999; font-size: 12px; margin-top: 15px; text-align: center;\">\n" +
            "            © " + appName + " 2026. All rights reserved.\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }

    /**
     * Build HTML content for OTP email
     */
    private String buildOtpEmailContent(String otp) {
        // Use string concatenation instead of .formatted() to avoid issues with CSS color codes
        return "<html>\n" +
            "<body style=\"font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;\">\n" +
            "    <div style=\"background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); max-width: 500px; margin: 0 auto;\">\n" +
            "        <div style=\"text-align: center; margin-bottom: 30px;\">\n" +
            "            <h1 style=\"color: #667eea; margin: 0;\">" + appName + "</h1>\n" +
            "            <p style=\"color: #999; margin: 5px 0 0 0;\">Email Verification</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <p style=\"color: #333; font-size: 16px; margin-bottom: 20px;\">\n" +
            "            Your OTP (One-Time Password) for email verification is:\n" +
            "        </p>\n" +
            "        \n" +
            "        <div style=\"background-color: #f9f9f9; border: 2px dashed #667eea; padding: 20px; text-align: center; margin-bottom: 20px; border-radius: 8px;\">\n" +
            "            <p style=\"font-size: 32px; font-weight: bold; color: #667eea; margin: 0; letter-spacing: 5px;\">" + otp + "</p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <p style=\"color: #666; font-size: 14px; margin: 20px 0;\">\n" +
            "            <strong>⏱️ This OTP will expire in 10 minutes.</strong>\n" +
            "        </p>\n" +
            "        \n" +
            "        <div style=\"background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px;\">\n" +
            "            <p style=\"color: #856404; margin: 0; font-size: 14px;\">\n" +
            "                <strong>⚠️ Security Warning:</strong> Do not share this OTP with anyone. " + appName + " staff will never ask for it.\n" +
            "            </p>\n" +
            "        </div>\n" +
            "        \n" +
            "        <p style=\"color: #999; font-size: 12px; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px;\">\n" +
            "            If you didn't request this OTP, please ignore this email or contact our support team immediately.\n" +
            "        </p>\n" +
            "        \n" +
            "        <p style=\"color: #999; font-size: 12px; margin-top: 10px;\">\n" +
            "            © " + appName + " 2026. All rights reserved.\n" +
            "        </p>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>";
    }
}
