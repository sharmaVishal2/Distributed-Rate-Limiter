package com.example.ratelimiter.service;

import com.example.ratelimiter.dto.RateLimitRuleRequest;
import com.example.ratelimiter.dto.RateLimitRuleResponse;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.NotFoundException;
import com.example.ratelimiter.mapper.RateLimitRuleMapper;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import com.example.ratelimiter.validator.RateLimitRuleValidator;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RateLimitRuleService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitRuleService.class);

    private final RateLimitRuleRepository ruleRepository;
    private final RateLimitRuleValidator validator;

    public RateLimitRuleResponse createRule(RateLimitRuleRequest request) {
        validator.validate(request);
        RateLimitRule saved = ruleRepository.save(RateLimitRuleMapper.toEntity(request));
        log.info("Created rate limit rule id={} clientId={} algorithm={}", saved.getId(), saved.getClientId(), saved.getAlgorithm());
        return RateLimitRuleMapper.toResponse(saved);
    }

    public RateLimitRuleResponse updateRule(Long id, RateLimitRuleRequest request) {
        validator.validate(request);
        RateLimitRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate limit rule not found: " + id));
        rule.setClientId(request.clientId());
        rule.setEndpoint(request.endpoint());
        rule.setAlgorithm(request.algorithm());
        rule.setLimit(request.limit());
        rule.setRefillRate(request.refillRate());
        rule.setWindowSize(request.windowSize());
        rule.setEnabled(request.enabled());
        rule.setUpdatedAt(Instant.now());
        RateLimitRuleResponse response = RateLimitRuleMapper.toResponse(ruleRepository.save(rule));
        log.info("Updated rate limit rule id={} clientId={}", id, request.clientId());
        return response;
    }

    public void deleteRule(Long id) {
        ruleRepository.findById(id).orElseThrow(() -> new NotFoundException("Rate limit rule not found: " + id));
        ruleRepository.deleteById(id);
        log.info("Deleted rate limit rule id={}", id);
    }

    public List<RateLimitRuleResponse> getAllRules() {
        return ruleRepository.findAll().stream().map(RateLimitRuleMapper::toResponse).toList();
    }

    public RateLimitRuleResponse getRuleById(Long id) {
        return RateLimitRuleMapper.toResponse(ruleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rate limit rule not found: " + id)));
    }
}
