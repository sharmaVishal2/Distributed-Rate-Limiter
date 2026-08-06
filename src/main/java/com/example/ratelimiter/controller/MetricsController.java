package com.example.ratelimiter.controller;

import com.example.ratelimiter.dto.MetricResponse;
import com.example.ratelimiter.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricService metricService;

    @GetMapping
    public ResponseEntity<MetricResponse> getMetrics() {
        return ResponseEntity.ok(metricService.getMetrics());
    }
}
