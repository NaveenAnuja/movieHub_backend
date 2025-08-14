package org.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.example.repository.TokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtill jwtUtill;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (isInvalidHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(authHeader);
        String email = jwtUtill.extractEmail(token);

        if (token.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticateUser(token, email, request);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isInvalidHeader(String header) {
        return header == null || header.isBlank() || !header.startsWith("Bearer ");
    }

    private String extractToken(String header) {
        return header.substring(7);
    }

    private void authenticateUser(String token, String email, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        boolean isValidToken = tokenRepository.findByToken(token)
                .map(tokenEntity -> !tokenEntity.getExpired())
                .orElse(false);

        if (jwtUtill.isTokenValid(token, userDetails) && isValidToken) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
