package org.example;

import org.example.dto.DTOs.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SecurityJwtApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private String getBaseUrl() {
		return "http://localhost:" + port + "/api/v1";
	}

	@Test
	void whenLogin_thenGetToken() {
		JwtRequest loginRequest = new JwtRequest();
		loginRequest.setUsername("user");
		loginRequest.setPassword("100");

		ResponseEntity<JwtResponse> response = restTemplate.postForEntity(
				getBaseUrl() + "/auth/login",
				loginRequest,
				JwtResponse.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getToken()).isNotEmpty();
	}

	@Test
	void whenAccessSecuredEndpoint_withoutToken_thenUnauthorized() {
		ResponseEntity<String> response = restTemplate.getForEntity(
				getBaseUrl() + "/secured",
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void whenAccessSecuredEndpoint_withToken_thenSuccess() {
		// First, get token
		JwtRequest loginRequest = new JwtRequest();
		loginRequest.setUsername("user");
		loginRequest.setPassword("100");

		ResponseEntity<JwtResponse> loginResponse = restTemplate.postForEntity(
				getBaseUrl() + "/auth/login",
				loginRequest,
				JwtResponse.class
		);

		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(loginResponse.getBody()).isNotNull();
		String token = loginResponse.getBody().getToken();

		// Then use token to access secured endpoint
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				getBaseUrl() + "/secured",
				HttpMethod.GET,
				entity,
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Secured data");
	}

	@Test
	void whenRegisterNewUser_thenSuccess() {
		RegistrationUserDto registrationDto = new RegistrationUserDto();
		registrationDto.setUsername("newuser");
		registrationDto.setPassword("password");
		registrationDto.setConfirmPassword("password");
		registrationDto.setEmail("new@example.com");

		ResponseEntity<UserDto> response = restTemplate.postForEntity(
				getBaseUrl() + "/auth/register",
				registrationDto,
				UserDto.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getUsername()).isEqualTo("newuser");
	}
}