package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenUtils {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.lifetime}")
	private Duration jwtLifetime;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();
		claims.put("roles", roles);

		Date issuedDate = new Date();
		Date expiredDate = new Date(issuedDate.getTime() + jwtLifetime.toMillis());

		return Jwts.builder()
				.claims(claims)
				.subject(userDetails.getUsername())
				.issuedAt(issuedDate)
				.expiration(expiredDate)
				.signWith(getSigningKey())
				.compact();
	}

	public String getUsername(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}

	public List<String> getRoles(String token) {
		return getAllClaimsFromToken(token).get("roles", List.class);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}