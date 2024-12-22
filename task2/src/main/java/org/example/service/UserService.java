package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	@Value("${security.max-failed-attempts}")
	private int maxFailedAttempts;

	@Value("${security.account-lock-duration-minutes}")
	private int lockDurationMinutes;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}

	@Transactional
	public void increaseFailedAttempts(User user) {
		int newFailAttempts = user.getFailedAttempts() + 1;
		userRepository.updateFailedAttempts(newFailAttempts, user.getUsername());

		// Lock the account if max failed attempts reached
		if (newFailAttempts >= maxFailedAttempts) {
			lockUser(user);
			log.warn("User account locked due to {} failed attempts: {}", maxFailedAttempts, user.getUsername());
		}
	}

	@Transactional
	public void resetFailedAttempts(String username) {
		userRepository.updateFailedAttempts(0, username);
	}

	@Transactional
	public void lockUser(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(LocalDateTime.now());
		userRepository.save(user);
	}

	@Transactional
	public boolean unlockWhenTimeExpired(User user) {
		LocalDateTime lockTime = user.getLockTime();
		if (lockTime != null && lockTime.plusMinutes(lockDurationMinutes).isBefore(LocalDateTime.now())) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempts(0);
			userRepository.save(user);
			log.info("User account automatically unlocked after lock duration: {}", user.getUsername());
			return true;
		}
		return false;
	}

	@Transactional
	public void manuallyUnlockUser(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		user.ifPresent(u -> {
			u.setAccountNonLocked(true);
			u.setLockTime(null);
			u.setFailedAttempts(0);
			userRepository.save(u);
			log.info("User account manually unlocked: {}", username);
		});
	}
}