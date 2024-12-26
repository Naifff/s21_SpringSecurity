package org.example.repository;

import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	void findByOauthId_WhenUserExists_ShouldReturnUser() {
		// Arrange
		User user = User.builder()
				.name("Test User")
				.email("test@example.com")
				.oauthId("oauth-123")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		entityManager.persist(user);
		entityManager.flush();

		// Act
		Optional<User> found = userRepository.findByOauthId("oauth-123");

		// Assert
		assertTrue(found.isPresent());
		assertEquals("test@example.com", found.get().getEmail());
	}

	@Test
	void findByOauthId_WhenUserDoesNotExist_ShouldReturnEmpty() {
		// Act
		Optional<User> found = userRepository.findByOauthId("non-existent");

		// Assert
		assertTrue(found.isEmpty());
	}

	@Test
	void findByEmail_WhenUserExists_ShouldReturnUser() {
		// Arrange
		User user = User.builder()
				.name("Test User")
				.email("test@example.com")
				.oauthId("oauth-123")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		entityManager.persist(user);
		entityManager.flush();

		// Act
		Optional<User> found = userRepository.findByEmail("test@example.com");

		// Assert
		assertTrue(found.isPresent());
		assertEquals("oauth-123", found.get().getOauthId());
	}

	@Test
	void findByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
		// Act
		Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

		// Assert
		assertTrue(found.isEmpty());
	}

	@Test
	void save_NewUser_ShouldPersistUser() {
		// Arrange
		User user = User.builder()
				.name("New User")
				.email("new@example.com")
				.oauthId("oauth-new")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		// Act
		User saved = userRepository.save(user);

		// Assert
		assertNotNull(saved.getId());
		User found = entityManager.find(User.class, saved.getId());
		assertEquals("new@example.com", found.getEmail());
	}

	@Test
	void save_UpdateExistingUser_ShouldUpdateUser() {
		// Arrange
		User user = User.builder()
				.name("Initial Name")
				.email("test@example.com")
				.oauthId("oauth-123")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		user = entityManager.persist(user);
		entityManager.flush();

		// Act
		user.setName("Updated Name");
		User updated = userRepository.save(user);

		// Assert
		assertEquals("Updated Name", updated.getName());
		User found = entityManager.find(User.class, user.getId());
		assertEquals("Updated Name", found.getName());
	}

	@Test
	void findByOauthId_WithMultipleUsers_ShouldReturnCorrectUser() {
		// Arrange
		User user1 = User.builder()
				.name("User One")
				.email("one@example.com")
				.oauthId("oauth-1")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		User user2 = User.builder()
				.name("User Two")
				.email("two@example.com")
				.oauthId("oauth-2")
				.provider("google")
				.role("ROLE_USER")
				.enabled(true)
				.build();

		entityManager.persist(user1);
		entityManager.persist(user2);
		entityManager.flush();

		// Act
		Optional<User> found = userRepository.findByOauthId("oauth-2");

		// Assert
		assertTrue(found.isPresent());
		assertEquals("two@example.com", found.get().getEmail());
	}
}