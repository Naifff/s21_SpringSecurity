package org.example.security;

import org.example.model.Role;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JWTUtilsTest {

	private JWTUtils jwtUtils;
	private User testUser;
	private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
	private static final long EXPIRATION = 86400000; // 24 hours
	private static final long REFRESH_EXPIRATION = 604800000; // 7 days

	@BeforeEach
	void setUp() {
		jwtUtils = new JWTUtils(SECRET_KEY, EXPIRATION, REFRESH_EXPIRATION);

		testUser = new User();
		testUser.setUsername("testuser");
		testUser.setPassword("password");
		testUser.setRole(Role.USER);
	}

	@Test
	void generateToken_ValidToken() {
		// Act
		String token = jwtUtils.generateToken(testUser);

		// Assert
		assertNotNull(token);
		assertTrue(token.length() > 0);
	}

	@Test
	void extractUsername_ValidToken() {
		// Arrange
		String token = jwtUtils.generateToken(testUser);

		// Act
		String username = jwtUtils.extractUsername(token);

		// Assert
		assertEquals(testUser.getUsername(), username);
	}

	@Test
	void isTokenValid_ValidToken() {
		// Arrange
		String token = jwtUtils.generateToken(testUser);

		// Act
		boolean isValid = jwtUtils.isTokenValid(token, testUser);

		// Assert
		assertTrue(isValid);
	}

	@Test
	void isTokenValid_InvalidUsername() {
		// Arrange
		String token = jwtUtils.generateToken(testUser);
		User differentUser = new User();
		differentUser.setUsername("different");
		differentUser.setPassword("password");
		differentUser.setRole(Role.USER);

		// Act
		boolean isValid = jwtUtils.isTokenValid(token, differentUser);

		// Assert
		assertFalse(isValid);
	}

	@Test
	void generateRefreshToken_ValidToken() {
		// Act
		String refreshToken = jwtUtils.generateRefreshToken(testUser);

		// Assert
		assertNotNull(refreshToken);
		assertTrue(refreshToken.length() > 0);
		assertNotEquals(jwtUtils.generateToken(testUser), refreshToken);
	}
}