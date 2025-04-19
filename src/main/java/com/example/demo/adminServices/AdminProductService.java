package com.example.demo.adminServices;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.ProductImages;
import com.example.demo.entity.Products;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class AdminProductService {
	
	
	ProductRepository productRepository;
	
	ProductImageRepository productImageRepository;

	CategoryRepository categoryRepository;
	
    public AdminProductService(ProductRepository productRepository, ProductImageRepository productImageRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
    }


	public Products addProductWithImage(String name, String description, Double price, Integer stock,
			Integer categoryId, String imageUrl) {
	     // Validate the category
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        
     // Create and save the product
        Products product = new Products(name,description,BigDecimal.valueOf(price),stock,category.get(),LocalDateTime.now(),LocalDateTime.now());
        Products savedProduct=productRepository.save(product);
        
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ProductImages productImage = new ProductImages(savedProduct,imageUrl);
            productImageRepository.save(productImage);
		
	}
        else {
        	throw new IllegalArgumentException("Product Image Cannot Be Empty");
        }
        return savedProduct;
	}


	public void deleteProduct(Integer productId) {
		
		  // Check if the product exists
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
		productImageRepository.deleteByProductId(productId);
		productRepository.deleteById(productId);
		
	}
	
}
