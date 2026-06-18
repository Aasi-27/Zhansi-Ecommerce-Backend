package com.ecommerce.controller;

import com.ecommerce.dto.OrderDTO.*;
import com.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/orders/place")
	public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody(required = false) PlaceOrderRequest request) {
		try {
			return ResponseEntity.ok(orderService.placeOrder(userDetails.getUsername(), request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/orders/my")
	public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(orderService.getMyOrders(userDetails.getUsername()));
	}

	@GetMapping("/admin/orders")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<OrderResponse>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrders());
	}

	@PutMapping("/admin/orders/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
		try {
			return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
