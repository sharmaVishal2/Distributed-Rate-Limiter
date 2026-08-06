package com.example.ratelimiter.validator;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.dto.RateLimitRuleRequest;
import com.example.ratelimiter.exception.RateLimitExceededException;
import org.springframework.stereotype.Component;

@Component
public class RateLimitRuleValidator {

    public void validate(RateLimitRuleRequest request) {
        if (request.algorithm() == RateLimitAlgorithm.TOKEN_BUCKET && request.refillRate() <= 0) {
            throw new IllegalArgumentException("TOKEN_BUCKET algorithm requires refillRate > 0");
        }
        if ((request.algorithm() == RateLimitAlgorithm.FIXED_WINDOW_COUNTER
                || request.algorithm() == RateLimitAlgorithm.SLIDING_WINDOW_LOG)
                && request.windowSize() <= 0) {
            throw new IllegalArgumentException(request.algorithm() + " algorithm requires windowSize > 0");
        }
    }
}
