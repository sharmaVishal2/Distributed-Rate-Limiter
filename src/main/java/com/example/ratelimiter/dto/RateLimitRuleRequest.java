package com.example.ratelimiter.dto;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RateLimitRuleRequest(
        @NotBlank String clientId,
        @NotBlank String endpoint,
        @NotNull RateLimitAlgorithm algorithm,
        @Min(1) long limit,
        @Min(1) long refillRate,
        @Min(1) long windowSize,
        @NotNull Boolean enabled
) {
}
