package com.example.shaden.features.authentication;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.shaden.features.user.User;
import com.example.shaden.features.user.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("userId", Long.class));
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Error extracting email from token");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserPrincipal userPrincipal, Optional<User> user) {
        return generateTokenWithCustomClaims(new HashMap<>(), userPrincipal, user);
    }

    public String generateTokenWithCustomClaims( Map<String, Object> extraClaims, UserPrincipal userPrincipal, Optional<User> user) {
        return buildToken(extraClaims, userPrincipal, jwtExpiration, user);
    }

    public String generateRefreshToken(UserPrincipal userPrincipal, Optional<User> user) {
        return buildToken(new HashMap<>(), userPrincipal, refreshExpiration, user);
    }

    private String buildToken(Map<String, Object> extraClaims, UserPrincipal userPrincipal, long expiration, Optional<User> user) {
        return Jwts
        .builder()
        .setClaims(extraClaims)
        .claim("userId", user.map(User::getId).orElse(null))
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    public boolean isTokenValid(String token, UserPrincipal userPrincipal) {
        final Long userId = extractUserId(token);
        return (userId.equals(userPrincipal.getUserId())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.
        parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
}

