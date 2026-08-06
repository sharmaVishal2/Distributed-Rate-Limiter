package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.Instant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RateLimitServiceTest {

    @Mock
    private RateLimitRuleRepository ruleRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private AuditService auditService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void check_shouldThrowWhenNoRuleExists() {
        when(ruleRepository.findByClientId("client-a")).thenReturn(Collections.emptyList());
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        assertThrows(RateLimitExceededException.class, () -> rateLimitService.check(request));
    }

    @Test
    void check_shouldThrowWhenRuleDisabled() {
        RateLimitRule disabledRule = RateLimitRule.builder()
                .clientId("client-a").endpoint("/api/check")
                .algorithm(RateLimitAlgorithm.FIXED_WINDOW_COUNTER)
                .limit(10).refillRate(1).windowSize(60).enabled(false)
                .build();
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of(disabledRule));
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        assertThrows(RateLimitExceededException.class, () -> rateLimitService.check(request));
    }

    @Test
    void check_fixedWindow_shouldAllowWhenUnderLimit() {
        RateLimitRule rule = RateLimitRule.builder()
                .clientId("client-a").endpoint("/api/check")
                .algorithm(RateLimitAlgorithm.FIXED_WINDOW_COUNTER)
                .limit(10).refillRate(1).windowSize(60).enabled(true)
                .build();
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of(rule));
        when(valueOperations.increment(anyString())).thenReturn(1L);
        when(redisTemplate.expireAt(anyString(), any(Instant.class))).thenReturn(true);

        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        var response = rateLimitService.check(request);
        assertTrue(response.isAllowed());
    }

    @Test
    void check_fixedWindow_shouldThrowWhenOverLimit() {
        RateLimitRule rule = RateLimitRule.builder()
                .clientId("client-a").endpoint("/api/check")
                .algorithm(RateLimitAlgorithm.FIXED_WINDOW_COUNTER)
                .limit(5).refillRate(1).windowSize(60).enabled(true)
                .build();
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of(rule));
        when(valueOperations.increment(anyString())).thenReturn(6L);
        when(redisTemplate.expireAt(anyString(), any(Instant.class))).thenReturn(true);

        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        assertThrows(RateLimitExceededException.class, () -> rateLimitService.check(request));
    }
}
