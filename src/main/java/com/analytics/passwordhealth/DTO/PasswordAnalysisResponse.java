package com.analytics.passwordhealth.DTO;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PasswordAnalysisResponse {
    private double entropyScore;
    private int passwordLength;
    private String estimatedCrackTime;
    private boolean isBreached;
    private long breachCount;
    private boolean hasPatterns;
    private List<String> warnings;
    private String healthCategory; // WEAK, MODERATE, STRONG, CRITICAL_BREACH
}