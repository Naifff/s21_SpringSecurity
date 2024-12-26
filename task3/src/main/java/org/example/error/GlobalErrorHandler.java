package org.example.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		log.error("Access denied error occurred: {}", ex.getMessage());

		ModelAndView mav = new ModelAndView();
		mav.addObject("errorMessage", "Access denied: You don't have permission to access this resource");
		mav.addObject("status", HttpStatus.FORBIDDEN.value());
		mav.setViewName("error");
		return mav;
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView handleAuthenticationError(AuthenticationException ex, HttpServletRequest request) {
		log.error("Authentication error occurred: {}", ex.getMessage());

		ModelAndView mav = new ModelAndView();
		mav.addObject("errorMessage", "Authentication failed: Please log in again");
		mav.addObject("status", HttpStatus.UNAUTHORIZED.value());
		mav.setViewName("error");
		return mav;
	}

	@ExceptionHandler(OAuth2AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView handleOAuth2Error(OAuth2AuthenticationException ex, HttpServletRequest request) {
		log.error("OAuth2 authentication error occurred: {}", ex.getMessage());

		ModelAndView mav = new ModelAndView();
		mav.addObject("errorMessage", "OAuth2 authentication failed: " + ex.getMessage());
		mav.addObject("status", HttpStatus.UNAUTHORIZED.value());
		mav.setViewName("error");
		return mav;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleGeneralError(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error occurred: {}", ex.getMessage());

		ModelAndView mav = new ModelAndView();
		mav.addObject("errorMessage", "An unexpected error occurred");
		mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		mav.setViewName("error");
		return mav;
	}
}