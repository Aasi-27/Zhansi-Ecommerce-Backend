package com.ecommerce.controller;

import com.ecommerce.dto.CartDTO.*;
import com.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
	}

	@PostMapping("/add")
	public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody AddToCartRequest request) {
		try {
			return ResponseEntity.ok(cartService.addToCart(userDetails.getUsername(), request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/item/{itemId}")
	public ResponseEntity<?> updateCartItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemId,
			@RequestBody UpdateCartItemRequest request) {
		try {
			return ResponseEntity.ok(cartService.updateCartItem(userDetails.getUsername(), itemId, request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/item/{itemId}")
	public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
			@PathVariable Long itemId) {
		try {
			return ResponseEntity.ok(cartService.removeFromCart(userDetails.getUsername(), itemId));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/clear")
	public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
		cartService.clearCart(userDetails.getUsername());
		return ResponseEntity.ok("Cart cleared");
	}
}
