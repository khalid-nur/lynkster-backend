package com.lynkster.backend.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        UserDto user
) {
}

