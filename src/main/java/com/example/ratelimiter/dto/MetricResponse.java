package com.example.ratelimiter.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
public class MetricResponse {
    private final long allowedRequests;
    private final long blockedRequests;
    private final long redisHits;
    private final double averageResponseTimeMs;
    private final Map<String, Long> topClients;
    private final Map<String, Long> topEndpoints;
}
