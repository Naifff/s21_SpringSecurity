package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.exception.DuplicateResourceException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	public User createUser(User user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new DuplicateResourceException("Email already exists: " + user.getEmail());
		}
		return userRepository.save(user);
	}

	public User updateUser(Long id, User userDetails) {
		User user = getUserById(id);
		user.setName(userDetails.getName());
		user.setEmail(userDetails.getEmail());
		return userRepository.save(user);
	}

	public void deleteUser(Long id) {
		User user = getUserById(id);
		userRepository.delete(user);
	}
}
