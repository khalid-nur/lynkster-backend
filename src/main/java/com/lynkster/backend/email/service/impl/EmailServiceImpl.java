package com.lynkster.backend.email.service.impl;


import com.lynkster.backend.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String senderEmail;

    @Value("${app.email.verification.token-expiration-minutes}")
    private long tokenExpirationMinutes;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String recipientEmail, String verificationToken) {

        // Create a verification link using the frontend URL and token
        String verificationLink = frontendUrl + "/verify-email?token=" +
                URLEncoder.encode(verificationToken, StandardCharsets.UTF_8);

        // Create the verification email
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Verify your Lynkster email address");

        // Include the verification link and its expiration time
        message.setText(
                """
                Welcome to Lynkster!

                Thank you for creating an account.

                Please verify your email address by visiting the link below:

                %s

                This verification link expires in %d minutes.

                If you didn't create a Lynkster account,
                you can safely ignore this email.

                The Lynkster Team
                """.formatted(verificationLink,tokenExpirationMinutes)
        );

        mailSender.send(message);
    }
}

