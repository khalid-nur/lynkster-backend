package com.lynkster.backend.config;

import com.lynkster.backend.security.AuthUserDetailsService;
import com.lynkster.backend.security.JwtAuthenticationEntryPoint;
import com.lynkster.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthUserDetailsService authUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configures Spring Security for JWT authentication
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * Configures authentication using user details and BCrypt
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(authUserDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Provides the authentication manager for login
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Encodes passwords using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

