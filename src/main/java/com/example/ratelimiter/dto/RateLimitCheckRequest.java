package com.example.ratelimiter.dto;

import jakarta.validation.constraints.NotBlank;

public record RateLimitCheckRequest(
        @NotBlank String clientId,
        @NotBlank String endpoint
) {
}
