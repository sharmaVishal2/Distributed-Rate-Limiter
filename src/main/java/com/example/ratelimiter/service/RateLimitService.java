package com.example.ratelimiter.service;

import com.example.ratelimiter.config.DefaultRateLimitProperties;
import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.dto.RateLimitCheckResponse;
import com.example.ratelimiter.entity.RateLimitRule;
import com.example.ratelimiter.exception.RateLimitExceededException;
import com.example.ratelimiter.repository.RateLimitRuleRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final Logger log = LoggerFactory.getLogger(RateLimitService.class);

    private static final DefaultRedisScript<Long> TOKEN_BUCKET_SCRIPT;
    private static final DefaultRedisScript<Long> FIXED_WINDOW_SCRIPT;
    private static final DefaultRedisScript<Long> SLIDING_WINDOW_SCRIPT;

    static {
        TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>();
        TOKEN_BUCKET_SCRIPT.setResultType(Long.class);
        TOKEN_BUCKET_SCRIPT.setScriptText(
                "local tokens = tonumber(redis.call('GET', KEYS[1]) or ARGV[1])\n" +
                "local capacity = tonumber(ARGV[1])\n" +
                "local rate = tonumber(ARGV[2])\n" +
                "local now = tonumber(ARGV[3])\n" +
                "local last = tonumber(redis.call('GET', KEYS[2]) or now)\n" +
                "local delta = math.max(0, now - last)\n" +
                "local refill = math.min(capacity, tokens + delta * rate)\n" +
                "if refill < 1 then\n" +
                "  redis.call('SET', KEYS[1], refill)\n" +
                "  redis.call('SET', KEYS[2], now)\n" +
                "  return 0\n" +
                "end\n" +
                "redis.call('SET', KEYS[1], refill - 1)\n" +
                "redis.call('SET', KEYS[2], now)\n" +
                "return 1"
        );

        FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>();
        FIXED_WINDOW_SCRIPT.setResultType(Long.class);
        FIXED_WINDOW_SCRIPT.setScriptText(
                "local count = redis.call('INCR', KEYS[1])\n" +
                "if count == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end\n" +
                "if count > tonumber(ARGV[2]) then return 0 else return 1 end"
        );

        SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>();
        SLIDING_WINDOW_SCRIPT.setResultType(Long.class);
        SLIDING_WINDOW_SCRIPT.setScriptText(
                "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1])\n" +
                "redis.call('ZADD', KEYS[1], ARGV[2], ARGV[2])\n" +
                "local count = redis.call('ZCOUNT', KEYS[1], ARGV[1], ARGV[2])\n" +
                "redis.call('EXPIRE', KEYS[1], ARGV[3])\n" +
                "if tonumber(count) > tonumber(ARGV[4]) then return 0 else return 1 end"
        );
    }

    private final RateLimitRuleRepository ruleRepository;
    private final StringRedisTemplate redisTemplate;
    private final AuditService auditService;
    private final DefaultRateLimitProperties defaultRateLimit;

    public RateLimitService(RateLimitRuleRepository ruleRepository, StringRedisTemplate redisTemplate,
                            AuditService auditService, DefaultRateLimitProperties defaultRateLimit) {
        this.ruleRepository = ruleRepository;
        this.redisTemplate = redisTemplate;
        this.auditService = auditService;
        this.defaultRateLimit = defaultRateLimit;
    }

    public RateLimitCheckResponse check(RateLimitCheckRequest request) {
        RateLimitRule rule = ruleRepository.findByClientId(request.clientId()).stream()
                .filter(r -> r.getEndpoint().equals(request.endpoint()) && r.isEnabled())
                .findFirst()
                .orElseGet(() -> defaultRule(request.endpoint()));

        boolean allowed = switch (rule.getAlgorithm()) {
            case TOKEN_BUCKET -> checkTokenBucket(rule, request.clientId());
            case FIXED_WINDOW_COUNTER -> checkFixedWindow(rule, request.clientId());
            case SLIDING_WINDOW_LOG -> checkSlidingWindow(rule, request.clientId());
        };

        String reason = allowed ? "allowed" : "rate limit exceeded";
        auditService.record(request.clientId(), request.endpoint(), allowed, reason);

        if (!allowed) {
            log.warn("Rate limit exceeded for client={} endpoint={} algorithm={}", request.clientId(), request.endpoint(), rule.getAlgorithm());
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Maximum " + rule.getLimit() + " requests allowed.", rule.getWindowSize());
        }
        return new RateLimitCheckResponse(true, reason);
    }

    private boolean checkTokenBucket(RateLimitRule rule, String clientId) {
        String key = buildKey(rule, clientId, "token_bucket");
        Long result = redisTemplate.execute(
                TOKEN_BUCKET_SCRIPT,
                List.of(key, key + ":last"),
                String.valueOf(rule.getLimit()),
                String.valueOf(rule.getRefillRate()),
                String.valueOf(Instant.now().getEpochSecond())
        );
        return Long.valueOf(1L).equals(result);
    }

    private boolean checkFixedWindow(RateLimitRule rule, String clientId) {
        Long result = redisTemplate.execute(
                FIXED_WINDOW_SCRIPT,
                List.of(buildKey(rule, clientId, "fixed_window")),
                String.valueOf(rule.getWindowSize()),
                String.valueOf(rule.getLimit())
        );
        return Long.valueOf(1L).equals(result);
    }

    private boolean checkSlidingWindow(RateLimitRule rule, String clientId) {
        String key = buildKey(rule, clientId, "sliding_window");
        long now = Instant.now().getEpochSecond();
        long windowStart = now - rule.getWindowSize();
        Long result = redisTemplate.execute(
                SLIDING_WINDOW_SCRIPT,
                List.of(key),
                String.valueOf(windowStart),
                String.valueOf(now),
                String.valueOf(rule.getWindowSize() + 5),
                String.valueOf(rule.getLimit())
        );
        return Long.valueOf(1L).equals(result);
    }

    private RateLimitRule defaultRule(String endpoint) {
        return new RateLimitRule(null, "default", endpoint, defaultRateLimit.getAlgorithm(),
                defaultRateLimit.getLimit(), 1, defaultRateLimit.getWindowSeconds(), true, Instant.now(), Instant.now());
    }

    private String buildKey(RateLimitRule rule, String clientId, String suffix) {
        return String.format("ratelimit:%s:%s:%s", clientId, rule.getEndpoint(), suffix);
    }
}
