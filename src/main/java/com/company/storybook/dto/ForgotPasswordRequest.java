package com.company.storybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ForgotPasswordRequest DTO - Request to initiate forgot password process
 */
@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;
}
