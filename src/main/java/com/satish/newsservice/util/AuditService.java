package com.satish.newsservice.util;

import com.satish.newsservice.constant.Constants;
import com.satish.newsservice.data.entity.AuditLog;
import com.satish.newsservice.data.repo.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final Environment env;

    public Mono<Long> generateTid(ServerWebExchange exchange) {

        String requestUrl = exchange.getRequest().getURI().toString();
        String endpoint = exchange.getRequest().getPath().value();
        String ipAddress = exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "UNKNOWN";

        AuditLog log = AuditLog.builder()
                .requestTime(LocalDateTime.now())
                .request(requestUrl)
                .endpoint(endpoint)
                .ipAddress(ipAddress)
                .serviceName(env.getProperty("spring.application.name"))
                .statusCode(Constants.PENDING_STATUS_CODE)
                .statusMsg(Constants.PENDING_STATUS_MSG)
                .build();

        // Wrap blocking DB call safely
        return Mono.fromCallable(() -> auditLogRepository.save(log))
                .subscribeOn(Schedulers.boundedElastic())
                .map(AuditLog::getTid);
    }
}

