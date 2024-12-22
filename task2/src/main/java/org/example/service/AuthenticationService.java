package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthenticationRequest;
import org.example.dto.AuthenticationResponse;
import org.example.dto.RegisterRequest;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.security.JWTUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtils jwtUtils;
	private final AuthenticationManager authenticationManager;

	@Transactional
	public AuthenticationResponse register(RegisterRequest request) {
		// Check if username is already taken
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException("Username is already taken");
		}

		// Create new user
		var user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER); // Default role for new registrations

		userRepository.save(user);
		log.info("New user registered: {}", user.getUsername());

		// Generate tokens
		var accessToken = jwtUtils.generateToken(user);
		var refreshToken = jwtUtils.generateRefreshToken(user);

		return new AuthenticationResponse(accessToken, refreshToken);
	}

	@Transactional
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		try {
			// Attempt authentication
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(),
							request.getPassword()
					)
			);

			// Load user and check lock status
			var user = (User) userService.loadUserByUsername(request.getUsername());

			if (!user.isAccountNonLocked()) {
				if (userService.unlockWhenTimeExpired(user)) {
					log.info("Account unlocked after timeout: {}", user.getUsername());
				} else {
					throw new IllegalStateException("Account is locked");
				}
			}

			// Reset failed attempts on successful login
			userService.resetFailedAttempts(user.getUsername());
			log.info("User successfully authenticated: {}", user.getUsername());

			// Generate new tokens
			var accessToken = jwtUtils.generateToken(user);
			var refreshToken = jwtUtils.generateRefreshToken(user);

			return new AuthenticationResponse(accessToken, refreshToken);

		} catch (BadCredentialsException e) {
			// Handle failed login attempt
			var user = (User) userService.loadUserByUsername(request.getUsername());
			userService.increaseFailedAttempts(user);
			log.warn("Failed login attempt for user: {}", request.getUsername());
			throw e;
		}
	}

	@Transactional
	public AuthenticationResponse refreshToken(String refreshToken) {
		final String username = jwtUtils.extractUsername(refreshToken);
		if (username == null) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		var user = (User) userService.loadUserByUsername(username);
		if (!jwtUtils.isTokenValid(refreshToken, user)) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		var accessToken = jwtUtils.generateToken(user);
		log.info("Access token refreshed for user: {}", username);

		return new AuthenticationResponse(accessToken, refreshToken);
	}
}