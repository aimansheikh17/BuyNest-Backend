package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Products;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {

	
	 
	List<Products> findByCategory_CategoryId(Integer categoryId);
	
	@Query("SELECT p.category.categoryName FROM Products p where p.productId = :productId")
	String findCategoryNameByProductId(int productId);
	
	
}
