package com.company.storybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.company.storybook.utility.PasswordMatch;
import lombok.Data;

/**
 * ResetPasswordRequest DTO - Request to reset password with OTP verification
 */
@Data
@PasswordMatch
public class ResetPasswordRequest {
    
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;
    
    @NotBlank(message = "{otp.required}")
    @Pattern(regexp = "\\d{6}", message = "{otp.invalid.format}")
    private String otp;
    
    @NotBlank(message = "{user.password.required}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "{user.password.invalid.format}")
    private String newPassword;
    
    @NotBlank(message = "{user.confirmPassword.required}")
    private String confirmPassword;
}
