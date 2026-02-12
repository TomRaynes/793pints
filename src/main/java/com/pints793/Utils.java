package com.pints793;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

public final class Utils {

    private static final String JWT_SECRET = "JWT_SECRET";
    private static final long TOKEN_EXPIRATION_TIME_MS = 1000 * 60 * 60 * 24; // 24 hours


    public static String newId(IdType type){
        UUID uuid = new UUID(System.currentTimeMillis(), UUID.randomUUID().getLeastSignificantBits());
        return type.toString() + "-" + uuid;
    }

    public static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static boolean passwordMatches(String password, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(password, encodedPassword);
    }

    private static Key getSecretKey() {
        return Keys.hmacShaKeyFor(System.getenv(JWT_SECRET).getBytes());
    }

    public static String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME_MS))
                .signWith(getSecretKey())
                .compact();
    }

    public static String authenticateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
