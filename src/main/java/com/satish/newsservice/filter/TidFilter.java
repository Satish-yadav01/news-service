package com.satish.newsservice.filter;

import com.satish.newsservice.service.AuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TidFilter extends OncePerRequestFilter {

    private final AuditService auditService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // generate tid BEFORE controller
        Long tid = auditService.generateTid(request);

        // store TID for later (controller/services can access it)
        request.setAttribute("tid", tid);

        filterChain.doFilter(request, response);
    }
}

