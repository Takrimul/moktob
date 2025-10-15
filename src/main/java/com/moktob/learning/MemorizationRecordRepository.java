package com.moktob.learning;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemorizationRecordRepository extends JpaRepository<MemorizationRecord, Long> {
    List<MemorizationRecord> findByClientId(Long clientId);
    
    @Query("SELECT mr FROM MemorizationRecord mr WHERE mr.clientId = :clientId AND mr.studentId = :studentId")
    List<MemorizationRecord> findByClientIdAndStudentId(@Param("clientId") Long clientId, @Param("studentId") Long studentId);
    
    Optional<MemorizationRecord> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT mr FROM MemorizationRecord mr WHERE mr.clientId = :clientId AND mr.surahName = :surahName")
    List<MemorizationRecord> findByClientIdAndSurahName(@Param("clientId") Long clientId, @Param("surahName") String surahName);
}
