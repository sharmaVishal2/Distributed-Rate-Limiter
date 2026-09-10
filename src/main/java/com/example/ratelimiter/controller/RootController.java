package com.example.ratelimiter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Service Info", description = "Service status and discovery")
public class RootController {

    @GetMapping
    @Operation(summary = "Service info", description = "Returns service name, status, version, and documentation link.")
    public ResponseEntity<Map<String, String>> root() {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("service", "Distributed Rate Limiter Service");
        body.put("status", "UP");
        body.put("version", "1.0.0");
        body.put("documentation", "/swagger-ui/index.html");
        return ResponseEntity.ok(body);
    }
}
