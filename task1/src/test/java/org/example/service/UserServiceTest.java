package org.example.service;

import org.example.entity.User;
import org.example.exception.DuplicateResourceException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.id(1L)
				.name("Test User")
				.email("test@example.com")
				.build();
	}

	@Test
	void whenCreateUser_thenReturnUser() {
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User created = userService.createUser(testUser);

		assertThat(created).isNotNull();
		assertThat(created.getEmail()).isEqualTo(testUser.getEmail());
	}

	@Test
	void whenCreateUserWithDuplicateEmail_thenThrowException() {
		when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> {
			userService.createUser(testUser);
		});
	}

	@Test
	void whenGetAllUsers_thenReturnUsersList() {
		List<User> users = Arrays.asList(testUser);
		when(userRepository.findAll()).thenReturn(users);

		List<User> found = userService.getAllUsers();

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getEmail()).isEqualTo(testUser.getEmail());
	}
}