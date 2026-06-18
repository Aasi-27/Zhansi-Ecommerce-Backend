package com.ecommerce.service;

import com.ecommerce.dto.CartDTO.*;
import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductRepository productRepository, UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	private Cart getOrCreateCart(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
		return cartRepository.findByUser(user).orElseGet(() -> {
			Cart newCart = new Cart();
			newCart.setUser(user);
			return cartRepository.save(newCart);
		});
	}

	public CartResponse getCart(String username) {
		Cart cart = getOrCreateCart(username);
		return toResponse(cart);
	}

	public CartResponse addToCart(String username, AddToCartRequest request) {
		Cart cart = getOrCreateCart(username);
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found"));

		if (product.getStock() < request.getQuantity()) {
			throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
		}

		Optional<CartItem> existingItem = cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId())).findFirst();

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			int newQty = item.getQuantity() + request.getQuantity();
			if (product.getStock() < newQty) {
				throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
			}
			item.setQuantity(newQty);
			cartItemRepository.save(item);
		} else {
			CartItem item = new CartItem();
			item.setCart(cart);
			item.setProduct(product);
			item.setQuantity(request.getQuantity());
			item.setPrice(product.getPrice());
			cart.getItems().add(item);
			cartItemRepository.save(item);
		}

		return toResponse(cartRepository.findById(cart.getId()).orElse(cart));
	}

	public CartResponse updateCartItem(String username, Long itemId, UpdateCartItemRequest request) {
		Cart cart = getOrCreateCart(username);
		CartItem item = cartItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Cart item not found"));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new RuntimeException("Item does not belong to your cart");
		}

		if (request.getQuantity() <= 0) {
			cart.getItems().remove(item);
			cartItemRepository.delete(item);
		} else {
			if (item.getProduct().getStock() < request.getQuantity()) {
				throw new RuntimeException("Insufficient stock");
			}
			item.setQuantity(request.getQuantity());
			cartItemRepository.save(item);
		}

		return toResponse(cartRepository.findById(cart.getId()).orElse(cart));
	}

	public CartResponse removeFromCart(String username, Long itemId) {
		Cart cart = getOrCreateCart(username);
		CartItem item = cartItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Cart item not found"));

		if (!item.getCart().getId().equals(cart.getId())) {
			throw new RuntimeException("Item does not belong to your cart");
		}

		cart.getItems().remove(item);
		cartItemRepository.delete(item);
		return toResponse(cartRepository.findById(cart.getId()).orElse(cart));
	}

	public void clearCart(String username) {
		Cart cart = getOrCreateCart(username);
		cart.getItems().clear();
		cartRepository.save(cart);
	}

	private CartResponse toResponse(Cart cart) {
		List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
			CartItemResponse r = new CartItemResponse();
			r.setId(item.getId());
			r.setProductId(item.getProduct().getId());
			r.setProductName(item.getProduct().getName());
			r.setImageUrl(item.getProduct().getImageUrl());
			r.setPrice(item.getPrice());
			r.setQuantity(item.getQuantity());
			r.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
			return r;
		}).collect(Collectors.toList());

		BigDecimal total = itemResponses.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		CartResponse res = new CartResponse();
		res.setCartId(cart.getId());
		res.setItems(itemResponses);
		res.setTotalAmount(total);
		res.setTotalItems(cart.getItems().stream().mapToInt(CartItem::getQuantity).sum());
		return res;
	}
}
