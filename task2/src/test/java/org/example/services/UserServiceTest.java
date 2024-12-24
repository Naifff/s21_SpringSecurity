package org.example.services;

import org.example.dtos.RegistrationUserDto;
import org.example.entities.Role;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleService roleService;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User testUser;
	private Role userRole;

	@BeforeEach
	void setUp() {
		userRole = new Role();
		userRole.setId(1);
		userRole.setName("ROLE_USER");

		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("test");
		testUser.setPassword("encoded");
		testUser.setEmail("test@test.com");
		testUser.setRoles(Collections.singletonList(userRole));
		testUser.setAccountNonLocked(true);
	}

	@Test
	void loadUserByUsername_ShouldReturnUserDetails() {
		when(userRepository.findByUsername("test")).thenReturn(Optional.of(testUser));

		UserDetails userDetails = userService.loadUserByUsername("test");

		assertNotNull(userDetails);
		assertEquals("test", userDetails.getUsername());
		assertEquals("encoded", userDetails.getPassword());
		assertTrue(userDetails.isAccountNonLocked());
	}

	@Test
	void createNewUser_ShouldCreateUser() {
		RegistrationUserDto dto = new RegistrationUserDto("test", "password", "password", "test@test.com");
		when(passwordEncoder.encode(any())).thenReturn("encoded");
		when(roleService.getUserRole()).thenReturn(userRole);
		when(userRepository.save(any())).thenReturn(testUser);

		User created = userService.createNewUser(dto);

		assertNotNull(created);
		assertEquals("test", created.getUsername());
		assertEquals("test@test.com", created.getEmail());
	}
}