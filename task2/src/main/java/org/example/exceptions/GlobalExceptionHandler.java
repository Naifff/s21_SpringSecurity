package org.example.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<AppError> handleExpiredJwtException(ExpiredJwtException ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), "Token expired", new Date()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	public ResponseEntity<AppError> handleSignatureException(SignatureException ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid token signature", new Date()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	public ResponseEntity<AppError> handleAccessDeniedException(AccessDeniedException ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.FORBIDDEN.value(), "Access denied", new Date()),
				HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	public ResponseEntity<AppError> handleBadCredentialsException(BadCredentialsException ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", new Date()),
				HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	public ResponseEntity<AppError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), new Date()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<AppError> handleException(Exception ex) {
		log.error(ex.getMessage(), ex);
		return new ResponseEntity<>(
				new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", new Date()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}