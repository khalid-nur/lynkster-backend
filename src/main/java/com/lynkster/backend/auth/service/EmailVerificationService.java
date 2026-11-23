package com.lynkster.backend.auth.service;

import com.lynkster.backend.user.entity.User;

public interface EmailVerificationService {

    /**
     * Creates a verification token for the user
     */
    String createVerificationToken(User user);

    /**
     * Verifies a user's email using the provided token
     */
    void verifyEmail(String token);

    /**
     * Resends a verification email if the user is eligible
     */
    void resendVerificationEmail(String email);


}

