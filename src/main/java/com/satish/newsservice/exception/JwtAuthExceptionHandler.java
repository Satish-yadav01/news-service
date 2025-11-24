package com.satish.newsservice.exception;

import com.satish.newsservice.util.ErrorResponseBuilder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.security.core.AuthenticationException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthExceptionHandler implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {

        Long tid = exchange.getAttributeOrDefault("TID", 0L);

        String body = ErrorResponseBuilder.build(
                tid,
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage() // "JWT signature does not match" OR "Expired token"
        );

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}

