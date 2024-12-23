package org.example.repository;

import org.example.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	void whenFindByName_thenReturnRole() {
		// given
		Role role = new Role();
		role.setName("ROLE_USER");
		entityManager.persist(role);
		entityManager.flush();

		// when
		Optional<Role> found = roleRepository.findByName("ROLE_USER");

		// then
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("ROLE_USER");
	}
}