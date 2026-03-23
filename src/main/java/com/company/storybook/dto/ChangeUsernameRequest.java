package com.company.storybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeUsernameRequest {

    @NotBlank(message = "{user.name.required}")
    private String newUsername;
}
