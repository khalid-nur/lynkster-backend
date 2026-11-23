package com.lynkster.backend.auth.repository;

import com.lynkster.backend.auth.entity.EmailVerificationToken;
import com.lynkster.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    /**
     * Finds a verification token by its token string
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Deletes all verification tokens belonging to a user
     */
    void deleteByUser(User user);

    /**
     * Finds the user's most recently created verification token
     */
    Optional<EmailVerificationToken> findTopByUserOrderByCreatedAtDesc(User user);

}
