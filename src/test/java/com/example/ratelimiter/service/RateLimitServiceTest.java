package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.config.DefaultRateLimitProperties;
import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitServiceTest {

    @Mock private RateLimitRuleRepository ruleRepository;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private AuditService auditService;

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        DefaultRateLimitProperties defaults = new DefaultRateLimitProperties();
        defaults.setLimit(5);
        defaults.setWindowSeconds(60);
        defaults.setAlgorithm(RateLimitAlgorithm.FIXED_WINDOW_COUNTER);
        rateLimitService = new RateLimitService(ruleRepository, redisTemplate, auditService, defaults);
    }

    @Test
    void defaultFixedWindow_allowsFirstFiveAndRejectsSixth() {
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of());
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class)))
                .thenReturn(1L, 1L, 1L, 1L, 1L, 0L);

        for (int requestNumber = 0; requestNumber < 5; requestNumber++) {
            assertTrue(rateLimitService.check(request).isAllowed());
        }

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class,
                () -> rateLimitService.check(request));
        assertEquals("Rate limit exceeded. Maximum 5 requests allowed.", exception.getMessage());
        assertEquals(60, exception.getRetryAfter());
    }

    @Test
    void fixedWindow_allowsRequestAfterRedisCounterExpires() {
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of());
        // The final 1 represents Redis creating a fresh counter after its 60-second TTL expires.
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class)))
                .thenReturn(0L, 1L);

        assertThrows(RateLimitExceededException.class, () -> rateLimitService.check(request));
        assertTrue(rateLimitService.check(request).isAllowed());
    }

    @Test
    void defaultFixedWindow_usesIndependentRedisKeysForDifferentClients() {
        RateLimitCheckRequest firstClient = new RateLimitCheckRequest("client-a", "/api/check");
        RateLimitCheckRequest secondClient = new RateLimitCheckRequest("client-b", "/api/check");
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of());
        when(ruleRepository.findByClientId("client-b")).thenReturn(List.of());
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class))).thenReturn(1L);

        rateLimitService.check(firstClient);
        rateLimitService.check(secondClient);

        ArgumentCaptor<List<String>> keys = ArgumentCaptor.forClass(List.class);
        verify(redisTemplate, org.mockito.Mockito.times(2))
                .execute(any(DefaultRedisScript.class), keys.capture(), any(Object[].class));
        assertTrue(keys.getAllValues().get(0).getFirst().contains("client-a"));
        assertTrue(keys.getAllValues().get(1).getFirst().contains("client-b"));
    }

    @Test
    void configuredRule_takesPrecedenceOverDefault() {
        RateLimitRule rule = new RateLimitRule(null, "client-a", "/api/check",
                RateLimitAlgorithm.FIXED_WINDOW_COUNTER, 10, 1, 60, true, Instant.now(), Instant.now());
        when(ruleRepository.findByClientId("client-a")).thenReturn(List.of(rule));
        when(redisTemplate.execute(any(DefaultRedisScript.class), anyList(), any(Object[].class))).thenReturn(1L);

        assertTrue(rateLimitService.check(new RateLimitCheckRequest("client-a", "/api/check")).isAllowed());
    }
}
