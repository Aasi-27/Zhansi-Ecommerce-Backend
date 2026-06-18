package com.ecommerce.service;

import com.ecommerce.dto.ProfileDTO.*;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

	private final UserRepository userRepository;

	public ProfileService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public ProfileResponse getProfile(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return toResponse(user);
	}

	public ProfileResponse updateProfile(String username, UpdateProfileRequest request) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		if (request.getFullName() != null && !request.getFullName().isBlank()) {
			user.setFullName(request.getFullName());
		}
		if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
			user.setMobileNumber(request.getMobileNumber());
		}
		if (request.getLocation() != null && !request.getLocation().isBlank()) {
			user.setLocation(request.getLocation());
		}
		if (request.getEmail() != null && !request.getEmail().isBlank()) {
			// Check if email already taken by someone else
			userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
				if (!existing.getUsername().equals(username)) {
					throw new RuntimeException("Email already in use");
				}
			});
			user.setEmail(request.getEmail());
		}

		return toResponse(userRepository.save(user));
	}

	private ProfileResponse toResponse(User user) {
		ProfileResponse res = new ProfileResponse();
		res.setId(user.getId());
		res.setUsername(user.getUsername());
		res.setEmail(user.getEmail());
		res.setFullName(user.getFullName());
		res.setMobileNumber(user.getMobileNumber());
		res.setLocation(user.getLocation());
		res.setRole(user.getRole().name());
		res.setCreatedAt(user.getCreatedAt());
		return res;
	}
}
