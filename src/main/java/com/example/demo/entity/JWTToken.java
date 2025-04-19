package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="jwt_tokens")
public class JWTToken {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer token_id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@Column
	private String token;
	
	@Column
	private LocalDateTime createdAt;
	
	@Column
	private LocalDateTime expiresAt;
	
	public JWTToken() {
		
	}

	public JWTToken(Integer token_id, User user, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
		super();
		this.token_id = token_id;
		this.user = user;
		this.token = token;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public JWTToken(User user, String token, LocalDateTime createdAt, LocalDateTime expiresAt) {
		super();
		this.user = user;
		this.token = token;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public Integer getToken_id() {
		return token_id;
	}

	public void setToken_id(Integer token_id) {
		this.token_id = token_id;
	}

	public User getUser_id() {
		return user;
	}

	public void setUser_id(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	 
	
}
