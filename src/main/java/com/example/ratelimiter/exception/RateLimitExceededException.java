package com.example.ratelimiter.exception;

public class RateLimitExceededException extends RuntimeException {
    private final long retryAfter;

    public RateLimitExceededException(String message) {
        this(message, 60);
    }

    public RateLimitExceededException(String message, long retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public long getRetryAfter() {
        return retryAfter;
    }
}
