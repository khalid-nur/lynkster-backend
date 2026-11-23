package com.lynkster.backend.auth.dto.event;

/**
 * Represents an event containing the user's email and verification token
 */
public record UserRegisteredEvent(String email, String verificationToken) {
}

