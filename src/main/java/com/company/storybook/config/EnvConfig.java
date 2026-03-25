package com.company.storybook.config;

import org.springframework.stereotype.Component;

/**
 * Configuration utility class for accessing environment variables.
 * The .env file is loaded by DotenvInitializer during Spring Boot startup.
 * This class provides helper methods for programmatic access to environment variables.
 */
@Component
public class EnvConfig {

    /**
     * Get environment variable with fallback to default value.
     *
     * @param key The environment variable key
     * @param defaultValue The default value if key is not found
     * @return The environment variable value or default value
     */
    public static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Get environment variable (required).
     *
     * @param key The environment variable key
     * @return The environment variable value
     * @throws IllegalArgumentException if the variable is not found
     */
    public static String get(String key) {
        String value = System.getenv(key);
        if (value == null) {
            throw new IllegalArgumentException(
                    String.format("Required environment variable '%s' not found. " +
                            "Please check your .env file.", key));
        }
        return value;
    }
}
