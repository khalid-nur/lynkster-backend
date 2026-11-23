package com.lynkster.backend.auth.mapper;

import com.lynkster.backend.auth.dto.request.RegisterRequest;
import com.lynkster.backend.auth.dto.response.UserDto;
import com.lynkster.backend.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    /**
     * Converts registration details into a new user entity
     */
    public User mapRegisterRequestToUser(RegisterRequest request) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .build();
    }

    /**
     * Converts a user entity into a user response DTO
     */
    public UserDto mapUserToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getRole().getName().name()
        );
    }
}
