package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private static final String USERNAME = "testuser";
	private static final String PASSWORD = "password";
	private static final String ENCODED_PASSWORD = "encodedPassword";

	@Test
	void createUser_Success() {
		// Arrange
		when(userRepository.existsByUsername(USERNAME)).thenReturn(false);
		when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		User createdUser = userService.createUser(USERNAME, PASSWORD);

		// Assert
		assertNotNull(createdUser);
		assertEquals(USERNAME, createdUser.getUsername());
		assertEquals(ENCODED_PASSWORD, createdUser.getPassword());
		assertEquals("ROLE_USER", createdUser.getRole());

		verify(userRepository).existsByUsername(USERNAME);
		verify(userRepository).save(any(User.class));
		verify(passwordEncoder).encode(PASSWORD);
	}

	@Test
	void createUser_UsernameTaken_ThrowsException() {
		// Arrange
		when(userRepository.existsByUsername(USERNAME)).thenReturn(true);

		// Act & Assert
		assertThrows(RuntimeException.class,
				() -> userService.createUser(USERNAME, PASSWORD));

		verify(userRepository).existsByUsername(USERNAME);
		verify(userRepository, never()).save(any(User.class));
		verify(passwordEncoder, never()).encode(any());
	}
}