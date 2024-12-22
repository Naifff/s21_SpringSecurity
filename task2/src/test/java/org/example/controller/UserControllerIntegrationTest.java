package org.example.controller;

import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.security.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTUtils jwtUtils;

	private User regularUser;
	private User moderatorUser;
	private User adminUser;
	private String regularUserToken;
	private String moderatorToken;
	private String adminToken;

	@BeforeEach
	void setUp() {
		// Clean up the database
		userRepository.deleteAll();

		// Create test users with different roles
		regularUser = createUser("user", "password", Role.USER);
		moderatorUser = createUser("moderator", "password", Role.MODERATOR);
		adminUser = createUser("admin", "password", Role.SUPER_ADMIN);

		// Generate tokens for each user
		regularUserToken = jwtUtils.generateToken(regularUser);
		moderatorToken = jwtUtils.generateToken(moderatorUser);
		adminToken = jwtUtils.generateToken(adminUser);
	}

	private User createUser(String username, String password, Role role) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setRole(role);
		user.setAccountNonLocked(true);
		return userRepository.save(user);
	}

	@Test
	void getUserProfile_AuthenticatedUser_Success() throws Exception {
		mockMvc.perform(get("/api/users/profile")
						.header("Authorization", "Bearer " + regularUserToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(regularUser.getUsername()))
				.andExpect(jsonPath("$.role").value(regularUser.getRole().toString()));
	}

	@Test
	void getUserProfile_NoAuthentication_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/users/profile"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void unlockUserAccount_Moderator_Success() throws Exception {
		// First lock a user account
		regularUser.setAccountNonLocked(false);
		userRepository.save(regularUser);

		// Try to unlock with moderator privileges
		mockMvc.perform(post("/api/users/" + regularUser.getUsername() + "/unlock")
						.header("Authorization", "Bearer " + moderatorToken))
				.andExpect(status().isOk());

		// Verify user is unlocked
		User updatedUser = userRepository.findByUsername(regularUser.getUsername()).orElseThrow();
		assert updatedUser.isAccountNonLocked();
	}

	@Test
	void unlockUserAccount_RegularUser_Forbidden() throws Exception {
		mockMvc.perform(post("/api/users/" + moderatorUser.getUsername() + "/unlock")
						.header("Authorization", "Bearer " + regularUserToken))
				.andExpect(status().isForbidden());
	}

	@Test
	void updateUserRole_SuperAdmin_Success() throws Exception {
		mockMvc.perform(post("/api/users/" + regularUser.getUsername() + "/role")
						.header("Authorization", "Bearer " + adminToken)
						.param("newRole", "MODERATOR"))
				.andExpect(status().isOk());
	}

	@Test
	void updateUserRole_Moderator_Forbidden() throws Exception {
		mockMvc.perform(post("/api/users/" + regularUser.getUsername() + "/role")
						.header("Authorization", "Bearer " + moderatorToken)
						.param("newRole", "MODERATOR"))
				.andExpect(status().isForbidden());
	}
}