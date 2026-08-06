package com.example.ratelimiter.dto;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
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
}
