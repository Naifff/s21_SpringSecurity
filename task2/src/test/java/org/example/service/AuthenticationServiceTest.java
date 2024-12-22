package org.example.service;

import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.security.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JWTUtils jwtUtils;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserService userService;

	@InjectMocks
	private AuthenticationService authService;

	private User testUser;
	private RegisterRequest registerRequest;
	private AuthenticationRequest authRequest;

	@BeforeEach
	void setUp() {
		// Initialize test data
		testUser = new User();
		testUser.setUsername("testuser");
		testUser.setPassword("encodedPassword");
		testUser.setRole(Role.USER);
		testUser.setAccountNonLocked(true);

		registerRequest = new RegisterRequest();
		registerRequest.setUsername("testuser");
		registerRequest.setPassword("password123");

		authRequest = new AuthenticationRequest();
		authRequest.setUsername("testuser");
		authRequest.setPassword("password123");
	}

	@Test
	void register_SuccessfulRegistration() {
		// Arrange
		when(userRepository.existsByUsername(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(testUser);
		when(jwtUtils.generateToken(any(User.class))).thenReturn("access_token");
		when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

		// Act
		AuthenticationResponse response = authService.register(registerRequest);

		// Assert
		assertNotNull(response);
		assertNotNull(response.getAccessToken());
		assertNotNull(response.getRefreshToken());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void register_UsernameAlreadyExists() {
		// Arrange
		when(userRepository.existsByUsername(anyString())).thenReturn(true);

		// Act & Assert
		assertThrows(IllegalArgumentException.class,
				() -> authService.register(registerRequest));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void authenticate_SuccessfulAuthentication() {
		// Arrange
		when(userService.loadUserByUsername(anyString())).thenReturn(testUser);
		when(jwtUtils.generateToken(any(User.class))).thenReturn("access_token");
		when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn("refresh_token");

		// Act
		AuthenticationResponse response = authService.authenticate(authRequest);

		// Assert
		assertNotNull(response);
		assertNotNull(response.getAccessToken());
		assertNotNull(response.getRefreshToken());
		verify(userService).resetFailedAttempts(anyString());
	}

	@Test
	void authenticate_FailedAuthentication() {
		// Arrange
		when(authenticationManager.authenticate(any()))
				.thenThrow(new BadCredentialsException("Bad credentials"));
		when(userService.loadUserByUsername(anyString())).thenReturn(testUser);

		// Act & Assert
		assertThrows(BadCredentialsException.class,
				() -> authService.authenticate(authRequest));
		verify(userService).increaseFailedAttempts(any(User.class));
	}

	@Test
	void authenticate_AccountLocked() {
		// Arrange
		testUser.setAccountNonLocked(false);
		when(authenticationManager.authenticate(any()))
				.thenReturn(new UsernamePasswordAuthenticationToken(testUser, null));
		when(userService.loadUserByUsername(anyString())).thenReturn(testUser);
		when(userService.unlockWhenTimeExpired(any(User.class))).thenReturn(false);

		// Act & Assert
		assertThrows(IllegalStateException.class,
				() -> authService.authenticate(authRequest));
	}
}