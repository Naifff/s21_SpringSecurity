package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.JwtRequest;
import org.example.dtos.JwtResponse;
import org.example.dtos.RegistrationUserDto;
import org.example.dtos.UserDto;
import org.example.entities.User;
import org.example.exceptions.AppError;
import org.example.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final Logger log = LoggerFactory.getLogger(AuthService.class);

	private final UserService userService;
	private final JwtTokenUtils jwtTokenUtils;
	private final AuthenticationManager authenticationManager;

	public ResponseEntity<?> createAuthToken(JwtRequest authRequest) {
		try {
			log.info("Attempting authentication for user: {}", authRequest.getUsername());
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
					authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			log.warn("Failed authentication attempt for user: {}", authRequest.getUsername());
			userService.increaseFailedAttempts(authRequest.getUsername());
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect username or password"));
		}

		User user = userService.findByUsername(authRequest.getUsername())
				.orElseThrow(() -> new BadCredentialsException("User not found"));

		if (!user.isAccountNonLocked()) {
			log.warn("Login attempt for locked account: {}", authRequest.getUsername());
			return ResponseEntity
					.status(HttpStatus.FORBIDDEN)
					.body(new AppError(HttpStatus.FORBIDDEN.value(), "Account is locked"));
		}

		UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
		String token = jwtTokenUtils.generateToken(userDetails);
		userService.resetFailedAttempts(authRequest.getUsername());
		log.info("Successful authentication for user: {}", authRequest.getUsername());
		return ResponseEntity.ok(new JwtResponse(token));
	}

	public ResponseEntity<?> createNewUser(RegistrationUserDto registrationUserDto) {
		if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
			log.warn("Registration failed: passwords don't match for username: {}", registrationUserDto.getUsername());
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new AppError(HttpStatus.BAD_REQUEST.value(), "Passwords don't match"));
		}
		if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
			log.warn("Registration failed: username already exists: {}", registrationUserDto.getUsername());
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new AppError(HttpStatus.BAD_REQUEST.value(), "Username already exists"));
		}
		User user = userService.createNewUser(registrationUserDto);
		log.info("Successfully registered new user: {}", registrationUserDto.getUsername());
		return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.isAccountNonLocked()));
	}
}