package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.dtos.JwtRequest;
import org.example.dtos.JwtResponse;
import org.example.dtos.RegistrationUserDto;
import org.example.dtos.UserDto;
import org.example.exceptions.AppError;
import org.example.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/auth")
	public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
		try {
			JwtResponse response = authService.createAuthToken(authRequest);
			return ResponseEntity.ok(response);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()),
					HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/registration")
	public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
		try {
			UserDto userDto = authService.createNewUser(registrationUserDto);
			return ResponseEntity.ok(userDto);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}
}