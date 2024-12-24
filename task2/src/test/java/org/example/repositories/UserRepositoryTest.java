package org.example.repositories;

import org.example.entities.User;
import org.example.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/test-data.sql")
class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;

	@Test
	void findByUsername_ShouldReturnUser() {
		var user = userRepository.findByUsername("user");
		assertTrue(user.isPresent());
		assertEquals("user@gmail.com", user.get().getEmail());
	}

	@Test
	void incrementLoginAttempts_ShouldIncrementAttempts() {
		userRepository.incrementLoginAttempts("user");
		var user = userRepository.findByUsername("user");
		assertTrue(user.isPresent());
		assertEquals(1, user.get().getLoginAttempts());
	}

	@Test
	void resetLoginAttempts_ShouldResetAttempts() {
		userRepository.incrementLoginAttempts("user");
		userRepository.resetLoginAttempts("user");
		var user = userRepository.findByUsername("user");
		assertTrue(user.isPresent());
		assertEquals(0, user.get().getLoginAttempts());
	}
}

