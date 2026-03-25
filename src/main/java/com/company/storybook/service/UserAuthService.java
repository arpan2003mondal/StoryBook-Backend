package com.company.storybook.service;

import com.company.storybook.dto.RegisterRequest;
import com.company.storybook.dto.LoginRequest;
import com.company.storybook.dto.ChangePasswordRequest;
import com.company.storybook.dto.ChangeUsernameRequest;
import com.company.storybook.dto.UserProfileDTO;
import com.company.storybook.dto.OtpRegisterRequest;
import com.company.storybook.exception.StoryBookException;

public interface UserAuthService {
    String registerUser(RegisterRequest registerRequest) throws StoryBookException;
    String loginUser(LoginRequest loginRequest) throws StoryBookException;
    String logout(String token);
    String changePassword(Long userId, ChangePasswordRequest request) throws StoryBookException;
    String changeUsername(Long userId, ChangeUsernameRequest request) throws StoryBookException;
    UserProfileDTO getUserProfile(Long userId) throws StoryBookException;
    
    // OTP Registration Methods
    String sendOtpWithRegistrationDetails(RegisterRequest registerRequest) throws StoryBookException;
    String verifyOtpAndRegister(OtpRegisterRequest otpRegisterRequest) throws StoryBookException;
}
