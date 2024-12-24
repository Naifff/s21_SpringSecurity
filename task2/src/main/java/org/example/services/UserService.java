package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.RegistrationUserDto;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;
	private final RoleService roleService;
	private final PasswordEncoder passwordEncoder;

	private static final int MAX_FAILED_ATTEMPTS = 3;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
				String.format("User '%s' not found", username)
		));

		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.isAccountNonLocked(),
				true,
				true,
				true,
				user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
		);
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
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

	@Transactional
	public void increaseFailedAttempts(String username) {
		userRepository.incrementLoginAttempts(username);
		Optional<User> userOpt = findByUsername(username);
		if (userOpt.isPresent() && userOpt.get().getLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
			lock(username);
		}
	}

	@Transactional
	public void resetFailedAttempts(String username) {
		userRepository.resetLoginAttempts(username);
	}

	@Transactional
	public void lock(String username) {
		userRepository.updateAccountLock(username, false);
	}

	@Transactional
	public void unlock(String username) {
		userRepository.updateAccountLock(username, true);
		resetFailedAttempts(username);
	}
}