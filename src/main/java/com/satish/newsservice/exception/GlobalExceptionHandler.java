package com.satish.newsservice.exception;

import com.satish.newsservice.constant.Constants;
import com.satish.newsservice.data.dto.ResponseData;
import com.satish.newsservice.data.entity.AuditLog;
import com.satish.newsservice.data.repo.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.scanner.Constant;

import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AuditLogRepository auditLogRepo;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseData<Object>> handleApiException(ApiException ex) {
        ResponseData<Object> responseData = new ResponseData<>();
        responseData.setStatusMsg(ex.getMessage());
        responseData.setStatusCode(Constants.FAILED_STATUS_CODE);
        responseData.setTid(ex.getTid());

        auditLogRepo.findByTid(ex.getTid()).ifPresent(auditLog -> {
            auditLog.setStatusMsg(ex.getMessage());
            auditLog.setStatusCode(Constants.FAILED_STATUS_CODE);
            auditLogRepo.save(auditLog);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseData);
    }
}

