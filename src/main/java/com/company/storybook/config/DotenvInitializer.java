package com.company.storybook.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Initializes environment variables from .env file very early in the Spring Boot startup process.
 * This ensures .env values are available before beans are created.
 */
public class DotenvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // Load .env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Set all dotenv variables as system properties so Spring can resolve them
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            // Set as system property so Spring property resolution can find it
            System.setProperty(key, value);
        });
    }
}
