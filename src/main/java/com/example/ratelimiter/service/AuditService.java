package com.example.ratelimiter.service;

import com.example.ratelimiter.entity.RequestAuditLog;
import com.example.ratelimiter.repository.RequestAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final RequestAuditLogRepository auditRepository;

    public void record(String clientId, String endpoint, boolean allowed, String reason) {
        RequestAuditLog logEntry = RequestAuditLog.builder()
                .clientId(clientId)
                .endpoint(endpoint)
                .allowed(allowed)
                .reason(reason)
                .build();
        auditRepository.save(logEntry);
        if (!allowed) {
            log.warn("Blocked request for client={} endpoint={} reason={}", clientId, endpoint, reason);
        }
    }
}
