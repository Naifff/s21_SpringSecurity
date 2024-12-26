package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/login", "/error", "/webjars/**").permitAll()
						.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")  // Changed from hasRole to hasAuthority
						.anyRequest().authenticated()
				)
				.exceptionHandling(exc -> exc
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
				)
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService))
						.defaultSuccessUrl("/user", true)
				)
				.logout(logout -> logout
						.logoutSuccessUrl("/")              // Specify redirect URL
						.logoutSuccessHandler((request, response, authentication) -> {
							response.sendRedirect("/");
						})
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
				);

		// Allow frames for H2 console
		http.headers(headers -> headers.frameOptions().disable());

		return http.build();
	}
}