package com.ecommerce.service;

import com.ecommerce.dto.ProductDTO.*;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
		return toResponse(product);
	}

	public List<ProductResponse> getProductsByCategory(String category) {
		return productRepository.findByCategory(category).stream().map(this::toResponse).collect(Collectors.toList());
	}

	public List<ProductResponse> searchProducts(String name) {
		return productRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse)
				.collect(Collectors.toList());
	}

	public ProductResponse createProduct(ProductRequest request) {
		Product product = new Product();
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		product.setCategory(request.getCategory());
		product.setImageUrl(request.getImageUrl());
		return toResponse(productRepository.save(product));
	}

	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStock(request.getStock());
		product.setCategory(request.getCategory());
		product.setImageUrl(request.getImageUrl());
		return toResponse(productRepository.save(product));
	}

	public void deleteProduct(Long id) {
		productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
		productRepository.deleteById(id);
	}

	private ProductResponse toResponse(Product product) {
		ProductResponse res = new ProductResponse();
		res.setId(product.getId());
		res.setName(product.getName());
		res.setDescription(product.getDescription());
		res.setPrice(product.getPrice());
		res.setStock(product.getStock());
		res.setCategory(product.getCategory());
		res.setImageUrl(product.getImageUrl());
		res.setCreatedAt(product.getCreatedAt());
		return res;
	}
}
