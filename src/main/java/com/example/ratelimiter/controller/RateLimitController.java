package com.example.ratelimiter.controller;

import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.dto.RateLimitCheckResponse;
import com.example.ratelimiter.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    @Operation(summary = "Check a client's rate limit", description = "The default rule allows 5 requests per client and endpoint in 60 seconds using a fixed-window counter. The sixth request is rejected.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request allowed"),
            @ApiResponse(responseCode = "429", description = "Default limit exceeded", content = @Content(
                    examples = @ExampleObject(value = "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Maximum 5 requests allowed.\",\"retryAfter\":60}")))
    })
    public ResponseEntity<RateLimitCheckResponse> check(@Valid @RequestBody RateLimitCheckRequest request) {
        return ResponseEntity.ok(rateLimitService.check(request));
    }
}
