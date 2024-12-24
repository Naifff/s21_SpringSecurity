package org.example.services;

import org.example.dtos.JwtRequest;
import org.example.dtos.JwtResponse;
import org.example.dtos.RegistrationUserDto;
import org.example.dtos.UserDto;
import org.example.entities.Role;
import org.example.entities.User;
import org.example.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@Mock
	private UserService userService;
	@Mock
	private JwtTokenUtils jwtTokenUtils;
	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthService authService;

	private User testUser;
	private JwtRequest authRequest;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("test");
		testUser.setPassword("password");
		testUser.setEmail("test@test.com");
		testUser.setAccountNonLocked(true);
		testUser.setRoles(Collections.singletonList(new Role()));

		authRequest = new JwtRequest("test", "password");
	}

	@Test
	void createAuthToken_Success() {
		when(authenticationManager.authenticate(any())).thenReturn(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		when(userService.findByUsername("test")).thenReturn(Optional.of(testUser));
		when(userService.loadUserByUsername("test")).thenReturn(mock(UserDetails.class));
		when(jwtTokenUtils.generateToken(any())).thenReturn("test-token");

		JwtResponse response = authService.createAuthToken(authRequest);

		assertNotNull(response);
		assertEquals("test-token", response.getToken());
		verify(userService).resetFailedAttempts("test");
	}

	@Test
	void createAuthToken_BadCredentials() {
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

		assertThrows(BadCredentialsException.class, () -> authService.createAuthToken(authRequest));
		verify(userService).increaseFailedAttempts("test");
	}

	@Test
	void createNewUser_Success() {
		RegistrationUserDto dto = new RegistrationUserDto("test", "password", "password", "test@test.com");
		when(userService.findByUsername("test")).thenReturn(Optional.empty());
		when(userService.createNewUser(any())).thenReturn(testUser);

		UserDto response = authService.createNewUser(dto);

		assertNotNull(response);
		assertEquals("test", response.getUsername());
		assertEquals("test@test.com", response.getEmail());
	}

	@Test
	void createNewUser_PasswordsDontMatch() {
		RegistrationUserDto dto = new RegistrationUserDto("test", "password", "different", "test@test.com");

		Exception exception = assertThrows(RuntimeException.class, () -> authService.createNewUser(dto));
		assertEquals("Passwords don't match", exception.getMessage());
	}

	@Test
	void createNewUser_UserExists() {
		RegistrationUserDto dto = new RegistrationUserDto("test", "password", "password", "test@test.com");
		when(userService.findByUsername("test")).thenReturn(Optional.of(testUser));

		Exception exception = assertThrows(RuntimeException.class, () -> authService.createNewUser(dto));
		assertEquals("Username already exists", exception.getMessage());
	}
}