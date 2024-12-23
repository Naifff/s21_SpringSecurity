package org.example.repository;

import org.example.entity.Role;
import org.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Test
	void whenFindByUsername_thenReturnUser() {
		// given
		User user = new User();
		user.setUsername("test");
		user.setPassword("password");
		user.setEmail("test@example.com");
		entityManager.persist(user);
		entityManager.flush();

		// when
		Optional<User> found = userRepository.findByUsername("test");

		// then
		assertThat(found).isPresent();
		assertThat(found.get().getUsername()).isEqualTo("test");
	}

	@Test
	void whenExistsByUsername_thenReturnTrue() {
		// given
		User user = new User();
		user.setUsername("test");
		user.setPassword("password");
		user.setEmail("test@example.com");
		entityManager.persist(user);
		entityManager.flush();

		// when
		boolean exists = userRepository.existsByUsername("test");

		// then
		assertThat(exists).isTrue();
	}
}

