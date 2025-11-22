package com.satish.newsservice.util;

import com.satish.newsservice.constant.Constants;
import com.satish.newsservice.data.entity.AuditLog;
import com.satish.newsservice.data.repo.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final Environment env;

    public Long generateTid(HttpServletRequest request) {
        AuditLog log = new AuditLog();

        log.setRequestTime(LocalDateTime.now());
        log.setRequest(String.valueOf(request.getRequestURL()));
        log.setEndpoint(request.getRequestURI());
        log.setIpAddress(request.getRemoteAddr());
        log.setServiceName(env.getProperty("spring.application.name"));
        log.setStatusCode(Constants.PENDING_STATUS_CODE);
        log.setStatusMsg(Constants.PENDING_STATUS_MSG);

        // Let MySQL generate tid + default values
        AuditLog saved = auditLogRepository.save(log);

        return saved.getTid(); // return auto-generated tid
    }
}

