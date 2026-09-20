package com.analytics.passwordhealth.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalAnalyzed;
    private double averageEntropy;
    private long totalBreached;
    private long weakCount;
    private long moderateCount;
    private long strongCount;
    private long criticalBreachCount;
}