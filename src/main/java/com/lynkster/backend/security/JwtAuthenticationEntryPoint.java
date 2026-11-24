package com.lynkster.backend.security;


import com.lynkster.backend.common.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Handles unauthorized requests and returns a 401 response
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // Check whether the authentication failure was caused by a JWT
        Object jwtException = request.getAttribute("jwtException");

        String message = "Unauthorized";

        // Determine the error message based on the JWT exception
        if (jwtException instanceof ExpiredJwtException) {
            message = "Access token has expired";

            log.warn("Expired JWT for request: {}", request.getRequestURI());

        } else if (jwtException instanceof JwtException
                || jwtException instanceof IllegalArgumentException) {

            message = "Invalid access token";

            log.warn("Invalid JWT for request: {}", request.getRequestURI());
        }

        // Build the unauthorized error response
        ErrorResponse error = ErrorResponse.response(
                message,
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI(),
                null
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write the error response as JSON
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}

