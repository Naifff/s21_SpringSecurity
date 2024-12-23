package org.example.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilsTest {
	private JwtTokenUtils jwtTokenUtils;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		jwtTokenUtils = new JwtTokenUtils();
		ReflectionTestUtils.setField(jwtTokenUtils, "secret", "test-secret-key-that-is-very-long-for-testing");
		ReflectionTestUtils.setField(jwtTokenUtils, "jwtLifetime", Duration.ofMinutes(10));

		userDetails = User.builder()
				.username("testUser")
				.password("password")
				.authorities(Collections.singletonList(() -> "ROLE_USER"))
				.build();
	}

	@Test
	void whenGenerateToken_thenSuccessfullyGenerateAndParseToken() {
		String token = jwtTokenUtils.generateToken(userDetails);

		assertNotNull(token);
		assertEquals("testUser", jwtTokenUtils.getUsername(token));

		List<String> roles = jwtTokenUtils.getRoles(token);
		assertNotNull(roles);
		assertTrue(roles.contains("ROLE_USER"));
	}

	@Test
	void whenGetRoles_thenReturnCorrectRoles() {
		String token = jwtTokenUtils.generateToken(userDetails);
		List<String> roles = jwtTokenUtils.getRoles(token);

		assertEquals(1, roles.size());
		assertEquals("ROLE_USER", roles.get(0));
	}
}