package com.analytics.passwordhealth.Controller;

import com.analytics.passwordhealth.DTO.DashboardStatsDto;
import com.analytics.passwordhealth.Repository.AuditRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsDashboardController {

    private final AuditRecordRepository auditRecordRepository;

    @GetMapping("/summary")
    public DashboardStatsDto getSummaryStats() {
        long total = auditRecordRepository.count();

        if (total == 0) {
            return DashboardStatsDto.builder()
                    .totalAnalyzed(0)
                    .averageEntropy(0.0)
                    .totalBreached(0)
                    .weakCount(0)
                    .moderateCount(0)
                    .strongCount(0)
                    .criticalBreachCount(0)
                    .build();
        }

        Double avgEntropy = auditRecordRepository.findAverageEntropy();
        double calculatedAvg = (avgEntropy != null) ? Math.round(avgEntropy * 100.0) / 100.0 : 0.0;

        long breached = auditRecordRepository.countByIsBreachedTrue();
        long weak = auditRecordRepository.countByHealthCategory("WEAK");
        long moderate = auditRecordRepository.countByHealthCategory("MODERATE");
        long strong = auditRecordRepository.countByHealthCategory("STRONG");
        long critical = auditRecordRepository.countByHealthCategory("CRITICAL_BREACH");

        return DashboardStatsDto.builder()
                .totalAnalyzed(total)
                .averageEntropy(calculatedAvg)
                .totalBreached(breached)
                .weakCount(weak)
                .moderateCount(moderate)
                .strongCount(strong)
                .criticalBreachCount(critical)
                .build();
    }
}