package com.example.demo.services;


import com.example.demo.entity.CartItem;
import com.example.demo.entity.ProductImages;
import com.example.demo.entity.Products;
import com.example.demo.entity.User;

import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductImageRepository productImageRepository;

	// Get the total cart item count for a user
	public int getCartItemCount(int user_id) {
		return cartRepository.CountTotalItems(user_id);
	}

	// Add an item to the cart
	public void addToCart(int user_id, int productId, int quantity) {
		User user = userRepository.findById(user_id)
				.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + user_id));

		Products product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		// Fetch cart item for this userId and productId
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(user_id, productId);

		if (existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartRepository.save(cartItem);
		} else {
			CartItem newItem = new CartItem(user, product, quantity);
			cartRepository.save(newItem);
		}
	}

	// Get Cart Items for a User
	public Map<String, Object> getCartItems(int user_id) {
		// Fetch the cart items for the user with product details
		List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(user_id);

		// Create a response map to hold the cart details
		Map<String, Object> response = new HashMap<>();
		User user = userRepository.findById(user_id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		response.put("username", user.getUsername());
		response.put("role", user.getRole().toString());

		// List to hold the product details
		List<Map<String, Object>> products = new ArrayList<>();
		int overallTotalPrice = 0;

		for (CartItem cartItem : cartItems) {
			Map<String, Object> productDetails = new HashMap<>();

			// Get product details
			Products product = cartItem.getProduct();

			// Fetch product images from the ProductImageRepository
			List<ProductImages> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
			String imageUrl = null;

			if (productImages != null && !productImages.isEmpty()) {
				// If there are images, get the first image's URL
				imageUrl = productImages.get(0).getImageUrl();
			} else {
				// Set a default image if no images are available
				imageUrl = "default-image-url";  // You can replace this with your default image URL
			}

			// Populate product details into the map
			productDetails.put("product_id", product.getProductId());
			productDetails.put("image_url", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put("quantity", cartItem.getQuantity());
			productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());

			// Add the product details to the products list
			products.add(productDetails);

			// Add to the overall total price
			overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
		}

		// Prepare the final cart response
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_total_price", overallTotalPrice);

		// Add the cart details to the response
		response.put("cart", cart);

		return response;
	}

	// Update Cart Item Quantity
	public void updateCartItemQuantity(int user_id, int productId, int quantity) {
		User user = userRepository.findById(user_id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Products product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		// Fetch cart item for this userId and productId
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(user_id, productId);

		if (existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			if (quantity == 0) {
				deleteCartItem(user_id, productId);
			} else {
				cartItem.setQuantity(quantity);
				cartRepository.save(cartItem);
			}
		}
	}

	// Delete Cart Item
	public void deleteCartItem(int user_id, int productId) {
		User user = userRepository.findById(user_id)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Products product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		cartRepository.deleteCartItem(user_id, productId);
	}
}
