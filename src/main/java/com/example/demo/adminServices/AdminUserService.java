package com.example.demo.adminServices;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.entity.User.Role;
import com.example.demo.repository.JWTTokenRepository;
import com.example.demo.repository.UserRepo;

@Service
public class AdminUserService {
	
	UserRepo userRepo;
	JWTTokenRepository jwtTokenRepository;

	
	
	
	public AdminUserService(UserRepo userRepo,JWTTokenRepository jwtTokenRepository) {
		super();
		this.userRepo=userRepo;
		this.jwtTokenRepository=jwtTokenRepository;
		
	}



	public User getUserById(Integer user_id) {
		
		 return userRepo.findById(user_id).orElseThrow(() -> new IllegalArgumentException("User not found"));
		
	}
	
	public User modifyUser(Integer user_id, String name, String email, String role) {
		
		Optional<User> userOptional=userRepo.findById(user_id);
		if(userOptional.isEmpty()) {
			throw new IllegalArgumentException("User with Id not found");
		}
		User existingUser = userOptional.get();
		
		if(name != null && !name.isEmpty()) {
			existingUser.setUsername(name);
		}
		if(email != null && !email.isEmpty()) {
			existingUser.setEmail(email);
		}
		 if (role != null && !role.isEmpty()) {
	            try {
	                existingUser.setRole(Role.valueOf(role));
	            } catch (IllegalArgumentException e) {
	                throw new IllegalArgumentException("Invalid role: " + role);
	            }
	        }
		 jwtTokenRepository.deleteByUserId(existingUser.getUser_id());
		  // Save updated user
	        return userRepo.save(existingUser);
		
	}

}
