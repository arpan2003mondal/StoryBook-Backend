package com.company.storybook.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * UserProfileDTO - Response DTO for user profile (excludes password)
 */
@Data
public class UserProfileDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
