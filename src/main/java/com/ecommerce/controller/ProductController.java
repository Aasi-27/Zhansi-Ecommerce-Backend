package com.ecommerce.controller;

import com.ecommerce.dto.ProductDTO.*;
import com.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/products")
	public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false) String category,
			@RequestParam(required = false) String search) {

		if (category != null && !category.isEmpty()) {
			return ResponseEntity.ok(productService.getProductsByCategory(category));
		}
		if (search != null && !search.isEmpty()) {
			return ResponseEntity.ok(productService.searchProducts(search));
		}
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(productService.getProductById(id));
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	// Admin only
	@PostMapping("/admin/products")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
		return ResponseEntity.ok(productService.createProduct(request));
	}

	@PutMapping("/admin/products/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
		try {
			return ResponseEntity.ok(productService.updateProduct(id, request));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/admin/products/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
		try {
			productService.deleteProduct(id);
			return ResponseEntity.ok("Product deleted successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
