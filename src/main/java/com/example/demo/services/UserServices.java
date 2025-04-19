package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepo;

@Service
public class UserServices {
	
	UserRepo userRepo;
	
	BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	public UserServices(UserRepo userRepo) {
		this.userRepo = userRepo;
		this.passwordEncoder = new BCryptPasswordEncoder();
		
	}


	public User registerUser(User user) {
		// TODO Auto-generated method stub
		if (userRepo.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("username is already present");
		}
		if (userRepo.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already registered");
		}
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
	}
	

}
