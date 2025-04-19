package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.ProductImages;
import com.example.demo.entity.Products;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductService {

	ProductRepository productRepo;
	ProductImageRepository productImageRepo;
	CategoryRepository categoryRepo;
	
	public ProductService(ProductRepository productRepo,ProductImageRepository productImageRepo, CategoryRepository categoryRepo) {
		this.productImageRepo=productImageRepo;
		this.productRepo=productRepo;
		this.categoryRepo=categoryRepo;
	}
	
    public List<Products> getProductsByCategory(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            Optional<Category> categoryOpt = categoryRepo.findByCategoryName(categoryName);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                return productRepo.findByCategory_CategoryId(category.getCategoryId());
            } else {
                throw new RuntimeException("Category not found");
            }
        } else {
            return productRepo.findAll();
        }
    }
    
    public List<String> getProductImages(Integer productId) {
        List<ProductImages> productImages = productImageRepo.findByProduct_ProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImages image : productImages) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
	
	
}
