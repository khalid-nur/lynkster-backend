package com.lynkster.backend.auth.service;


import com.lynkster.backend.auth.dto.request.LoginRequest;
import com.lynkster.backend.auth.dto.request.RegisterRequest;
import com.lynkster.backend.auth.dto.response.LoginResponse;
import com.lynkster.backend.auth.dto.response.UserDto;

public interface AuthService {

    /**
     * Registers a new user and sends an email verification request
     */
    UserDto register(RegisterRequest request);

    /**
     * Authenticates a user and returns a JWT access token
     */
    LoginResponse login(LoginRequest request);

}

