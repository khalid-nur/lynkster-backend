package com.lynkster.backend.auth.service.impl;

import com.lynkster.backend.auth.dto.event.UserRegisteredEvent;
import com.lynkster.backend.auth.entity.EmailVerificationToken;
import com.lynkster.backend.auth.repository.EmailVerificationTokenRepository;
import com.lynkster.backend.auth.service.EmailVerificationService;
import com.lynkster.backend.common.exception.EmailVerificationException;
import com.lynkster.backend.common.util.VerificationTokenGenerator;
import com.lynkster.backend.user.entity.User;
import com.lynkster.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final VerificationTokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.email.verification.token-expiration-minutes}")
    private long tokenExpirationMinutes;

    @Value("${app.email.verification.resend-cooldown-seconds}")
    private long resendCooldownSeconds;

    @Override
    @Transactional
    public String createVerificationToken(User user) {

        // Generate a new verification token
        String rawToken = tokenGenerator.generateToken();

        // Hash the token before storing it in the database
        String hashedToken = tokenGenerator.hashToken(rawToken);

        // Create a verification token with an expiration time
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                        .token(hashedToken)
                        .user(user)
                        .expiresAt(Instant.now().plus(Duration.ofMinutes(tokenExpirationMinutes)))
                        .build();

        tokenRepository.save(verificationToken);

        return rawToken;
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        // Hash the provided token to find its database record
        String hashedToken = tokenGenerator.hashToken(token);

        EmailVerificationToken verificationToken = tokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new EmailVerificationException("Invalid email verification token"));

        // Prevent using the same verification token more than once
        if (verificationToken.getVerifiedAt() != null) {
            throw new EmailVerificationException(
                    "Email verification token has already been used"
            );
        }

        // Reject expired verification tokens
        if (!Instant.now().isBefore(verificationToken.getExpiresAt())) {
            throw new EmailVerificationException(
                    "Email verification token has expired"
            );
        }

        User user = verificationToken.getUser();

        // Mark the user's email as verified
        user.setEmailVerified(true);
        verificationToken.setVerifiedAt(Instant.now());
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        // Find the account associated with the email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Ignore requests for accounts that don't exist
        if (optionalUser.isEmpty()) {
            return;
        }

        User user = optionalUser.get();

        // Ignore requests for accounts that are already verified
        if (user.isEmailVerified()) {
            return;
        }

        // Get the user's most recent verification token
        Optional<EmailVerificationToken> latestToken = tokenRepository.findTopByUserOrderByCreatedAtDesc(user);

        if (latestToken.isPresent()) {

            // Check whether the resend cooldown period has passed
            Instant cooldownEndsAt = latestToken.get().getCreatedAt().plus(Duration.ofSeconds(resendCooldownSeconds));

            // Ignore the request if the cooldown period hasn't passed
            if (Instant.now().isBefore(cooldownEndsAt)) {
                return;
            }
        }

        // Remove previous tokens so they can no longer be used
        tokenRepository.deleteByUser(user);
        tokenRepository.flush();

        // Generate a new verification token
        String verificationToken = createVerificationToken(user);

        // Send the new verification email after the transaction commits
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(), verificationToken));
    }
}

