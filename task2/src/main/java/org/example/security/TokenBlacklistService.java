package org.example.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage blacklisted JWT tokens.
 * Maintains a thread-safe map of invalidated tokens and their expiration times.
 * Automatically removes expired tokens to prevent memory leaks.
 */
@Service
@Slf4j
public class TokenBlacklistService {

	// Using ConcurrentHashMap for thread-safety
	private final Map<String, Instant> blacklistedTokens = new ConcurrentHashMap<>();

	/**
	 * Adds a token to the blacklist
	 * @param token The JWT token to blacklist
	 */
	public void blacklistToken(String token) {
		// Store token with current timestamp plus some buffer (e.g., token's natural expiration)
		blacklistedTokens.put(token, Instant.now().plusSeconds(86400)); // 24 hours
		log.debug("Token added to blacklist");
	}

	/**
	 * Checks if a token is blacklisted
	 * @param token The JWT token to check
	 * @return true if the token is blacklisted, false otherwise
	 */
	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.containsKey(token);
	}

	/**
	 * Scheduled task to clean up expired tokens from the blacklist
	 * Runs every hour to prevent memory leaks
	 */
	@Scheduled(fixedRate = 3600000) // Run every hour
	public void cleanupExpiredTokens() {
		Instant now = Instant.now();
		blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
		log.debug("Cleaned up expired tokens from blacklist");
	}
}