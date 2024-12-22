package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JWTUtils jwtUtils;
	private final UserService userService;
	private final TokenBlacklistService tokenBlacklistService;

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		try {
			// Extract Authorization header
			final String authHeader = request.getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			}

			// Extract JWT token
			final String jwt = authHeader.substring(7);

			// Check if token is blacklisted
			if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
				log.warn("Attempt to use blacklisted token");
				filterChain.doFilter(request, response);
				return;
			}

			final String username = jwtUtils.extractUsername(jwt);

			// Process authentication if user is not already authenticated
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userService.loadUserByUsername(username);

				// Validate token and set authentication if valid
				if (jwtUtils.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities()
					);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);

					log.debug("Successfully authenticated user: {}", username);
				} else {
					log.warn("Invalid JWT token for user: {}", username);
				}
			}
		} catch (Exception e) {
			log.error("Cannot authenticate user: {}", e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		// Skip authentication for login, register and refresh token endpoints
		return path.contains("/api/auth/login") ||
				path.contains("/api/auth/register") ||
				path.contains("/api/auth/refresh-token");
	}
}