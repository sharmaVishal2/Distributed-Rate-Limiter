package com.example.ratelimiter.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RateLimitCheckResponse {
    private final boolean allowed;
    private final String reason;
}
