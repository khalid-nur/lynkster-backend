package com.lynkster.backend.auth.service.impl;

import com.lynkster.backend.auth.dto.event.UserRegisteredEvent;
import com.lynkster.backend.auth.dto.request.LoginRequest;
import com.lynkster.backend.auth.dto.request.RegisterRequest;
import com.lynkster.backend.auth.dto.response.LoginResponse;
import com.lynkster.backend.auth.dto.response.UserDto;
import com.lynkster.backend.auth.mapper.AuthMapper;
import com.lynkster.backend.auth.service.AuthService;
import com.lynkster.backend.auth.service.EmailVerificationService;
import com.lynkster.backend.common.exception.UserAlreadyExistsException;
import com.lynkster.backend.security.AuthUserDetails;
import com.lynkster.backend.security.JwtTokenService;
import com.lynkster.backend.user.entity.Role;
import com.lynkster.backend.user.entity.User;
import com.lynkster.backend.user.entity.UserRole;
import com.lynkster.backend.user.repository.RoleRepository;
import com.lynkster.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;


    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        User user = authMapper.mapRegisterRequestToUser(request);

        user.setPassword(passwordEncoder.encode(request.password()));

        // Find the default user role or create it if it doesn't exist
        Role role = roleRepository.findByName(UserRole.ROLE_USER).orElseGet(
                () -> roleRepository.save(Role.builder().name(UserRole.ROLE_USER).build()));

        user.setRole(role);

        User savedUser = userRepository.save(user);

        // Generate an email verification token for the user
        String verificationToken = emailVerificationService.createVerificationToken(savedUser);

        // Publish an event to send the verification email after the transaction commits
        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser.getEmail(), verificationToken));

        return authMapper.mapUserToUserDto(savedUser);
    }

    /**
     * Authenticates a user and generates a JWT access token
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        // Authenticate the user's email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Get the authenticated user's security details
        AuthUserDetails userDetails =
                (AuthUserDetails) authentication.getPrincipal();

        // Generate a JWT access token
        String accessToken = jwtTokenService.generateToken(userDetails);

        // Retrieve the authenticated user
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found")
                );

        // Map the user to the response DTO
        UserDto userDto = authMapper.mapUserToUserDto(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                userDto
        );
    }
}

