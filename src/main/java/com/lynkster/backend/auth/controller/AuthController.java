package com.lynkster.backend.auth.controller;

import com.lynkster.backend.auth.dto.request.RegisterRequest;
import com.lynkster.backend.auth.dto.request.ResendVerificationRequest;
import com.lynkster.backend.auth.dto.response.UserDto;
import com.lynkster.backend.auth.service.AuthService;
import com.lynkster.backend.auth.service.EmailVerificationService;
import com.lynkster.backend.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    /**
     * Registers a new user and initiates email verification.
     * The account remains unverified until the user confirms their email
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {

        UserDto registeredUser = authService.register(request);

        ApiResponse<UserDto> response = ApiResponse.response(
                true,
                "User registered successfully",
                registeredUser,
                HttpStatus.CREATED.value()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Verifies a user's email address using a single-use token
     */
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);

        ApiResponse<Void> response = ApiResponse.response(
                true,
                "Email verified successfully",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
     * Requests a new verification email for an unverified account
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(@Valid @RequestBody ResendVerificationRequest request) {

        emailVerificationService.resendVerificationEmail(request.email());

        ApiResponse<Void> response = ApiResponse.response(
                true,
                "If the email address is registered and not verified, a verification email will be sent.",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}


