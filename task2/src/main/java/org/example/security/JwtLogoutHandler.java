package org.example.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Custom LogoutHandler implementation for JWT-based authentication.
 * This handler is responsible for invalidating JWT tokens during logout.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtLogoutHandler implements LogoutHandler {

	private final TokenBlacklistService tokenBlacklistService;

	@Override
	public void logout(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) {
		// Extract the JWT token from the Authorization header
		final String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.warn("Logout requested without valid Authorization header");
			return;
		}

		// Get the token and add it to the blacklist
		final String jwt = authHeader.substring(7);
		tokenBlacklistService.blacklistToken(jwt);
		log.info("Successfully logged out and blacklisted token");
	}
}