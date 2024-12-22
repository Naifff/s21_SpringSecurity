package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		String requestId = generateRequestId();
		LocalDateTime timestamp = LocalDateTime.now();

		// Log the incoming request
		logRequest(request, requestId, timestamp);

		try {
			filterChain.doFilter(request, response);
		} finally {
			// Log the response
			logResponse(response, requestId, timestamp);
		}
	}

	private void logRequest(HttpServletRequest request, String requestId, LocalDateTime timestamp) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth != null ? auth.getName() : "anonymous";

		log.info("[{}] {} Request: {} {} from IP: {} by user: {} at {}",
				requestId,
				"→",
				request.getMethod(),
				request.getRequestURI(),
				request.getRemoteAddr(),
				username,
				timestamp.format(formatter)
		);
	}

	private void logResponse(HttpServletResponse response, String requestId, LocalDateTime timestamp) {
		log.info("[{}] {} Response: status {} at {}",
				requestId,
				"←",
				response.getStatus(),
				timestamp.format(formatter)
		);
	}

	private String generateRequestId() {
		return String.format("%08X", (int) (Math.random() * Integer.MAX_VALUE));
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		// Don't log requests to static resources
		String path = request.getRequestURI();
		return path.contains("/static/") ||
				path.contains("/favicon.ico") ||
				path.contains("/h2-console");
	}
}