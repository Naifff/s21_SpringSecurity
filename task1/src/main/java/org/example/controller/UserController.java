package org.example.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.dto.Views;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	@JsonView(Views.UserSummary.class)
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	@JsonView(Views.UserDetails.class)
	public User getUserById(@PathVariable Long id) {
		return userService.getUserById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(Views.UserDetails.class)
	public User createUser(@Valid @RequestBody User user) {
		return userService.createUser(user);
	}

	@PutMapping("/{id}")
	@JsonView(Views.UserDetails.class)
	public User updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
		return userService.updateUser(id, userDetails);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
}
