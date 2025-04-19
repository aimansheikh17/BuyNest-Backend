package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.JWTToken;

import jakarta.transaction.Transactional;

@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {

	@Query("SELECT t FROM JWTToken t WHERE t.user.user_id=:user_id")
	public JWTToken findByuserid(int user_id);

	public Optional<JWTToken> findByToken(String token);
	
	
	
	@Modifying
	@Transactional
	@Query("Delete from JWTToken t where t.user.user_id=:user_id")
	public void deleteByUserId(int user_id);
		
}

