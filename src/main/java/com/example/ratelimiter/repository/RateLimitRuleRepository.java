package com.example.ratelimiter.repository;

import com.example.ratelimiter.entity.RateLimitRule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, Long> {
    List<RateLimitRule> findByClientId(String clientId);
}
