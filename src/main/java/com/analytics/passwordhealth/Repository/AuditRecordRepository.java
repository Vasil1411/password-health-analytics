package com.analytics.passwordhealth.Repository;


import com.analytics.passwordhealth.Entity.AuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {
    // Средна ентропия на всички анализирани пароли
    @Query("SELECT COALESCE(AVG(a.entropyScore), 0.0) FROM AuditRecord a")
    Double findAverageEntropy();

    // Брой пароли, които са били засечени в течове
    long countByIsBreachedTrue();

    // Брой пароли по определена категория (WEAK, MODERATE, STRONG, CRITICAL_BREACH)
    long countByHealthCategory(String healthCategory);
}