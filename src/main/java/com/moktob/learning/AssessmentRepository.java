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
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.classId = :classId")
    List<Assessment> findByClientIdAndClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
    
    Optional<Assessment> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByClientIdAndAssessmentDateBetween(@Param("clientId") Long clientId, 
                                                           @Param("startDate") LocalDate startDate, 
                                                           @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.studentId = :studentId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByClientIdAndStudentIdAndAssessmentDateBetween(@Param("clientId") Long clientId, 
                                                                       @Param("studentId") Long studentId,
                                                                       @Param("startDate") LocalDate startDate, 
                                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.classId = :classId AND a.assessmentDate BETWEEN :startDate AND :endDate")
    List<Assessment> findByClientIdAndClassIdAndAssessmentDateBetween(@Param("clientId") Long clientId, 
                                                                     @Param("classId") Long classId,
                                                                     @Param("startDate") LocalDate startDate, 
                                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.assessmentType = :assessmentType")
    List<Assessment> findByClientIdAndAssessmentType(@Param("clientId") Long clientId, @Param("assessmentType") String assessmentType);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.studentId = :studentId AND a.assessmentType = :assessmentType")
    List<Assessment> findByClientIdAndStudentIdAndAssessmentType(@Param("clientId") Long clientId, 
                                                               @Param("studentId") Long studentId, 
                                                               @Param("assessmentType") String assessmentType);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.isCompleted = :isCompleted")
    List<Assessment> findByClientIdAndIsCompleted(@Param("clientId") Long clientId, @Param("isCompleted") Boolean isCompleted);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.parentNotified = :parentNotified")
    List<Assessment> findByClientIdAndParentNotified(@Param("clientId") Long clientId, @Param("parentNotified") Boolean parentNotified);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.surahName = :surahName")
    List<Assessment> findByClientIdAndSurahName(@Param("clientId") Long clientId, @Param("surahName") String surahName);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.grade = :grade")
    List<Assessment> findByClientIdAndGrade(@Param("clientId") Long clientId, @Param("grade") String grade);
    
    @Query("SELECT a FROM Assessment a WHERE a.clientId = :clientId AND a.overallScore >= :minScore AND a.overallScore <= :maxScore")
    List<Assessment> findByClientIdAndOverallScoreBetween(@Param("clientId") Long clientId, 
                                                         @Param("minScore") Double minScore, 
                                                         @Param("maxScore") Double maxScore);
    
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.clientId = :clientId AND a.studentId = :studentId")
    Long countByClientIdAndStudentId(@Param("clientId") Long clientId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(a) FROM Assessment a WHERE a.clientId = :clientId AND a.classId = :classId")
    Long countByClientIdAndClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
    
    @Query("SELECT AVG(a.overallScore) FROM Assessment a WHERE a.clientId = :clientId AND a.studentId = :studentId")
    Double getAverageScoreByClientIdAndStudentId(@Param("clientId") Long clientId, @Param("studentId") Long studentId);
    
    @Query("SELECT AVG(a.overallScore) FROM Assessment a WHERE a.clientId = :clientId AND a.classId = :classId")
    Double getAverageScoreByClientIdAndClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
}
