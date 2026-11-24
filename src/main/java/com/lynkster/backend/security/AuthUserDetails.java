package com.lynkster.backend.security;

import com.lynkster.backend.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class AuthUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final boolean emailVerified;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUserDetails(
            UUID id,
            String email,
            String password,
            boolean emailVerified,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.emailVerified = emailVerified;
        this.authorities = authorities;
    }

    /**
     * Builds user details from the User entity
     */
    public static AuthUserDetails buildUserDetails(User user) {

        // Convert the user's role into a Spring Security authority
        GrantedAuthority authority =
                new SimpleGrantedAuthority(user.getRole().getName().name());

        return new AuthUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.isEmailVerified(),
                List.of(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Allows authentication only after email verification
     */
    @Override
    public boolean isEnabled() {
        return emailVerified;
    }
}


