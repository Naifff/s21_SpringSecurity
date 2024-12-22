package org.example.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JWTUtils {

	private SecretKey secretKey;
	private final long jwtExpiration;
	private final long refreshExpiration;

	public JWTUtils(
			@Value("${jwt.secret}") String secretKey,
			@Value("${jwt.expiration}") long jwtExpiration,
			@Value("${jwt.refresh-token.expiration}") long refreshExpiration
	) {
		try {
			// First attempt to decode as Base64
			byte[] keyBytes = Decoders.BASE64.decode(secretKey);
			this.secretKey = Keys.hmacShaKeyFor(keyBytes);
		} catch (Exception e) {
			// If Base64 decoding fails, use the string directly to generate a key
			this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
		}
		this.jwtExpiration = jwtExpiration;
		this.refreshExpiration = refreshExpiration;
	}

	// Extracts the username from the JWT token
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Generic method to extract any claim from the token
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// Generates a token with only the username as a claim
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	// Generates a token with additional claims and username
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildToken(extraClaims, userDetails, jwtExpiration);
	}

	// Generates a refresh token
	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, refreshExpiration);
	}

	// Core token building logic
	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
		return Jwts.builder()
				.claims(extraClaims)
				.subject(userDetails.getUsername())
				.claim("authorities", userDetails.getAuthorities())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(secretKey, Jwts.SIG.HS256)
				.compact();
	}

	// Validates if a token is valid for a given user
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	}

	// Checks if a token has expired
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// Extracts the expiration date from a token
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// Extracts all claims from a token
	private Claims extractAllClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (ExpiredJwtException e) {
			log.error("JWT token has expired: {}", e.getMessage());
			throw e;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			throw e;
		} catch (UnsupportedJwtException e) {
			log.error("Unsupported JWT token: {}", e.getMessage());
			throw e;
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw e;
		}
	}
}