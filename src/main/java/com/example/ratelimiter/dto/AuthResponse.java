package com.example.ratelimiter.dto;

public class AuthResponse {
    private final String accessToken;
    private final String tokenType;

    public AuthResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
