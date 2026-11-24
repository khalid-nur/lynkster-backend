package com.lynkster.backend.security;

import com.lynkster.backend.user.entity.User;
import com.lynkster.backend.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    /**
     * Authenticates requests using a valid JWT access token
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {

            // Extract the JWT token from the request
            String token = jwtTokenService.extractTokenFromRequest(request);

            if (token != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Extract the user's UUID from the token
                UUID userId = jwtTokenService.getUserIdFromToken(token);

                // Find the user in the database
                Optional<User> optionalUser = userRepository.findById(userId);

                if (optionalUser.isPresent()) {

                    User user = optionalUser.get();

                    // Only authenticate users with verified emails
                    if (user.isEmailVerified()) {

                        AuthUserDetails userDetails =
                                AuthUserDetails.buildUserDetails(user);

                        // Create the Spring Security authentication
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        // Store the authenticated user in the security context
                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

        } catch (JwtException | IllegalArgumentException ex) {

            // Clear authentication when the JWT is invalid
            SecurityContextHolder.clearContext();

            // Store the exception for the authentication entry point
            request.setAttribute("jwtException", ex);

            log.debug("JWT authentication failed: {}",
                    ex.getClass().getSimpleName());
        }

        // Continue processing the request
        filterChain.doFilter(request, response);
    }
}

