package com.ecommerce.service;

import com.ecommerce.dto.OrderDTO.*;
import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	public OrderService(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository,
			ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.userRepository = userRepository;
		this.productRepository = productRepository;
	}

	public OrderResponse placeOrder(String username, PlaceOrderRequest request) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

		Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart is empty"));

		if (cart.getItems().isEmpty()) {
			throw new RuntimeException("Cannot place order with empty cart");
		}

		Order order = new Order();
		order.setUser(user);
		order.setStatus(Order.OrderStatus.PLACED);

		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;

		for (CartItem cartItem : cart.getItems()) {
			Product product = cartItem.getProduct();

			if (product.getStock() < cartItem.getQuantity()) {
				throw new RuntimeException("Insufficient stock for: " + product.getName());
			}

			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepository.save(product);

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getPrice());
			orderItems.add(orderItem);

			total = total.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
		}

		order.setItems(orderItems);
		order.setTotalAmount(total);
		order.setShippingAddress(request != null ? request.getAddress() : null);
		order.setPaymentMethod(
				request != null && request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD");
		Order savedOrder = orderRepository.save(order);
		cart.getItems().clear();
		cartRepository.save(cart);

		return toResponse(savedOrder);
	}

	public List<OrderResponse> getMyOrders(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return orderRepository.findByUserOrderByPlacedAtDesc(user).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	public List<OrderResponse> getAllOrders() {
		return orderRepository.findAllByOrderByPlacedAtDesc().stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		order.setStatus(Order.OrderStatus.valueOf(request.getStatus().toUpperCase()));
		return toResponse(orderRepository.save(order));
	}

	private OrderResponse toResponse(Order order) {
		List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
			OrderItemResponse r = new OrderItemResponse();
			r.setProductId(item.getProduct().getId());
			r.setProductName(item.getProduct().getName());
			r.setQuantity(item.getQuantity());
			r.setPrice(item.getPrice());
			r.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			return r;
		}).collect(Collectors.toList());

		OrderResponse res = new OrderResponse();
		res.setId(order.getId());
		res.setUsername(order.getUser().getUsername());
		res.setItems(itemResponses);
		res.setTotalAmount(order.getTotalAmount());
		res.setStatus(order.getStatus().name());
		res.setPlacedAt(order.getPlacedAt());
		res.setShippingAddress(order.getShippingAddress());
		res.setPaymentMethod(order.getPaymentMethod());
		return res;
	}
}
