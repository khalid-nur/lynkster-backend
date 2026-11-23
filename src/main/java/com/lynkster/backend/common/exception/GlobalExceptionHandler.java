package com.lynkster.backend.common.exception;

import com.lynkster.backend.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
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
}


