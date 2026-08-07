package com.example.ratelimiter.controller;

import com.example.ratelimiter.dto.MetricResponse;
import com.example.ratelimiter.service.MetricService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping
    public ResponseEntity<MetricResponse> getMetrics() {
        return ResponseEntity.ok(metricService.getMetrics());
    }
}
