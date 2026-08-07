package com.example.ratelimiter.dto;

public class RateLimitCheckResponse {
    private final boolean allowed;
    private final String reason;

    public RateLimitCheckResponse(boolean allowed, String reason) {
        this.allowed = allowed;
        this.reason = reason;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }
}
