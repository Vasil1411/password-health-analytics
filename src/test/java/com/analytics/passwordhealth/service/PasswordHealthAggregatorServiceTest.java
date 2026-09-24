package com.analytics.passwordhealth.service;

import com.analytics.passwordhealth.DTO.PasswordAnalysisResponse;
import com.analytics.passwordhealth.Entity.AuditRecord;
import com.analytics.passwordhealth.Repository.AuditRecordRepository;
import com.analytics.passwordhealth.DTO.DashboardStatsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordHealthAggregatorServiceTest {

    @Mock
    private EntropyCalculatorService entropyCalculator;

    @Mock
    private PatternAnalysisService patternAnalysis;

    @Mock
    private HibpBreachCheckService breachCheck;

    @Mock
    private AuditRecordRepository auditRecordRepository;

    @InjectMocks
    private PasswordHealthAggregatorService aggregatorService;

    @Test
    @DisplayName("Should return STRONG health category for secure password")
    void analyzePassword_StrongPassword_ReturnsStrongCategory() {
        String rawPassword = "K9#mX$8!vL2pQ1";

        when(entropyCalculator.calculateEntropy(rawPassword)).thenReturn(75.0);
        when(entropyCalculator.estimateCrackTime(75.0)).thenReturn("3 centuries");
        when(patternAnalysis.analyzePatterns(rawPassword)).thenReturn(new ArrayList<>());
        when(breachCheck.checkBreachCount(rawPassword)).thenReturn(0L);

        PasswordAnalysisResponse response = aggregatorService.analyzePassword(rawPassword);

        assertNotNull(response);
        assertEquals(75.0, response.getEntropyScore());
        assertEquals("3 centuries", response.getEstimatedCrackTime());
        assertFalse(response.isBreached());
        assertEquals("STRONG", response.getHealthCategory());
        verify(auditRecordRepository, times(1)).save(any(AuditRecord.class));
    }

    @Test
    @DisplayName("Should return CRITICAL_BREACH when password appears in HIBP leaks")
    void analyzePassword_LeakedPassword_ReturnsCriticalBreachCategory() {
        String leakedPassword = "Password123!";

        when(entropyCalculator.calculateEntropy(leakedPassword)).thenReturn(50.0);
        when(entropyCalculator.estimateCrackTime(50.0)).thenReturn("2 days");
        when(patternAnalysis.analyzePatterns(leakedPassword)).thenReturn(new ArrayList<>());
        when(breachCheck.checkBreachCount(leakedPassword)).thenReturn(1500L);

        PasswordAnalysisResponse response = aggregatorService.analyzePassword(leakedPassword);

        assertNotNull(response);
        assertTrue(response.isBreached());
        assertEquals(1500L, response.getBreachCount());
        assertEquals("CRITICAL_BREACH", response.getHealthCategory());
        assertTrue(response.getWarnings().get(0).contains("CRITICAL"));
        verify(auditRecordRepository, times(1)).save(any(AuditRecord.class));
    }


    @Test
    @DisplayName("Should handle null or empty password gracefully")
    void analyzePassword_NullOrEmptyPassword_ReturnsWeakCategory() {
        // Given
        String nullPassword = null;
        when(entropyCalculator.calculateEntropy("")).thenReturn(0.0);
        when(entropyCalculator.estimateCrackTime(0.0)).thenReturn("0 seconds");
        when(patternAnalysis.analyzePatterns("")).thenReturn(List.of("Password is empty"));
        when(breachCheck.checkBreachCount("")).thenReturn(0L);

        // When
        PasswordAnalysisResponse response = aggregatorService.analyzePassword(nullPassword);

        // Then
        assertNotNull(response);
        assertEquals(0.0, response.getEntropyScore());
        assertEquals(0, response.getPasswordLength());
        assertEquals("WEAK", response.getHealthCategory());
        assertTrue(response.isHasPatterns());
        verify(auditRecordRepository, times(1)).save(any(AuditRecord.class));
    }

    @Test
    @DisplayName("Should correct aggregate dashboard stats when records exist")
    void getAnalyticsSummary_RecordsExist_ReturnsCorrectDto() {
        // Given
        when(auditRecordRepository.count()).thenReturn(10L);
        when(auditRecordRepository.countByIsBreachedTrue()).thenReturn(3L);
        when(auditRecordRepository.findAverageEntropy()).thenReturn(54.3219); // За закръгляне до 54.32
        when(auditRecordRepository.countByHealthCategory("WEAK")).thenReturn(2L);
        when(auditRecordRepository.countByHealthCategory("MODERATE")).thenReturn(3L);
        when(auditRecordRepository.countByHealthCategory("STRONG")).thenReturn(2L);
        when(auditRecordRepository.countByHealthCategory("CRITICAL_BREACH")).thenReturn(3L);

        // When
        DashboardStatsDto stats = aggregatorService.getAnalyticsSummary();

        // Then
        assertNotNull(stats);
        assertEquals(10L, stats.getTotalAnalyzed());
        assertEquals(3L, stats.getTotalBreached());
        assertEquals(54.32, stats.getAverageEntropy());
        assertEquals(2L, stats.getWeakCount());
        assertEquals(3L, stats.getModerateCount());
        assertEquals(2L, stats.getStrongCount());
        assertEquals(3L, stats.getCriticalBreachCount());
    }

    @Test
    @DisplayName("Should return zeroed stats when database is empty")
    void getAnalyticsSummary_EmptyDatabase_ReturnsZeroedDto() {
        // Given
        when(auditRecordRepository.count()).thenReturn(0L);

        // When
        DashboardStatsDto stats = aggregatorService.getAnalyticsSummary();

        // Then
        assertNotNull(stats);
        assertEquals(0L, stats.getTotalAnalyzed());
        assertEquals(0.0, stats.getAverageEntropy());
        assertEquals(0L, stats.getWeakCount());

        // Уверяваме се, че не правим ненужни заявки към базата при 0 записа
        verify(auditRecordRepository, never()).countByIsBreachedTrue();
    }



}

