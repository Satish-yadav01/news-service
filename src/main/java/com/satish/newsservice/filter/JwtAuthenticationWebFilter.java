package com.satish.newsservice.filter;
import com.satish.newsservice.data.entity.User;
import com.satish.newsservice.data.repo.UserRepository;
import com.satish.newsservice.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


//@Component
@RequiredArgsConstructor
@Order(1)
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {

                String email = jwtService.getEmailFromToken(token);

                return Mono.fromCallable(() -> userRepository.findByEmail(email))
                        .subscribeOn(Schedulers.boundedElastic()) // run blocking code safely
                        .flatMap(optionalUser -> {
                            if (optionalUser.isEmpty()) return Mono.empty();

                            User user = optionalUser.get();

                            List<GrantedAuthority> authorities = Arrays.stream(
                                            (user.getRoles() == null ? "ROLE_USER" : user.getRoles())
                                                    .split(","))
                                    .map(String::trim)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                            Authentication auth =
                                    new UsernamePasswordAuthenticationToken(email, null, authorities);

                            exchange.getAttributes().put(
                                    "org.springframework.security.core.context.SecurityContext",
                                    new SecurityContextImpl(auth)
                            );

                            return Mono.empty();
                        })
                        .then(chain.filter(exchange));
            }
        }

        return chain.filter(exchange);
    }
}


