package com.analytics.passwordhealth.service;

import com.analytics.passwordhealth.DTO.DashboardStatsDto;
import com.analytics.passwordhealth.DTO.PasswordAnalysisResponse;
import com.analytics.passwordhealth.Entity.AuditRecord;
import com.analytics.passwordhealth.Repository.AuditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordHealthAggregatorService {

    private final EntropyCalculatorService entropyCalculator;
    private final PatternAnalysisService patternAnalysis;
    private final HibpBreachCheckService breachCheck;
    private final AuditRecordRepository auditRecordRepository;

    public PasswordAnalysisResponse analyzePassword(String password) {
        if (password == null) {
            password = "";
        }

        double entropy = entropyCalculator.calculateEntropy(password);
        String crackTime = entropyCalculator.estimateCrackTime(entropy);
        List<String> warnings = patternAnalysis.analyzePatterns(password);
        boolean hasPatterns = !warnings.isEmpty();

        long breachCount = breachCheck.checkBreachCount(password);
        boolean isBreached = breachCount > 0;

        if (isBreached) {
            warnings.add(0, "CRITICAL: This password has been leaked " + breachCount + " times in data breaches!");
        }

        String category = determineCategory(entropy, isBreached, hasPatterns);

        AuditRecord record = AuditRecord.builder()
                .entropyScore(entropy)
                .passwordLength(password.length())
                .isBreached(isBreached)
                .breachCount(breachCount)
                .hasPatterns(hasPatterns)
                .healthCategory(category)
                .build();

        auditRecordRepository.save(record);

        return PasswordAnalysisResponse.builder()
                .entropyScore(entropy)
                .passwordLength(password.length())
                .estimatedCrackTime(crackTime)
                .isBreached(isBreached)
                .breachCount(breachCount)
                .hasPatterns(hasPatterns)
                .warnings(warnings)
                .healthCategory(category)
                .build();
    }

    private String determineCategory(double entropy, boolean isBreached, boolean hasPatterns) {
        if (isBreached) return "CRITICAL_BREACH";
        if (entropy < 30 || hasPatterns) return "WEAK";
        if (entropy < 60) return "MODERATE";
        return "STRONG";
    }

    public DashboardStatsDto getAnalyticsSummary() {
        long totalAnalyzed = auditRecordRepository.count();

        if (totalAnalyzed == 0) {
            return DashboardStatsDto.builder()
                    .totalAnalyzed(0)
                    .totalBreached(0)
                    .averageEntropy(0.0)
                    .weakCount(0)
                    .moderateCount(0)
                    .strongCount(0)
                    .criticalBreachCount(0)
                    .build();
        }

        long totalBreached = auditRecordRepository.countByIsBreachedTrue();
        Double avgEntropy = auditRecordRepository.findAverageEntropy();
        double averageEntropy = (avgEntropy != null) ? Math.round(avgEntropy * 100.0) / 100.0 : 0.0;

        return DashboardStatsDto.builder()
                .totalAnalyzed(totalAnalyzed)
                .totalBreached(totalBreached)
                .averageEntropy(averageEntropy)
                .weakCount(auditRecordRepository.countByHealthCategory("WEAK"))
                .moderateCount(auditRecordRepository.countByHealthCategory("MODERATE"))
                .strongCount(auditRecordRepository.countByHealthCategory("STRONG"))
                .criticalBreachCount(auditRecordRepository.countByHealthCategory("CRITICAL_BREACH"))
                .build();
    }
}