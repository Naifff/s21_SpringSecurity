package org.example.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<AppError> handleBadCredentialsException(BadCredentialsException ex) {
		log.error("Authentication failed: {}", ex.getMessage());
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), new Date()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<AppError> handleAuthenticationException(AuthenticationException ex) {
		log.error("Authentication error: {}", ex.getMessage());
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", new Date()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<AppError> handleAccessDeniedException(AccessDeniedException ex) {
		log.error("Access denied: {}", ex.getMessage());
		return new ResponseEntity<>(
				new AppError(HttpStatus.FORBIDDEN.value(), "Access denied", new Date()),
				HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<AppError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		log.error("User not found: {}", ex.getMessage());
		return new ResponseEntity<>(
				new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), new Date()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<AppError> handleException(Exception ex) {
		log.error("Unexpected error occurred: ", ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"An unexpected error occurred. Please contact support.", new Date()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}