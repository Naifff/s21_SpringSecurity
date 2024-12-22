package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// Endpoint accessible to any authenticated user to view their own profile
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfile(Authentication authentication) {
		User user = (User) userService.loadUserByUsername(authentication.getName());
		// Clear sensitive information before returning
		user.setPassword(null);
		return ResponseEntity.ok(user);
	}

	// Endpoint accessible only to moderators and admins to unlock user accounts
	@PostMapping("/{username}/unlock")
	@PreAuthorize("hasAnyRole('MODERATOR', 'SUPER_ADMIN')")
	public ResponseEntity<String> unlockUserAccount(@PathVariable String username) {
		userService.manuallyUnlockUser(username);
		return ResponseEntity.ok("User account unlocked successfully");
	}

	// Endpoint accessible only to super admins to manage user roles
	@PostMapping("/{username}/role")
	@PreAuthorize("hasRole('SUPER_ADMIN')")
	public ResponseEntity<String> updateUserRole(
			@PathVariable String username,
			@RequestParam Role newRole) {
		// Implementation for role update
		return ResponseEntity.ok("User role updated successfully");
	}
}