package org.example.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilsTest {
	private JwtTokenUtils jwtTokenUtils;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		jwtTokenUtils = new JwtTokenUtils();
		ReflectionTestUtils.setField(jwtTokenUtils, "secret", "very-long-secret-key-for-testing-purposes-only");
		ReflectionTestUtils.setField(jwtTokenUtils, "jwtLifetime", Duration.ofMinutes(10));

		userDetails = new User("testUser", "password",
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	void whenGenerateToken_thenSuccess() {
		String token = jwtTokenUtils.generateToken(userDetails);

		assertNotNull(token);
		assertTrue(token.length() > 0);
	}

	@Test
	void whenGetUsername_thenSuccess() {
		String token = jwtTokenUtils.generateToken(userDetails);
		String username = jwtTokenUtils.getUsername(token);

		assertEquals("testUser", username);
	}

	@Test
	void whenGetRoles_thenSuccess() {
		String token = jwtTokenUtils.generateToken(userDetails);
		var roles = jwtTokenUtils.getRoles(token);

		assertNotNull(roles);
		assertEquals(1, roles.size());
		assertEquals("ROLE_USER", roles.get(0));
	}
}