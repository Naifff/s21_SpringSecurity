package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.entities.Role;
import org.example.repositories.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	public Role getUserRole() {
		return roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
	}

	public Role getModeratorRole() {
		return roleRepository.findByName("ROLE_MODERATOR")
				.orElseThrow(() -> new RuntimeException("ROLE_MODERATOR not found"));
	}

	public Role getAdminRole() {
		return roleRepository.findByName("ROLE_ADMIN")
				.orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
	}
}