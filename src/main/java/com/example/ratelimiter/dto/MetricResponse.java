package com.example.ratelimiter.dto;

import java.util.Map;

public class MetricResponse {
    private final long allowedRequests;
    private final long blockedRequests;
    private final long redisHits;
    private final double averageResponseTimeMs;
    private final Map<String, Long> topClients;
    private final Map<String, Long> topEndpoints;

    public MetricResponse(long allowedRequests, long blockedRequests, long redisHits, double averageResponseTimeMs, Map<String, Long> topClients, Map<String, Long> topEndpoints) {
        this.allowedRequests = allowedRequests;
        this.blockedRequests = blockedRequests;
        this.redisHits = redisHits;
        this.averageResponseTimeMs = averageResponseTimeMs;
        this.topClients = topClients;
        this.topEndpoints = topEndpoints;
    }

    public long getAllowedRequests() {
        return allowedRequests;
    }

    public long getBlockedRequests() {
        return blockedRequests;
    }

    public long getRedisHits() {
        return redisHits;
    }

    public double getAverageResponseTimeMs() {
        return averageResponseTimeMs;
    }

    public Map<String, Long> getTopClients() {
        return topClients;
    }

    public Map<String, Long> getTopEndpoints() {
        return topEndpoints;
    }
}
