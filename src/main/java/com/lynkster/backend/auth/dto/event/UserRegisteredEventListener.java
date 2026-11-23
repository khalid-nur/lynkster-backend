package com.lynkster.backend.auth.dto.event;

import com.lynkster.backend.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventListener {

    private final EmailService emailService;

    /**
     * Sends a verification email after the user registration is committed to the database
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegistered(UserRegisteredEvent event) {
        try {
            emailService.sendVerificationEmail(event.email(), event.verificationToken());

        } catch (Exception ex) {
            log.error("Failed to send registration verification email", ex);
        }
    }
}

