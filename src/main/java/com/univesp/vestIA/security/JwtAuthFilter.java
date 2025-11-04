package com.univesp.vestIA.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // 🔓 Endpoints públicos — precisam ser iguais aos configurados no SecurityConfig
    private static final String[] PUBLIC_ENDPOINTS = {
            "/usuarios/logar",
            "/usuarios/cadastrar",
            "/questao/all",
            "/questao/*",
            "/error",
            "/swagger-ui",
            "/v3/api-docs"
    };

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ Se o endpoint for público, pula a validação JWT
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractTokenFromRequest(request);

            if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            processJwtAuthentication(request, token);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UsernameNotFoundException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private boolean isPublicEndpoint(String path) {
        AntPathMatcher matcher = new AntPathMatcher();
        return Arrays.stream(PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> matcher.match(pattern, path));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void processJwtAuthentication(HttpServletRequest request, String token) {
        String username = jwtService.extractUsername(token);

        if (username != null && !username.trim().isEmpty()) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new RuntimeException("Token JWT inválido ou expirado");
            }
        } else {
            throw new RuntimeException("Usuário não pôde ser extraído do token JWT");
        }
    }
}
