package com.example.ratelimiter.service;

import com.example.ratelimiter.dto.MetricResponse;
import com.example.ratelimiter.repository.RequestAuditLogRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final RequestAuditLogRepository auditRepository;

    public MetricResponse getMetrics() {
        long allowed = auditRepository.countByAllowed(true);
        long blocked = auditRepository.countByAllowed(false);

        Map<String, Long> topClients = new LinkedHashMap<>();
        auditRepository.findTopClients().stream().limit(5)
                .forEach(row -> topClients.put((String) row[0], (Long) row[1]));

        Map<String, Long> topEndpoints = new LinkedHashMap<>();
        auditRepository.findTopEndpoints().stream().limit(5)
                .forEach(row -> topEndpoints.put((String) row[0], (Long) row[1]));

        return MetricResponse.builder()
                .allowedRequests(allowed)
                .blockedRequests(blocked)
                .redisHits(0)
                .averageResponseTimeMs(0.0)
                .topClients(topClients)
                .topEndpoints(topEndpoints)
                .build();
    }
}
