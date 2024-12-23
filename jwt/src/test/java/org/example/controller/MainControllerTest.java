package org.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
class MainControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenUnsecuredEndpoint_thenAllowAccess() throws Exception {
		mockMvc.perform(get("/api/v1/unsecured"))
				.andExpect(status().isOk())
				.andExpect(content().string("Unsecured data"));
	}

	@Test
	@WithMockUser
	void whenSecuredEndpoint_thenAllowAuthenticatedAccess() throws Exception {
		mockMvc.perform(get("/api/v1/secured"))
				.andExpect(status().isOk())
				.andExpect(content().string("Secured data"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void whenAdminEndpoint_thenAllowAdminAccess() throws Exception {
		mockMvc.perform(get("/api/v1/admin"))
				.andExpect(status().isOk())
				.andExpect(content().string("Admin data"));
	}

	@Test
	@WithMockUser(username = "testuser")
	void whenInfoEndpoint_thenReturnUsername() throws Exception {
		mockMvc.perform(get("/api/v1/info"))
				.andExpect(status().isOk())
				.andExpect(content().string("testuser"));
	}
}