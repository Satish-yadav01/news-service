package com.satish.newsservice.config;

import com.satish.newsservice.exception.JwtAuthExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtServerAuthenticationConverter jwtConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthExceptionHandler authExceptionHandler) {

        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        jwtAuthFilter.setServerAuthenticationConverter(jwtConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authExceptionHandler)
                )
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**", "/login/**", "/public/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}




