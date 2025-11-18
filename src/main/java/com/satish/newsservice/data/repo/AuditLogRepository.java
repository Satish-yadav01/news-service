package com.satish.newsservice.data.repo;

import com.satish.newsservice.data.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    Optional<AuditLog> findByTid(Long tid);
}
