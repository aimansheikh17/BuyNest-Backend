package com.example.demo.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User;
import com.example.demo.entity.User.Role;
import com.example.demo.repository.UserRepo;
import com.example.demo.services.AuthService;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/api/*","/admin/*"})
@Component
public class AuthenticationFilter implements Filter {
	
	AuthService authService;
	UserRepo userRepo;
	
	 private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	public static final String[] UNAUTHENTICATED_PATHS= { "/api/users/register","/api/auth/login"};
	private static final String ALLOWED_ORIGIN = "http://localhost:5174";

	
	public AuthenticationFilter(AuthService authService, UserRepo userRepo) {
		System.out.println("filter started");
		this.authService = authService;
		this.userRepo = userRepo;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try {
			executeFilterLogin(request,response,chain);
		}
		catch(Exception e) {
			logger.error("Unexpected error in AuthenticationFilter", e);
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			sendErrorResponse(httpResponse,HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "internal server error");
		}
		
		
	}
 

	public void executeFilterLogin(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// validations here
		HttpServletRequest httpRequest= (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		// check URIS if it is one of the UNAUTHENTICATED_PATH chain.doFilter()
		
		String requestURI = httpRequest.getRequestURI();
		logger.info("Request URI: {}", requestURI);
		
		
		if(Arrays.asList(UNAUTHENTICATED_PATHS).contains(requestURI)) {
			
			chain.doFilter(request, response);
			return;
		}
		// Handle preflight (OPTIONS) requests
        if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            setCORSHeaders(httpResponse);
            return;
        }
		
		// we have to validate token
		// fetch the token from cookie present in cookie
		String token =getAuthTokenFromCookies(httpRequest);
		System.out.println(token);
		
		if(token == null || !authService.validateToken(token)) {
			sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid or missing token");
			return;
		}
		
		 // Extract username and verify user
        String username = authService.extractUsername(token);
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: User not found");
            return;
        }
        
        
        User authenticatedUser = userOptional.get();
        Role role = authenticatedUser.getRole();
        logger.info("Authenticated User: {}, Role: {}", authenticatedUser.getUsername(), role);
		
        
        if (requestURI.startsWith("/admin/") && role != Role.ADMIN) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Admin access required");
            return;
        }

        if (requestURI.startsWith("/api/") && role != Role.CUSTOMER) {
            sendErrorResponse(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Customer access required");
            return;
        }
        

        // Attach user details to request
        httpRequest.setAttribute("authenticatedUser", authenticatedUser);
        chain.doFilter(request, response);
	
	}
	
	
	public void sendErrorResponse(HttpServletResponse response, int statusCode , String message)  throws IOException{
		response.setStatus(statusCode);
        response.getWriter().write(message);
		
	}
		
	public void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }
	
	public String getAuthTokenFromCookies(HttpServletRequest request) {
        Cookie cookies[] = request.getCookies();
        System.out.println(cookies.length);
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "authToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }	
}
