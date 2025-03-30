package com.foc.rest;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class FocSimpleTokenAuth {

    //private static final String SECRET_KEY_BASE64 = "your-very-long-and-secret-key-here"; // Replace with a strong, random key
    private static final String SECRET_KEY_BASE64 =   "ASFklol349g23fjb9312ws33a12mcvd91ssnfd7277dbvdjh71cgcvhca1mdsskduf7348237gjdasd7";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY_BASE64));

    public static String generateToken(String username) {

        // Sample claims to include in the JWT payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123);
        claims.put("username", "sampleUser");
        claims.put("roles", new String[]{"user", "admin"});

        // Set the expiration time (e.g., 1 hour from now)
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + 3600000); // 1 hour in milliseconds

        // Generate the JWT
        String jwtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuer("example.com")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("Generated JWT: " + jwtToken);
        return jwtToken;
    }

    public static boolean verifyToken(String jwtToken) {
        // Verify the JWT
        try {
            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken);

            // JWT is valid, extract the claims
            Claims body = jwsClaims.getBody();
            return true;
        } catch (JwtException e) {
            // JWT is invalid
            System.err.println("JWT is invalid: " + e.getMessage());
            return false;
        }
    }

    protected String getKEY() {
        return "!@#dfdfSDFSdFSDFsdvkikhdcvq";
    }
}
