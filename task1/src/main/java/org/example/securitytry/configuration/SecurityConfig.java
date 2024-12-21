package org.example.securitytry.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/public/**").permitAll() // Доступ к публичным страницам без аутентификации
						.anyRequest().authenticated() // Все остальные URL требуют аутентификации
				)
				.formLogin(withDefaults()) // Используем встроенную форму логина
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