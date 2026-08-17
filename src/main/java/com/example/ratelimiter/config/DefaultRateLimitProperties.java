package com.example.ratelimiter.config;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Default rule used when no client-specific rule is configured. */
@Component
@ConfigurationProperties(prefix = "rate-limit.default")
public class DefaultRateLimitProperties {

    private long limit = 5;
    private long windowSeconds = 60;
    private RateLimitAlgorithm algorithm = RateLimitAlgorithm.FIXED_WINDOW_COUNTER;

    public long getLimit() { return limit; }
    public void setLimit(long limit) { this.limit = limit; }
    public long getWindowSeconds() { return windowSeconds; }
    public void setWindowSeconds(long windowSeconds) { this.windowSeconds = windowSeconds; }
    public RateLimitAlgorithm getAlgorithm() { return algorithm; }
    public void setAlgorithm(RateLimitAlgorithm algorithm) { this.algorithm = algorithm; }
}
