package org.example.service;

import org.example.dto.DTOs;
import org.example.dto.DTOs.RegistrationUserDto;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
		testUser.setEmail("test@example.com");
		testUser.setRoles(Collections.singletonList(userRole));
	}

	@Test
	void whenValidUsername_thenUserShouldBeFound() {
		given(userRepository.findByUsername("test")).willReturn(Optional.of(testUser));

		Optional<User> found = userService.findByUsername("test");

		assertThat(found).isPresent();
		assertThat(found.get().getUsername()).isEqualTo("test");
	}

	@Test
	void whenInvalidUsername_thenLoadUserByUsernameShouldThrowException() {
		given(userRepository.findByUsername("invalid")).willReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.loadUserByUsername("invalid");
		});
	}

	@Test
	void whenValidUsername_thenLoadUserByUsernameShouldReturnUserDetails() {
		given(userRepository.findByUsername("test")).willReturn(Optional.of(testUser));

		UserDetails userDetails = userService.loadUserByUsername("test");

		assertThat(userDetails.getUsername()).isEqualTo("test");
		assertThat(userDetails.getAuthorities()).hasSize(1);
	}

	@Test
	void whenCreateNewUser_thenShouldSaveUser() {
		RegistrationUserDto dto = new RegistrationUserDto();
		dto.setUsername("newuser");
		dto.setPassword("password");
		dto.setEmail("new@example.com");

		given(passwordEncoder.encode("password")).willReturn("encoded");
		given(roleService.getUserRole()).willReturn(userRole);
		given(userRepository.save(any(User.class))).willReturn(testUser);

		User created = userService.createNewUser(dto);

		verify(userRepository).save(any(User.class));
		assertThat(created).isNotNull();
	}
}

