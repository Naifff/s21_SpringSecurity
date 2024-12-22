package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.TestSecurityConfig;
import org.example.entity.User;
import org.example.exception.DuplicateResourceException;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.ResourceNotFoundException;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, TestSecurityConfig.class, GlobalExceptionHandler.class})
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void whenGetAllUsers_thenReturnUsersList() throws Exception {
		List<User> users = Arrays.asList(
				createTestUser(1L, "John Doe", "john@example.com"),
				createTestUser(2L, "Jane Doe", "jane@example.com")
		);

		given(userService.getAllUsers()).willReturn(users);

		mockMvc.perform(get("/api/users"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("John Doe")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("Jane Doe")));
	}

	@Test
	void whenGetUserById_thenReturnUser() throws Exception {
		User user = createTestUser(1L, "John Doe", "john@example.com");
		given(userService.getUserById(1L)).willReturn(user);

		mockMvc.perform(get("/api/users/1"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("John Doe")))
				.andExpect(jsonPath("$.email", is("john@example.com")));
	}

	@Test
	void whenGetNonExistentUser_thenReturn404() throws Exception {
		given(userService.getUserById(anyLong()))
				.willThrow(new ResourceNotFoundException("User not found"));

		mockMvc.perform(get("/api/users/999"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("User not found")));
	}

	@Test
	void whenCreateUser_thenReturnCreatedUser() throws Exception {
		User newUser = createTestUser(null, "John Doe", "john@example.com");
		User createdUser = createTestUser(1L, "John Doe", "john@example.com");

		given(userService.createUser(any(User.class))).willReturn(createdUser);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUser)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("John Doe")))
				.andExpect(jsonPath("$.email", is("john@example.com")));
	}

	@Test
	void whenCreateUserWithDuplicateEmail_thenReturn409() throws Exception {
		User newUser = createTestUser(null, "John Doe", "john@example.com");
		given(userService.createUser(any(User.class)))
				.willThrow(new DuplicateResourceException("Email already exists"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(newUser)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isConflict())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Email already exists")));
	}

	@Test
	void whenCreateUserWithInvalidEmail_thenReturn400() throws Exception {
		User invalidUser = createTestUser(null, "John Doe", "invalid-email");

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidUser)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.email", is("Invalid email format")));
	}

	@Test
	void whenCreateUserWithEmptyName_thenReturn400() throws Exception {
		User invalidUser = createTestUser(null, "", "john@example.com");

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidUser)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("Validation failed")))
				.andExpect(jsonPath("$.errors.name", is("Name is required")));
	}

	@Test
	void whenUpdateUser_thenReturnUpdatedUser() throws Exception {
		User userToUpdate = createTestUser(1L, "Updated John", "john.updated@example.com");
		given(userService.updateUser(anyLong(), any(User.class))).willReturn(userToUpdate);

		mockMvc.perform(put("/api/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userToUpdate)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", is("Updated John")))
				.andExpect(jsonPath("$.email", is("john.updated@example.com")));
	}

	@Test
	void whenUpdateNonExistentUser_thenReturn404() throws Exception {
		User userToUpdate = createTestUser(999L, "John Doe", "john@example.com");
		given(userService.updateUser(anyLong(), any(User.class)))
				.willThrow(new ResourceNotFoundException("User not found"));

		mockMvc.perform(put("/api/users/999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(userToUpdate)))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("User not found")));
	}

	@Test
	void whenDeleteUser_thenReturn204() throws Exception {
		doNothing().when(userService).deleteUser(anyLong());

		mockMvc.perform(delete("/api/users/1"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNoContent());
	}

	@Test
	void whenDeleteNonExistentUser_thenReturn404() throws Exception {
		doThrow(new ResourceNotFoundException("User not found"))
				.when(userService).deleteUser(anyLong());

		mockMvc.perform(delete("/api/users/999"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message", is("User not found")));
	}

	private User createTestUser(Long id, String name, String email) {
		return User.builder()
				.id(id)
				.name(name)
				.email(email)
				.build();
	}
}