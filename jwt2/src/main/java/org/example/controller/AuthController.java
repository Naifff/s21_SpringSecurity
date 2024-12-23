package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserRegistrationDto;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("user", new UserRegistrationDto());
		return "register";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute("user") UserRegistrationDto registrationDto) {
		try {
			userService.createUser(registrationDto.getUsername(), registrationDto.getPassword());
			return "redirect:/login?registered";
		} catch (RuntimeException e) {
			return "redirect:/register?error";
		}
	}
}