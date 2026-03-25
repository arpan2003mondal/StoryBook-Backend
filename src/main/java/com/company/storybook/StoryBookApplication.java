package com.company.storybook;

import com.company.storybook.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoryBookApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(StoryBookApplication.class);
		// Register DotenvInitializer to load .env file very early
		app.addInitializers(new DotenvInitializer());
		app.run(args);
	}

}
