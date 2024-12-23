package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ValidatedDTOs {
	@Data
	public static class JwtRequest {
		@NotBlank(message = "Username cannot be empty")
		@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
		private String username;

		@NotBlank(message = "Password cannot be empty")
		@Size(min = 6, max = 80, message = "Password must be between 6 and 80 characters")
		private String password;
	}

	@Data
	public static class RegistrationUserDto {
		@NotBlank(message = "Username cannot be empty")
		@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
		private String username;

		@NotBlank(message = "Password cannot be empty")
		@Size(min = 6, max = 80, message = "Password must be between 6 and 80 characters")
		private String password;

		@NotBlank(message = "Password confirmation cannot be empty")
		private String confirmPassword;

		@NotBlank(message = "Email cannot be empty")
		@Email(message = "Invalid email format")
		@Size(max = 50, message = "Email must not exceed 50 characters")
		private String email;
	}
}