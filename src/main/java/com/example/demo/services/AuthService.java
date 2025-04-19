package com.example.demo.services;

 
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.JWTToken;
import com.example.demo.entity.User;
import com.example.demo.repository.JWTTokenRepository;
import com.example.demo.repository.UserRepo;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Service
public class AuthService {

	private final Key SIGNING_KEY;
	private final UserRepo userRepo;
    private final JWTTokenRepository jwtTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthService(UserRepo userRepo, JWTTokenRepository jwtTokenRepository, @Value("${jwt.secret}") String jwtSecret) {
        this.userRepo = userRepo;
        this.jwtTokenRepository = jwtTokenRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
    
    
    public User authenticate(String username, String password) {
    	
    	Optional<User> existingUser = userRepo.findByUsername(username);
    	if(existingUser.isPresent()) {
    		User user = existingUser.get();
    		if(!passwordEncoder.matches(password, user.getPassword())) {
    			throw new RuntimeException("Invalid Password");
    		}
    		return user;
    	}
    	else {
    		throw new RuntimeException("Invalid Username");
    	}
    }
    
    public String generateToken(User user) {
    	
    	String token;
    	
    	LocalDateTime currentTime = LocalDateTime.now();
    	JWTToken existingToken = jwtTokenRepository.findByuserid(user.getUser_id());
    	if(existingToken!=null && currentTime.isBefore(existingToken.getExpiresAt())) {
    		token = existingToken.getToken();
    	}
    	else {
    		token = generateNewToken(user);
    		if(existingToken!=null) {
    			jwtTokenRepository.delete(existingToken);
    		}
    		saveToken(user, token);
    	}
		return token;
    	
    }
    
    public String generateNewToken(User user) {
    	JwtBuilder builder = Jwts.builder();
    	builder.setSubject(user.getUsername());
    	builder.claim("role", user.getRole().name());
    	builder.setIssuedAt(new Date());
    	builder.setExpiration(new Date(System.currentTimeMillis()+3600000));
    	builder.signWith(SIGNING_KEY);
    	String token = builder.compact();
    	return token;
    }
    
    public void saveToken(User user, String token) {
    	JWTToken jwtToken = new JWTToken(user, token, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        jwtTokenRepository.save(jwtToken);
    	
    }
	public boolean validateToken(String token) {
		System.out.println("Validating Token");
		try {
			  System.err.println("VALIDATING TOKEN...");
			// parse and validate token
			
			Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token);
			
			// check if token is present in db and not expired
			Optional<JWTToken> jwtToken = jwtTokenRepository.findByToken(token);
			
			if(jwtToken.isPresent()) {
				System.err.println("Token Expiry: " + jwtToken.get().getExpiresAt());
                System.err.println("Current Time: " + LocalDateTime.now());
                return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
			}
			return false;
			
		}
		catch(Exception e) {
			
			System.err.println("Token validation failed: " + e.getMessage());
            return false;
		}
		 
	}
	
	public String extractUsername(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
	public void logout(User user) {
		jwtTokenRepository.deleteByUserId(user.getUser_id());
		
	}
	
}
