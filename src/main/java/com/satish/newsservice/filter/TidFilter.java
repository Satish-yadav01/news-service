package com.satish.newsservice.filter;

import com.satish.newsservice.util.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(2)
public class TidFilter implements WebFilter {

    private final AuditService auditService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // your audit logic here

        return auditService.generateTid(exchange)
                .flatMap(tid -> {
                    // store TID in request attributes
                    exchange.getAttributes().put("TID", tid);
                    return chain.filter(exchange);
                });
    }
}

