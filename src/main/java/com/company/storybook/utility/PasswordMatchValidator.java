package com.company.storybook.utility;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch annotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // Try newPassword/confirmPassword for password reset DTOs
            Field newPasswordField = value.getClass().getDeclaredField("newPassword");
            Field confirmPasswordField = value.getClass().getDeclaredField("confirmPassword");
            
            newPasswordField.setAccessible(true);
            confirmPasswordField.setAccessible(true);
            
            String newPassword = (String) newPasswordField.get(value);
            String confirmPassword = (String) confirmPasswordField.get(value);
            
            if (newPassword == null || confirmPassword == null) {
                return false;
            }
            return newPassword.equals(confirmPassword);
        } catch (NoSuchFieldException e1) {
            // Try password/confirmPassword for registration DTOs
            try {
                Field passwordField = value.getClass().getDeclaredField("password");
                Field confirmPasswordField = value.getClass().getDeclaredField("confirmPassword");
                
                passwordField.setAccessible(true);
                confirmPasswordField.setAccessible(true);
                
                String password = (String) passwordField.get(value);
                String confirmPassword = (String) confirmPasswordField.get(value);
                
                if (password == null || confirmPassword == null) {
                    return false;
                }
                return password.equals(confirmPassword);
            } catch (Exception e2) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
