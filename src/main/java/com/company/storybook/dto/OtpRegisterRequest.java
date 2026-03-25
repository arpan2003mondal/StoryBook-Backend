package com.company.storybook.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * OtpRegisterRequest DTO - Request to verify OTP and complete registration
 */
@Data
public class OtpRegisterRequest {
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{otp.required}")
    @Pattern(regexp = "\\d{6}", message = "{otp.invalid.format}")
    private String otp;

    @Valid
    private RegisterRequest registerRequest;
}
