package com.satish.newsservice.config;

import com.satish.newsservice.util.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JwtAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        // TODO: validate token (signature, expiry, claims)
        if (!jwtService.validateToken(token)) {
            return Mono.error(new BadCredentialsException("Invalid JWT"));
        }

        // Extract email/username
        String email = jwtService.getEmailFromToken(token);

        // Set empty authorities, or assign roles if needed
        List<GrantedAuthority> authorities = List.of();

        return Mono.just(
                new UsernamePasswordAuthenticationToken(email, null, authorities)
        );
    }
}

