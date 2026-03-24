package com.company.storybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.company.storybook.utility.PasswordMatch;
import lombok.Data;

@Data
@PasswordMatch
public class RegisterRequest {

    @NotBlank(message = "{user.name.required}")
    @Pattern(regexp = "^[A-Z][a-z]{2,}( [A-Z][a-z]+)?$", message = "{user.name.invalid}")
    private String name;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @NotBlank(message = "{user.password.required}")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "{user.password.invalid.format}")
    private String password;

    @NotBlank(message = "{user.confirmPassword.required}")
    private String confirmPassword;

}