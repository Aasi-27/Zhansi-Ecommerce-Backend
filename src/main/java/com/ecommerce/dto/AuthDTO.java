package com.ecommerce.dto;

public class AuthDTO {

	public static class RegisterRequest {
		private String username;
		private String email;
		private String password;
		private String role; // ADMIN or USER

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}
	}

	public static class LoginRequest {
		private String username;
		private String password;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	public static class AuthResponse {
		private String token;
		private String username;
		private String role;
		private Long userId;

		public AuthResponse(String token, String username, String role, Long userId) {
			this.token = token;
			this.username = username;
			this.role = role;
			this.userId = userId;
		}

		public String getToken() {
			return token;
		}

		public String getUsername() {
			return username;
		}

		public String getRole() {
			return role;
		}

		public Long getUserId() {
			return userId;
		}
	}

	public static class MessageResponse {
		private String message;

		public MessageResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
