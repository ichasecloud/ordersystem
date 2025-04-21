package com.example.menuservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final String secret = "MySuperSecretKeyForJWTThatIsAtLeast256BitsLong123!";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUsername(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public String getUserRole(String token) {
        return parseToken(token).getBody().get("role", String.class);
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}