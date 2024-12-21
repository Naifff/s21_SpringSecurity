package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().disable() // Отключаем CSRF для тестов
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/api/**").permitAll() // Разрешаем доступ к API без аутентификации
						.anyRequest().authenticated() // Все остальные URL требуют аутентификации
				)
				.formLogin(login -> login
						.loginPage("/login")
						.permitAll()
				)
				.logout(logout -> logout.permitAll()); // Разрешаем выход без аутентификации
		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		var userDetailsManager = new InMemoryUserDetailsManager();

		var user = User.withUsername("user")
				.password("{noop}password") // {noop} указывает на отсутствие кодирования пароля
				.roles("USER")
				.build();

		userDetailsManager.createUser(user);

		return userDetailsManager;
	}
}