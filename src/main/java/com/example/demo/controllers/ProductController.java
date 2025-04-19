package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Products;
import com.example.demo.entity.User;
import com.example.demo.services.ProductService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("api/products")
public class ProductController {
	
	@Autowired
	ProductService productService;

	public ProductController(ProductService productService) {
		this.productService=productService;
		
	}
	
	@GetMapping
	public ResponseEntity<Map<String,Object>> getProducts(@RequestParam String category, HttpServletRequest request) 
		{
		try {
	
		User authenticatedUser = (User) request.getAttribute("authenticatedUser");
		if(authenticatedUser==null) {
			return ResponseEntity.status(401).body(Map.of("Error", "unauthorized access"));
		}
		// data get it from service
		
		
		List<Products> products = productService.getProductsByCategory(category);
		
		
		// Build the response
		Map<String, Object> response = new HashMap<>();
		
		
		// Add user info
		
		Map<String, String> userInfo = new HashMap<>();
		userInfo.put("name", authenticatedUser.getUsername());
		userInfo.put("role", authenticatedUser.getRole().name());
		response.put("user", userInfo);
		
		
		// Add product details
		
		List<Map<String, Object>> productList = new ArrayList<>();
		for(Products product : products) {
			Map<String, Object> productDetails = new HashMap<>();
			productDetails.put("product_id", product.getProductId());
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price", product.getPrice());
			productDetails.put("stock", product.getStock());
			
			
			// Fetch product images
			
			List<String> images = productService.getProductImages(product.getProductId());
			productDetails.put("images", images);
			
			productList.add(productDetails);
		}
		response.put("products", productList);
		
		return ResponseEntity.ok(response);
		
		}
		catch(Exception e) {
			return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
		}
		}
	
	}
