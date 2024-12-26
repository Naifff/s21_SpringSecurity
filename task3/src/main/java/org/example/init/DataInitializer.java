package org.example.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

	private final UserRepository userRepository;

	@PostConstruct
	@Transactional
	public void initialize() {
		// Create an admin user if none exists
		if (userRepository.count() == 0) {
			User adminUser = User.builder()
					.name("Admin User")
					.email("admin@example.com")
					.oauthId("admin-oauth-id")
					.provider("google")
					.role("ROLE_ADMIN")
					.enabled(true)
					.build();

			userRepository.save(adminUser);
			log.info("Created admin user: {}", adminUser.getEmail());

			// Create a regular user for testing
			User testUser = User.builder()
					.name("Test User")
					.email("user@example.com")
					.oauthId("user-oauth-id")
					.provider("google")
					.role("ROLE_USER")
					.enabled(true)
					.build();

			userRepository.save(testUser);
			log.info("Created test user: {}", testUser.getEmail());
		}
	}
}