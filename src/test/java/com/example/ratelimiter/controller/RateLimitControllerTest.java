package com.example.ratelimiter.controller;

import com.example.ratelimiter.config.SecurityConfig;
import com.example.ratelimiter.dto.RateLimitCheckRequest;
import com.example.ratelimiter.dto.RateLimitCheckResponse;
import com.example.ratelimiter.security.JwtAuthenticationEntryPoint;
import com.example.ratelimiter.security.JwtAuthenticationFilter;
import com.example.ratelimiter.security.JwtTokenProvider;
import com.example.ratelimiter.service.AppUserDetailsService;
import com.example.ratelimiter.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RateLimitController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class})
class RateLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RateLimitService rateLimitService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        Mockito.doAnswer(inv -> {
            inv.getArgument(2, FilterChain.class).doFilter(
                    inv.getArgument(0, HttpServletRequest.class),
                    inv.getArgument(1, HttpServletResponse.class));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @WithMockUser
    void check_shouldReturnAllowedResponse() throws Exception {
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");
        RateLimitCheckResponse response = new RateLimitCheckResponse(true, "allowed");

        when(rateLimitService.check(any())).thenReturn(response);

        mockMvc.perform(post("/api/check")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.reason").value("allowed"));
    }

    @Test
    @WithMockUser
    void check_shouldReturn400WhenClientIdBlank() throws Exception {
        RateLimitCheckRequest request = new RateLimitCheckRequest("", "/api/check");

        mockMvc.perform(post("/api/check")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void check_shouldReturn401WhenUnauthenticated() throws Exception {
        RateLimitCheckRequest request = new RateLimitCheckRequest("client-a", "/api/check");

        mockMvc.perform(post("/api/check")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
