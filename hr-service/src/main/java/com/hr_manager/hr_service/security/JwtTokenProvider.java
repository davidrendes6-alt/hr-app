package com.hr_manager.hr_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UserPrincipal getUserPrincipalFromToken(String token) {
        Claims claims = validateToken(token);

        UserPrincipal principal = new UserPrincipal();
        principal.setUserId(UUID.fromString(claims.get("userId", String.class)));
        principal.setEmail(claims.get("email", String.class));
        principal.setRole(claims.get("role", String.class));
        principal.setName(claims.get("name", String.class));

        return principal;
    }
}

