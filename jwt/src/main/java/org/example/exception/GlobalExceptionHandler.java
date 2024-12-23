package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AppException.class)
	public ResponseEntity<AppError> handleAppException(AppException ex) {
		AppError error = new AppError(ex.getStatus().value(), ex.getMessage());
		return new ResponseEntity<>(error, ex.getStatus());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<AppError> handleBadCredentialsException() {
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"),
				HttpStatus.UNAUTHORIZED
		);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<AppError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		return new ResponseEntity<>(
				new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
				HttpStatus.NOT_FOUND
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<AppError> handleException(Exception ex) {
		return new ResponseEntity<>(
				new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}