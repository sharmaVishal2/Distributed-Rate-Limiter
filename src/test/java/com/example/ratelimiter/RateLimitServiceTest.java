package com.example.ratelimiter;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import com.example.ratelimiter.service.AuditService;
import com.example.ratelimiter.service.RateLimitService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;

class RateLimitServiceTest {

    @Test
    void shouldThrowWhenNoRuleExists() {
        RateLimitRuleRepository ruleRepository = Mockito.mock(RateLimitRuleRepository.class);
        StringRedisTemplate redisTemplate = Mockito.mock(StringRedisTemplate.class);
        AuditService auditService = Mockito.mock(AuditService.class);
        RateLimitService service = new RateLimitService(ruleRepository, redisTemplate, auditService);

        Mockito.when(ruleRepository.findByClientId("client-a")).thenReturn(java.util.Collections.emptyList());

        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");

        Assertions.assertThrows(RateLimitExceededException.class, () -> service.check(request));
    }
}
