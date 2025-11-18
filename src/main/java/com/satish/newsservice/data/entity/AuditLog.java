package com.satish.newsservice.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_LOG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tid;  // primary key

    private Integer statusCode;

    @Column(length = 500)
    private String statusMsg;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String request;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String response;

    private LocalDateTime requestTime;


    private LocalDateTime responseTime;

    // This can be calculated: responseTime - requestTime
    private Long processingTime; // in milliseconds

    private String ipAddress;

    private String userId;

    private String endpoint;

    private String serviceName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorTrace;

    //Auto-calculate processingTime before saving or updating
    @PrePersist
    @PreUpdate
    private void calculateProcessingTime() {
        if (requestTime != null && responseTime != null) {
            this.processingTime = Duration.between(requestTime, responseTime).toMillis();
        }
    }
}

