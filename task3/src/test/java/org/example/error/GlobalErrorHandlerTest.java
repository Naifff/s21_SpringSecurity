package org.example.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorHandlerTest {

	private GlobalErrorHandler errorHandler;
	private MockHttpServletRequest request;

	@BeforeEach
	void setUp() {
		errorHandler = new GlobalErrorHandler();
		request = new MockHttpServletRequest();
	}

	@Test
	void handleAccessDenied_ShouldReturnForbiddenStatus() {
		// Arrange
		AccessDeniedException ex = new AccessDeniedException("Access denied");

		// Act
		ModelAndView mav = errorHandler.handleAccessDenied(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("Access denied"));
		assertEquals(403, mav.getModel().get("status"));
	}

	@Test
	void handleAuthenticationError_ShouldReturnUnauthorizedStatus() {
		// Arrange
		AuthenticationException ex = new AuthenticationException("Authentication failed") {
		};

		// Act
		ModelAndView mav = errorHandler.handleAuthenticationError(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("Authentication failed"));
		assertEquals(401, mav.getModel().get("status"));
	}

	@Test
	void handleOAuth2Error_ShouldReturnUnauthorizedStatus() {
		// Arrange
		OAuth2Error oAuth2Error = new OAuth2Error(
				"invalid_token",
				"The token is invalid",
				"https://example.com/error"
		);
		OAuth2AuthenticationException ex = new OAuth2AuthenticationException(oAuth2Error);

		// Act
		ModelAndView mav = errorHandler.handleOAuth2Error(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("OAuth2 authentication failed"));
		assertEquals(401, mav.getModel().get("status"));
	}

	@Test
	void handleGeneralError_ShouldReturnInternalServerError() {
		// Arrange
		Exception ex = new RuntimeException("Unexpected error");

		// Act
		ModelAndView mav = errorHandler.handleGeneralError(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("unexpected error"));
		assertEquals(500, mav.getModel().get("status"));
	}

	@Test
	void handleCustomError_WithDetailedMessage_ShouldIncludeMessageInResponse() {
		// Arrange
		String detailedMessage = "Custom validation error occurred";
		Exception ex = new IllegalArgumentException(detailedMessage);

		// Act
		ModelAndView mav = errorHandler.handleGeneralError(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("error occurred"));
		assertEquals(500, mav.getModel().get("status"));
	}

	@Test
	void handleNullPointerException_ShouldProvideGenericErrorMessage() {
		// Arrange
		NullPointerException ex = new NullPointerException();

		// Act
		ModelAndView mav = errorHandler.handleGeneralError(ex, request);

		// Assert
		assertNotNull(mav);
		assertEquals("error", mav.getViewName());
		assertTrue(mav.getModel().get("errorMessage").toString()
				.contains("unexpected error"));
		assertEquals(500, mav.getModel().get("status"));
	}
}