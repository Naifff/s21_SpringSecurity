package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.JwtRequest;
import org.example.dtos.JwtResponse;
import org.example.dtos.RegistrationUserDto;
import org.example.dtos.UserDto;
import org.example.entities.User;
import org.example.exceptions.AppError;
import org.example.utils.JwtTokenUtils;
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
	private final UserService userService;
	private final JwtTokenUtils jwtTokenUtils;
	private final AuthenticationManager authenticationManager;

	public JwtResponse createAuthToken(JwtRequest authRequest) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
					authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			userService.increaseFailedAttempts(authRequest.getUsername());
			throw new BadCredentialsException("Incorrect username or password");
		}

		User user = userService.findByUsername(authRequest.getUsername())
				.orElseThrow(() -> new BadCredentialsException("User not found"));

		if (!user.isAccountNonLocked()) {
			throw new BadCredentialsException("Account is locked");
		}

		UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
		String token = jwtTokenUtils.generateToken(userDetails);
		userService.resetFailedAttempts(authRequest.getUsername());
		return new JwtResponse(token);
	}

	public UserDto createNewUser(RegistrationUserDto registrationUserDto) {
		if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
			throw new RuntimeException("Passwords don't match");
		}
		if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		User user = userService.createNewUser(registrationUserDto);
		return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.isAccountNonLocked());
	}
}