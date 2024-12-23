package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.DTOs.*;
import org.example.entity.User;
import org.example.exception.AppError;
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
public class AuthServiceImpl implements AuthService {
	private final UserService userService;
	private final JwtTokenUtils jwtTokenUtils;
	private final AuthenticationManager authenticationManager;

	@Override
	public ResponseEntity<Object> createAuthToken(JwtRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
			);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(
					new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"),
					HttpStatus.UNAUTHORIZED
			);
		}

		UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
		String token = jwtTokenUtils.generateToken(userDetails);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	@Override
	public ResponseEntity<Object> createNewUser(RegistrationUserDto registrationUserDto) {
		if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
			return new ResponseEntity<>(
					new AppError(HttpStatus.BAD_REQUEST.value(), "Passwords don't match"),
					HttpStatus.BAD_REQUEST
			);
		}
		if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
			return new ResponseEntity<>(
					new AppError(HttpStatus.BAD_REQUEST.value(), "Username already exists"),
					HttpStatus.BAD_REQUEST
			);
		}
		User user = userService.createNewUser(registrationUserDto);
		return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
	}
}