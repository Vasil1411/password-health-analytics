package com.analytics.passwordhealth.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entropy_score", nullable = false)
    private Double entropyScore;

    @Column(name = "password_length", nullable = false)
    private Integer passwordLength;

    @Column(name = "is_breached", nullable = false)
    private Boolean isBreached;

    @Column(name = "breach_count", nullable = false)
    private Long breachCount;

    @Column(name = "has_patterns", nullable = false)
    private Boolean hasPatterns;

    @Column(name = "health_category", nullable = false)
    private String healthCategory;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}