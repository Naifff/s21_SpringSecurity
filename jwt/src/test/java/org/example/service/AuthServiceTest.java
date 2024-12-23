package org.example.service;

import org.example.dto.DTOs;
import org.example.utils.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@Mock
	private UserService userService;

	@Mock
	private JwtTokenUtils jwtTokenUtils;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthService authService;

	@Test
	void whenValidCredentials_thenAuthTokenShouldBeCreated() {
		DTOs.JwtRequest request = new DTOs.JwtRequest();
		request.setUsername("test");
		request.setPassword("password");

		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("test")
				.password("encoded")
				.authorities("ROLE_USER")
				.build();

		given(userService.loadUserByUsername("test")).willReturn(userDetails);
		given(jwtTokenUtils.generateToken(userDetails)).willReturn("token");

		ResponseEntity<?> response = authService.createAuthToken(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isInstanceOf(DTOs.JwtResponse.class);
		assertThat(((DTOs.JwtResponse)response.getBody()).getToken()).isEqualTo("token");
	}
}