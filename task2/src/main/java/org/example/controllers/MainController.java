package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.services.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MainController {
	private final UserService userService;

	@GetMapping("/unsecured")
	public String unsecuredData() {
		return "Unsecured data";
	}

	@GetMapping("/user/info")
	public String userInfo(Principal principal) {
		return principal.getName();
	}

	@GetMapping("/admin/users")
	@Secured("ROLE_ADMIN")
	public String adminData() {
		return "Admin data";
	}

	@PostMapping("/admin/unlock/{username}")
	@Secured("ROLE_ADMIN")
	public void unlockUser(@PathVariable String username) {
		userService.unlock(username);
	}

	@GetMapping("/moderator/data")
	@Secured("ROLE_MODERATOR")
	public String moderatorData() {
		return "Moderator data";
	}
}