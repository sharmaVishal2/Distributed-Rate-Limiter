package com.example.ratelimiter.dto;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import java.time.Instant;

public class RateLimitRuleResponse {
    private final Long id;
    private final String clientId;
    private final String endpoint;
    private final RateLimitAlgorithm algorithm;
    private final long limit;
    private final long refillRate;
    private final long windowSize;
    private final boolean enabled;
    private final Instant createdAt;
    private final Instant updatedAt;

    public RateLimitRuleResponse(Long id, String clientId, String endpoint, RateLimitAlgorithm algorithm, long limit, long refillRate, long windowSize, boolean enabled, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.clientId = clientId;
        this.endpoint = endpoint;
        this.algorithm = algorithm;
        this.limit = limit;
        this.refillRate = refillRate;
        this.windowSize = windowSize;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public RateLimitAlgorithm getAlgorithm() {
        return algorithm;
    }

    public long getLimit() {
        return limit;
    }

    public long getRefillRate() {
        return refillRate;
    }

    public long getWindowSize() {
        return windowSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public java.time.Instant getCreatedAt() {
        return createdAt;
    }

    public java.time.Instant getUpdatedAt() {
        return updatedAt;
    }
}
