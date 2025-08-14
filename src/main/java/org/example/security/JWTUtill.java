package org.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.entity.constants.TokenType;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JWTUtill {

    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails, TokenType tokenType) {
        return createToken(new HashMap<>(), userDetails, tokenType);
    }

    private String createToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            TokenType tokenType
    ) {
        Calendar calendar = Calendar.getInstance();

        JwtBuilder jwtBuilder = Jwts
                .builder()
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()));

        if (tokenType == TokenType.BEARER) {
            calendar.add(Calendar.HOUR, 1);
            Date expDate = calendar.getTime();
            jwtBuilder = jwtBuilder
                    .subject(userDetails.getUsername())
                    .claim("role", Objects.requireNonNull(
                            userRepository.findByEmail(
                                    userDetails.getUsername()).orElse(null)).getRole().name())
                    .expiration(expDate);
        }

        if (tokenType == TokenType.VERIFICATION) {
            calendar.add(Calendar.YEAR, 1);
            Date expDate = calendar.getTime();
            jwtBuilder = jwtBuilder
                    .subject(userDetails.getUsername())
                    .expiration(expDate);
        }

        return jwtBuilder.signWith(getSecretKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userEmail = extractEmail(token);
        return (userEmail.equals(userDetails.getUsername()));
    }

}
