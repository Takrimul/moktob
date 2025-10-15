package com.moktob.learning;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByClientId(Long clientId);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.studentId = :studentId")
    List<Assessment> findByClientIdAndStudentId(@Param("clientId") Long clientId, @Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.teacherId = :teacherId")
    List<Assessment> findByClientIdAndTeacherId(@Param("clientId") Long clientId, @Param("teacherId") Long teacherId);
    
    Optional<Assessment> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByClientIdAndAssessmentDateBetween(@Param("clientId") Long clientId, 
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
}
