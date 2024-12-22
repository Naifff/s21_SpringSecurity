package org.example.service;

import org.example.entity.User;
import org.example.exception.DuplicateResourceException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
	void whenGetAllUsers_thenReturnUsersList() {
		List<User> users = Arrays.asList(testUser);
		when(userRepository.findAll()).thenReturn(users);

		List<User> found = userService.getAllUsers();

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getId()).isEqualTo(testUser.getId());
	}

	@Test
	void whenGetUserById_thenReturnUser() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

		User found = userService.getUserById(1L);

		assertThat(found).isNotNull();
		assertThat(found.getId()).isEqualTo(testUser.getId());
	}

	@Test
	void whenGetNonExistentUserById_thenThrowException() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			userService.getUserById(999L);
		});
	}

	@Test
	void whenCreateUser_thenReturnNewUser() {
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(testUser);

		User created = userService.createUser(testUser);

		assertThat(created).isNotNull();
		assertThat(created.getEmail()).isEqualTo(testUser.getEmail());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void whenCreateUserWithExistingEmail_thenThrowException() {
		when(userRepository.existsByEmail(anyString())).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> {
			userService.createUser(testUser);
		});
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void whenUpdateUser_thenReturnUpdatedUser() {
		User updatedUser = User.builder()
				.id(1L)
				.name("Updated Name")
				.email("updated@example.com")
				.build();

		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);

		User result = userService.updateUser(1L, updatedUser);

		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo("Updated Name");
		assertThat(result.getEmail()).isEqualTo("updated@example.com");
		verify(userRepository).save(any(User.class));
	}

	@Test
	void whenUpdateNonExistentUser_thenThrowException() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			userService.updateUser(999L, testUser);
		});
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void whenDeleteUser_thenCallRepository() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		doNothing().when(userRepository).delete(any(User.class));

		userService.deleteUser(1L);

		verify(userRepository).delete(testUser);
	}

	@Test
	void whenDeleteNonExistentUser_thenThrowException() {
		when(userRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			userService.deleteUser(999L);
		});
		verify(userRepository, never()).delete(any(User.class));
	}
}