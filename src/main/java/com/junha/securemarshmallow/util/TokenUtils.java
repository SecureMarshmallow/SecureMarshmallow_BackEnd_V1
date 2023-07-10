package com.junha.securemarshmallow.util;

import com.junha.securemarshmallow.ServerMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@Component
public class TokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessTokenExpirationMs}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refreshTokenExpirationMs}")
    private long refreshTokenExpirationMs;


    public String generateAccessToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = createClaims(userDetails.getUsername(), "access");
            return generateToken(claims, accessTokenExpirationMs);
        } catch (Exception ex) {
            throw new TokenGenerationException("Token Generate Failed");
        }
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ServerMessage> handleTokenGenerationException(TokenGenerationException ex) {
        ServerMessage serverMessage = new ServerMessage("Token Generate Failed", ex.getMessage());
        return new ResponseEntity<>(serverMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public class TokenGenerationException extends RuntimeException {
        public TokenGenerationException(String message) {
            super(message);
        }
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = createClaims(userDetails.getUsername(), "refresh");
        return generateToken(claims, refreshTokenExpirationMs);
    }

    private Map<String, Object> createClaims(String username, String tokenType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("iat", new Date());
        claims.put("exp", new Date(System.currentTimeMillis() + 3600000));
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("token_type", tokenType);
        return claims;
    }

    private String generateToken(Map<String, Object> claims, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        Claims claims = extractClaims(refreshToken);
        String username = claims.getSubject();
        UserDetails userDetails = new User(username, "", Collections.emptyList());
        return generateAccessToken(userDetails);
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(refreshToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }


}
