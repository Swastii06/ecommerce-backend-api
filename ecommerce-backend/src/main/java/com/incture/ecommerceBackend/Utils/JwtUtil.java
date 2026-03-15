package com.incture.ecommerceBackend.Utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil { // This class is responsible for handling JWT token operations like generating,
						// extracting, and validating tokens.
	private static final String SECRET_KEY = "secure-key-super-secret-12345678-ecommerce"; // Secret key is used to sign
																							// & verify JWT tokens
	private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	// Converts the secret key into a secure cryptographic key which is used for
	// signing the JWT token.

	public String generateToken(String username, long expiryMinutes) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		// Parses the JWT token, verifies signature using the secret key, extracts the
		// payload data.
		return claims.getSubject(); // Returns the username stored inside the token
	}

	public boolean validateToken(String token) { // This method checks whether the token is valid
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}