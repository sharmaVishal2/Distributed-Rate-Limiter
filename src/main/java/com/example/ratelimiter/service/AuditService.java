package com.example.ratelimiter.service;

import com.example.ratelimiter.entity.RequestAuditLog;
import com.example.ratelimiter.repository.RequestAuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final RequestAuditLogRepository auditRepository;

    public AuditService(RequestAuditLogRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void record(String clientId, String endpoint, boolean allowed, String reason) {
        RequestAuditLog logEntry = new RequestAuditLog(
                null,
                clientId,
                endpoint,
                allowed,
                reason,
                java.time.Instant.now()
        );
        auditRepository.save(logEntry);
        if (!allowed) {
            log.warn("Blocked request for client={} endpoint={} reason={}", clientId, endpoint, reason);
        }
    }
}
