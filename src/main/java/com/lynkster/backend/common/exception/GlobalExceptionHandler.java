package com.lynkster.backend.common.exception;

import com.lynkster.backend.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles requests when a user account already exists
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErrorResponse response = ErrorResponse.response(
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles validation errors in incoming requests
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        ErrorResponse response = ErrorResponse.response(
                "One or more fields have invalid values",
                status.value(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles errors during email verification
     */
    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<ErrorResponse> handleEmailVerificationException(
            EmailVerificationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse response = ErrorResponse.response(
                ex.getMessage(),
                status.value(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles failed authentication attempts
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErrorResponse response = ErrorResponse.response(
                "Invalid credentials or account is not verified",
                status.value(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles internal authentication service errors
     */
    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationServiceException(
            AuthenticationServiceException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Internal authentication service error", ex);

        ErrorResponse response = ErrorResponse.response(
                "An error occurred during authentication",
                status.value(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(response);
    }
}


