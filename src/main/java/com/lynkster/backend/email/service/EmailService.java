package com.lynkster.backend.email.service;

public interface EmailService {

    /**
     * Sends an email containing a verification link to the user
     */
    void sendVerificationEmail(String recipientEmail, String verificationToken);
}

