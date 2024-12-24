package org.example.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void unsecuredData_Success() throws Exception {
		mockMvc.perform(get("/api/v1/unsecured"))
				.andExpect(status().isOk())
				.andExpect(content().string("Unsecured data"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void userInfo_Success() throws Exception {
		mockMvc.perform(get("/api/v1/user/info"))
				.andExpect(status().isOk());
	}

	@Test
	void userInfo_Unauthorized() throws Exception {
		mockMvc.perform(get("/api/v1/user/info"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminData_Success() throws Exception {
		mockMvc.perform(get("/api/v1/admin/users"))
				.andExpect(status().isOk())
				.andExpect(content().string("Admin data"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void adminData_Forbidden() throws Exception {
		mockMvc.perform(get("/api/v1/admin/users"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "MODERATOR")
	void moderatorData_Success() throws Exception {
		mockMvc.perform(get("/api/v1/moderator/data"))
				.andExpect(status().isOk())
				.andExpect(content().string("Moderator data"));
	}

	@Test
	@WithMockUser(roles = "USER")
	void moderatorData_Forbidden() throws Exception {
		mockMvc.perform(get("/api/v1/moderator/data"))
				.andExpect(status().isForbidden());
	}
}