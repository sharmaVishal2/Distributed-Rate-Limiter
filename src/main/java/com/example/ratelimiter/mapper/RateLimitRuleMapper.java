package com.example.ratelimiter.mapper;

import com.example.ratelimiter.dto.RateLimitRuleRequest;
import com.example.ratelimiter.dto.RateLimitRuleResponse;
import com.example.ratelimiter.entity.RateLimitRule;

public final class RateLimitRuleMapper {

    private RateLimitRuleMapper() {
    }

    public static RateLimitRule toEntity(RateLimitRuleRequest request) {
        return RateLimitRule.builder()
                .clientId(request.clientId())
                .endpoint(request.endpoint())
                .algorithm(request.algorithm())
                .limit(request.limit())
                .refillRate(request.refillRate())
                .windowSize(request.windowSize())
                .enabled(request.enabled())
                .build();
    }

    public static RateLimitRuleResponse toResponse(RateLimitRule entity) {
        return RateLimitRuleResponse.builder()
                .id(entity.getId())
                .clientId(entity.getClientId())
                .endpoint(entity.getEndpoint())
                .algorithm(entity.getAlgorithm())
                .limit(entity.getLimit())
                .refillRate(entity.getRefillRate())
                .windowSize(entity.getWindowSize())
                .enabled(entity.isEnabled())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
