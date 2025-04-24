package com.example.ezra.helpers;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final TokenBlacklist tokenBlacklist;
    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET_KEY = "QqUKYmLzMWsuJ+pmNOm2wwQkC1UHCSAZ96g7HI3M8do=";

    public JwtUtil(TokenBlacklist tokenBlacklist) {
        this.tokenBlacklist = tokenBlacklist;
    }

    private Key getSigningKey() {
        if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
            throw new IllegalStateException("❌ JWT Secret Key is missing or empty!");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY.trim());
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to decode JWT Secret Key: " + e.getMessage());
        }
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                throw new IllegalArgumentException("❌ Token format is incorrect!");
            }
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to extract username from token: " + e.getMessage());
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (JwtException e) {
            throw new RuntimeException("❌ Failed to parse token: " + e.getMessage());
        }
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Changed from getSignInKey() to getSigningKey()
                    .build()
                    .parseClaimsJws(token);
            return !tokenBlacklist.isBlacklisted(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractEmail(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract email from token: " + e.getMessage());
        }
    }
}