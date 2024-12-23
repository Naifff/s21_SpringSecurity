package org.example.service;

import org.example.dto.DTOs.JwtRequest;
import org.example.dto.DTOs.RegistrationUserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
	ResponseEntity<Object> createAuthToken(JwtRequest request);
	ResponseEntity<Object> createNewUser(RegistrationUserDto registrationUserDto);
}