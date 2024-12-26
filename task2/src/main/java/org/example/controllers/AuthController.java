package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dtos.JwtRequest;
import org.example.dtos.RegistrationUserDto;
import org.example.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/auth")
	public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
		return authService.createAuthToken(authRequest);
	}

	@PostMapping("/registration")
	public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
		return authService.createNewUser(registrationUserDto);
	}
}