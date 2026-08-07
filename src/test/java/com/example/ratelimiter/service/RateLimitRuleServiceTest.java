package com.example.ratelimiter.service;

import com.example.ratelimiter.algorithm.RateLimitAlgorithm;
import com.example.ratelimiter.dto.RateLimitRuleRequest;
import com.example.ratelimiter.dto.RateLimitRuleResponse;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.NotFoundException;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import com.example.ratelimiter.validator.RateLimitRuleValidator;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RateLimitRuleServiceTest {

    @Mock
    private RateLimitRuleRepository ruleRepository;

    @Mock
    private RateLimitRuleValidator validator;

    @InjectMocks
    private RateLimitRuleService ruleService;

    private RateLimitRule sampleRule;
    private RateLimitRuleRequest sampleRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleRequest = new RateLimitRuleRequest("client-a", "/api/check", RateLimitAlgorithm.TOKEN_BUCKET, 10, 1, 60, true);
        sampleRule = new RateLimitRule(
                1L,
                "client-a",
                "/api/check",
                RateLimitAlgorithm.TOKEN_BUCKET,
                10,
                1,
                60,
                true,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void createRule_shouldReturnResponse() {
        when(ruleRepository.save(any())).thenReturn(sampleRule);
        RateLimitRuleResponse response = ruleService.createRule(sampleRequest);
        assertNotNull(response);
        assertEquals("client-a", response.getClientId());
        verify(validator).validate(sampleRequest);
    }

    @Test
    void updateRule_shouldUpdateAndReturn() {
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(sampleRule));
        when(ruleRepository.save(any())).thenReturn(sampleRule);
        RateLimitRuleResponse response = ruleService.updateRule(1L, sampleRequest);
        assertNotNull(response);
        verify(validator).validate(sampleRequest);
    }

    @Test
    void updateRule_shouldThrowWhenNotFound() {
        when(ruleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> ruleService.updateRule(99L, sampleRequest));
    }

    @Test
    void deleteRule_shouldDeleteWhenExists() {
        when(ruleRepository.findById(1L)).thenReturn(Optional.of(sampleRule));
        ruleService.deleteRule(1L);
        verify(ruleRepository).deleteById(1L);
    }

    @Test
    void deleteRule_shouldThrowWhenNotFound() {
        when(ruleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> ruleService.deleteRule(99L));
    }

    @Test
    void getAllRules_shouldReturnList() {
        when(ruleRepository.findAll()).thenReturn(List.of(sampleRule));
        List<RateLimitRuleResponse> rules = ruleService.getAllRules();
        assertEquals(1, rules.size());
    }

    @Test
    void getRuleById_shouldThrowWhenNotFound() {
        when(ruleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> ruleService.getRuleById(99L));
    }
}
