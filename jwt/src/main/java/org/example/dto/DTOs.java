package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DTOs {
	@Data
	public static class JwtRequest {
		private String username;
		private String password;
	}

	@Data
	@AllArgsConstructor
	public static class JwtResponse {
		private String token;
	}

	@Data
	public static class RegistrationUserDto {
		private String username;
		private String password;
		private String confirmPassword;
		private String email;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UserDto {
		private Long id;
		private String username;
		private String email;
	}
}