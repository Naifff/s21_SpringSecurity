package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.DTOs.RegistrationUserDto;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final RoleService roleService;
	private final PasswordEncoder passwordEncoder;

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						String.format("User '%s' not found", username)
				));
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.getRoles().stream()
						.map(role -> new SimpleGrantedAuthority(role.getName()))
						.toList()
		);
	}

	@Transactional
	public User createNewUser(RegistrationUserDto registrationUserDto) {
		User user = new User();
		user.setUsername(registrationUserDto.getUsername());
		user.setEmail(registrationUserDto.getEmail());
		user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
		user.setRoles(List.of(roleService.getUserRole()));
		return userRepository.save(user);
	}
}