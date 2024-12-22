package org.example.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		// Only initialize if no users exist
		if (userRepository.count() == 0) {
			log.info("Initializing database with default users...");

			// Create SUPER_ADMIN user
			createUser("admin", "admin123", Role.SUPER_ADMIN);

			// Create MODERATOR user
			createUser("moderator", "mod123", Role.MODERATOR);

			// Create regular USER
			createUser("user", "user123", Role.USER);

			log.info("Database initialization completed successfully");
		}
	}

	private void createUser(String username, String password, Role role) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		user.setAccountNonLocked(true);
		userRepository.save(user);
		log.info("Created {} user: {}", role, username);
	}
}