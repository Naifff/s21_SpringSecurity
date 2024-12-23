package org.example.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class DataInitializer {
	private final UserService userService;

	@PostConstruct
	public void init() {
		// Create demo user
		createDemoUser();
		// Create sample text files
		createSampleFiles();
	}

	private void createDemoUser() {
		try {
			userService.createUser("user", "password");
		} catch (RuntimeException e) {
			// User might already exist
		}
	}

	private void createSampleFiles() {
		try {
			Path uploadDir = Paths.get("./uploads");
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}

			// Create sample text file
			Path sampleFile = uploadDir.resolve("sample.txt");
			if (!Files.exists(sampleFile)) {
				Files.writeString(sampleFile, "This is a sample text file.\nWelcome to the File Viewer application!");
			}

			// Create another sample file
			Path readmeFile = uploadDir.resolve("readme.txt");
			if (!Files.exists(readmeFile)) {
				Files.writeString(readmeFile, "This is a README file.\nYou can view text files using this application.");
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to create sample files", e);
		}
	}
}