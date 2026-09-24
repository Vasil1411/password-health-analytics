package com.analytics.passwordhealth.Controller;

import com.analytics.passwordhealth.DTO.PasswordAnalysisResponse;
import com.analytics.passwordhealth.DTO.PasswordCheckRequest;
import com.analytics.passwordhealth.service.PasswordHealthAggregatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PasswordAnalysisControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PasswordHealthAggregatorService aggregatorService;

    @InjectMocks
    private PasswordAnalysisController controller;

    @BeforeEach
    void setUp() {
        // Конфигурираме MockMvc за конкретния контролер без зареждане на целия Spring
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/v1/password/analyze - Should return 200 OK for valid request")
    void analyze_ValidRequest_Returns200OK() throws Exception {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("Secur3P@ssword2026");

        PasswordAnalysisResponse mockResponse = PasswordAnalysisResponse.builder()
                .entropyScore(70.0)
                .passwordLength(18)
                .estimatedCrackTime("100 years")
                .isBreached(false)
                .breachCount(0)
                .hasPatterns(false)
                .warnings(Collections.emptyList())
                .healthCategory("STRONG")
                .build();

        when(aggregatorService.analyzePassword(anyString())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthCategory").value("STRONG"))
                .andExpect(jsonPath("$.entropyScore").value(70.0));
    }

    @Test
    @DisplayName("POST /api/v1/password/analyze - Should return 400 Bad Request when password is empty")
    void analyze_EmptyPassword_Returns400BadRequest() throws Exception {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword(""); // Празен стринг

        mockMvc.perform(post("/api/v1/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/password/analyze - Should return WEAK category with warnings")
    void analyze_WeakPassword_ReturnsWeakCategoryWithWarnings() throws Exception {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("Qwerty1!"); // Вече минава @Pattern валидацията

        PasswordAnalysisResponse mockResponse = PasswordAnalysisResponse.builder()
                .entropyScore(28.5)
                .passwordLength(8)
                .estimatedCrackTime("5 minutes")
                .isBreached(false)
                .breachCount(0)
                .hasPatterns(true)
                .warnings(List.of("Sequential pattern detected: Qwerty"))
                .healthCategory("WEAK")
                .build();

        when(aggregatorService.analyzePassword("Qwerty1!")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthCategory").value("WEAK"))
                .andExpect(jsonPath("$.hasPatterns").value(true))
                .andExpect(jsonPath("$.warnings[0]").value("Sequential pattern detected: Qwerty"));
    }

    @Test
    @DisplayName("POST /api/v1/password/analyze - Should return CRITICAL_BREACH for leaked password")
    void analyze_BreachedPassword_ReturnsCriticalBreachCategory() throws Exception {
        PasswordCheckRequest request = new PasswordCheckRequest();
        request.setPassword("Password123!");

        PasswordAnalysisResponse mockResponse = PasswordAnalysisResponse.builder()
                .entropyScore(45.0)
                .passwordLength(12)
                .estimatedCrackTime("2 days")
                .isBreached(true)
                .breachCount(2386124L)
                .hasPatterns(true)
                .warnings(List.of("CRITICAL: This password has been leaked 2386124 times in data breaches!"))
                .healthCategory("CRITICAL_BREACH")
                .build();

        when(aggregatorService.analyzePassword("Password123!")).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/password/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthCategory").value("CRITICAL_BREACH"))
                .andExpect(jsonPath("$.breached").value(true)) // <-- Променено от $.isBreached на $.breached
                .andExpect(jsonPath("$.breachCount").value(2386124));
    }

    @Test
    @DisplayName("POST /api/v1/password/analyze - Should return 415 Unsupported Media Type when content type is not JSON")
    void analyze_InvalidContentType_Returns415UnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/v1/password/analyze")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("Secur3P@ssword2026"))
                .andExpect(status().isUnsupportedMediaType());
    }
}