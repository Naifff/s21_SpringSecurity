package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DTOs.*;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MainController {
	@GetMapping("/unsecured")
	public ResponseEntity<String> unsecuredData() {
		return ResponseEntity.ok("Unsecured data");
	}

	@GetMapping("/secured")
	public ResponseEntity<String> securedData() {
		return ResponseEntity.ok("Secured data");
	}

	@GetMapping("/admin")
	public ResponseEntity<String> adminData() {
		return ResponseEntity.ok("Admin data");
	}

	@GetMapping("/info")
	public ResponseEntity<String> userData(Principal principal) {
		return ResponseEntity.ok(principal.getName());
	}
}