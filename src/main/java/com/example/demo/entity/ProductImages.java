package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name ="productimages")
public class ProductImages {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer image_id;
	
	@ManyToOne
	@JoinColumn(name="product_id")
	private Products product;
	
	private String imageUrl;

	public ProductImages() {
		
		// TODO Auto-generated constructor stub
	}

	public ProductImages(Integer image_id, Products product, String imageUrl) {
		super();
		this.image_id = image_id;
		this.product = product;
		this.imageUrl = imageUrl;
	}

	public ProductImages(Products product, String imageUrl) {
		super();
		this.product = product;
		this.imageUrl = imageUrl;
	}

	public Integer getImage_id() {
		return image_id;
	}

	public void setImage_id(Integer image_id) {
		this.image_id = image_id;
	}

	public Products getProduct() {
		return product;
	}

	public void setProduct(Products product) {
		this.product = product;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	 

 
}
