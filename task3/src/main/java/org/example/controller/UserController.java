package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

	@ModelAttribute
	public void addAttributes(HttpServletRequest request, Model model) {
		model.addAttribute("currentUri", request.getRequestURI());
	}

	// Home page endpoint - accessible to all users
	@GetMapping("/")
	public String index(@AuthenticationPrincipal OAuth2User principal, Model model) {
		if (principal != null) {
			model.addAttribute("user", principal);
		}
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("loginTitle", "Login");  // Add some model attributes
		return "login";
	}

	@GetMapping("/error")
	public String handleError() {
		log.error("Error page accessed");
		return "error";
	}

	// User profile endpoint - displays authenticated user's information
	@GetMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal, Model model) {
		model.addAttribute("name", principal.getAttribute("name"));
		model.addAttribute("email", principal.getAttribute("email"));
		model.addAttribute("picture", principal.getAttribute("avatar_url")); // Changed from 'avatar' to 'picture'
		return "user";
	}

	// Admin dashboard endpoint - only accessible to users with ADMIN role
	@GetMapping("/admin")
	public String admin(@AuthenticationPrincipal Object principal, Model model) {
		if (principal instanceof OAuth2User oauth2User) {
			String email = oauth2User.getAttribute("email");
			model.addAttribute("email", email);

			// Using array syntax to make the parameter type explicit
			log.info("Admin panel accessed by OAuth2 user: {}", new Object[]{email});
		} else if (principal instanceof UserDetails userDetails) {
			String username = userDetails.getUsername();
			model.addAttribute("email", username);

			// Using array syntax to make the parameter type explicit
			log.info("Admin panel accessed by regular user: {}", new Object[]{username});
		}

		return "admin";
	}


}