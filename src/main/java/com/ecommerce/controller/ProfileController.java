package com.ecommerce.controller;

import com.ecommerce.dto.ProfileDTO.*;
import com.ecommerce.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(profileService.getProfile(userDetails.getUsername()));
	}

	@PutMapping
	public ResponseEntity<?> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody UpdateProfileRequest request) {
		try {
			return ResponseEntity.ok(profileService.updateProfile(userDetails.getUsername(), request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
