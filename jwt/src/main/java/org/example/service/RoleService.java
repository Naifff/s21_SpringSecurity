package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Role;
import org.example.repository.RoleRepository;
import org.springframework.stereotype.Service;
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
public class RoleService {
	private final RoleRepository roleRepository;

	public Role getUserRole() {
		return roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("Role ROLE_USER not found"));
	}
}