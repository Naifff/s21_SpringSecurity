package org.example.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.example.service.CustomOAuth2UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomOAuth2UserService customOAuth2UserService;

	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	private OAuth2User regularUser;
	private OAuth2User adminUser;

	@BeforeEach
	void setUp() {
		// Setup regular user attributes
		Map<String, Object> regularUserAttributes = new HashMap<>();
		regularUserAttributes.put("name", "Regular User");
		regularUserAttributes.put("email", "user@example.com");
		regularUserAttributes.put("picture", "https://example.com/user.jpg");

		regularUser = new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				regularUserAttributes,
				"email"
		);

		// Setup admin user attributes
		Map<String, Object> adminUserAttributes = new HashMap<>();
		adminUserAttributes.put("name", "Admin User");
		adminUserAttributes.put("email", "admin@example.com");
		adminUserAttributes.put("picture", "https://example.com/admin.jpg");

		adminUser = new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")),
				adminUserAttributes,
				"email"
		);
	}

	@Test
	void user_WhenNotAuthenticated_ShouldRedirectToLogin() throws Exception {
		mockMvc.perform(get("/user"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrlPattern("**/login"));
	}

	@Test
	void user_WhenAuthenticated_ShouldShowUserProfile() throws Exception {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("name", "Test User");
		attributes.put("email", "test@example.com");
		attributes.put("avatar_url", "https://example.com/user.jpg");  // Match GitHub's attribute name

		OAuth2User oauth2User = new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
				attributes,
				"email"
		);

		mockMvc.perform(get("/user")
						.with(oauth2Login().oauth2User(oauth2User)))
				.andExpect(status().isOk())
				.andExpect(view().name("user"))
				.andExpect(model().attributeExists("name", "email", "picture"));
	}

	@Test
	void admin_WhenAdminRole_ShouldShowAdminPanel() throws Exception {
		// Create OAuth2 authentication for admin
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("login", "admin");
		attributes.put("name", "Admin User");
		attributes.put("email", "admin@example.com");
		attributes.put("avatar_url", "https://example.com/avatar.jpg");

		OAuth2User oauth2User = new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")),
				attributes,
				"login"
		);

		mockMvc.perform(get("/admin")
						.with(oauth2Login().oauth2User(oauth2User)))
				.andExpect(status().isOk())
				.andExpect(view().name("admin"));
	}


	@Test
	void index_WhenAuthenticated_ShouldShowUserInfo() throws Exception {
		mockMvc.perform(get("/")
						.with(oauth2Login().oauth2User(regularUser)))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(model().attributeExists("user"));
	}


}