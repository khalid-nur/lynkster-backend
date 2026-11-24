package com.lynkster.backend.security;

import com.lynkster.backend.user.entity.User;
import com.lynkster.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Finds a user by email and returns their security details
     */
    @Override
    public UserDetails loadUserByUsername(String email) {

        // Find the user using their email address
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        // Convert the user into Spring Security user details
        return AuthUserDetails.buildUserDetails(user);
    }
}
