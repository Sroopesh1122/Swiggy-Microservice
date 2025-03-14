package com.shivu.swiggy_app.util;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("cgsdhjcvdfbvmbfgyuguyuydvbkfdbkjvbdkjfbvkjfdbvkjfd".getBytes());

    public String generateToken(String email ,Map<String, Object> tokenClaims) {
        Map<String, Object> claims = tokenClaims;
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .signWith(SECRET_KEY,SignatureAlgorithm.HS256 )
                .compact();
    }

    public String extractSubject(String token) {
    	String details=null;
    	try {
        	details = Jwts.parserBuilder()
            		.setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
		} catch (Exception e) {
			throw new UsernameNotFoundException("Invalid Token");
		}
        
        return details;
    }
    
    public Map<String, Object> extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token) 
                .getBody(); 
    }
     
   
}

