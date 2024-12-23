package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DTOs.*;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<Object> createAuthToken(@RequestBody JwtRequest authRequest) {
		return authService.createAuthToken(authRequest);
	}

	@PostMapping("/register")
	public ResponseEntity<Object> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
		return authService.createNewUser(registrationUserDto);
	}
}