package org.example.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Sql("/test-data.sql")
class RoleRepositoryTest {
	@Autowired
	private RoleRepository roleRepository;

	@Test
	void findByName_ShouldReturnRole() {
		var role = roleRepository.findByName("ROLE_USER");
		assertTrue(role.isPresent());
		assertEquals("ROLE_USER", role.get().getName());
	}
}