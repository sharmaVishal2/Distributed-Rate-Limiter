package com.example.ratelimiter.mapper;

import com.example.ratelimiter.dto.RateLimitRuleRequest;
import com.example.ratelimiter.dto.RateLimitRuleResponse;
import com.example.ratelimiter.entity.RateLimitRule;

public final class RateLimitRuleMapper {

    private RateLimitRuleMapper() {
    }

    public static RateLimitRule toEntity(RateLimitRuleRequest request) {
        return new RateLimitRule(
                null,
                request.clientId(),
                request.endpoint(),
                request.algorithm(),
                request.limit(),
                request.refillRate(),
                request.windowSize(),
                request.enabled(),
                java.time.Instant.now(),
                java.time.Instant.now()
        );
    }

    public static RateLimitRuleResponse toResponse(RateLimitRule entity) {
        return new RateLimitRuleResponse(
                entity.getId(),
                entity.getClientId(),
                entity.getEndpoint(),
                entity.getAlgorithm(),
                entity.getLimit(),
                entity.getRefillRate(),
                entity.getWindowSize(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
