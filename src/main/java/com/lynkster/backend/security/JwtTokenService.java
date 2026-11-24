package com.lynkster.backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenService {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.accessExpirationInMils}")
    private long accessExpirationMs;

    /**
     * Generates a JWT access token for the authenticated user
     */
    public String generateToken(AuthUserDetails userDetails) {

        // Use the user's UUID as the JWT subject
        String userId = userDetails.getId().toString();

        // Add the user's authorities to the token
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("roles", roles);

        return buildToken(userId, accessExpirationMs, claims);
    }

    /**
     * Validates the JWT token and extracts the user's UUID
     */
    public UUID getUserIdFromToken(String token) {

        String subject = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return UUID.fromString(subject);
    }

    /**
     * Validates a JWT token
     */
    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Extracts the JWT token from the Authorization header
     */
    public String extractTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * Builds a signed JWT with the provided subject and claims
     */
    private String buildToken(
            String subject,
            long expirationMs,
            Map<String, Object> claims
    ) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Creates the JWT signing key from the configured secret
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}

