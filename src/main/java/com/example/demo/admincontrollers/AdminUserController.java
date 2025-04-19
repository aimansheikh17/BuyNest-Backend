package com.example.demo.admincontrollers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminServices.AdminUserService;
import com.example.demo.entity.User;

@RestController
@RequestMapping("/admin/user")
@CrossOrigin(origins="http://localhost:5174",allowCredentials="true")

public class AdminUserController {

	AdminUserService adminUserService;
	
	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
	

    @GetMapping("/getbyid")
	public ResponseEntity<?> getUserById(@RequestBody Map<String, Integer> userRequest) {
		try {
		int user_id=userRequest.get("user_id");
		User user = adminUserService.getUserById(user_id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
		}
		catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
	
	 @PutMapping("/modify")
	public ResponseEntity<?> modifyUser(@RequestBody Map<String, Object> userRequest) {
		
		try {
			Integer user_id=(Integer) userRequest.get("user_id");
			String name=(String) userRequest.get("username");
			String email=(String) userRequest.get("email");
			String role=(String) userRequest.get("role");
			
			User updatedUser = adminUserService.modifyUser(user_id, name, email, role);
			Map<String, Object> response = new HashMap<>();
            response.put("user_id", updatedUser.getUser_id());
            response.put("username", updatedUser.getUsername());
            response.put("email", updatedUser.getEmail());
            response.put("role", updatedUser.getRole().name());
            response.put("createdAt", updatedUser.getCreatedAt());
            response.put("updatedAt", updatedUser.getUpdatedAt());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
		}
}
