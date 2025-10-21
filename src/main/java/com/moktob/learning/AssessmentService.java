package com.moktob.learning;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.AssessmentRequest;
import com.moktob.dto.AssessmentResponseDTO;
import com.moktob.dto.AssessmentSummaryDTO;
import com.moktob.dto.AssessmentAnalyticsDTO;
import com.moktob.service.AssessmentBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    private final AssessmentBusinessService assessmentBusinessService;
    
    public List<AssessmentResponseDTO> getAllAssessments() {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdWithRelationships(clientId);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public Optional<AssessmentResponseDTO> getAssessmentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Assessment> assessment = assessmentRepository.findByClientIdAndIdWithRelationships(clientId, id);
        return assessment.map(assessmentBusinessService::convertToResponseDTO);
    }
    
    @Transactional
    public AssessmentResponseDTO createAssessment(AssessmentRequest request) {
        log.info("Creating assessment for student: {}, teacher: {}", request.getStudentId(), request.getTeacherId());
        return assessmentBusinessService.processAssessmentCreation(request);
    }
    
    @Transactional
    public AssessmentResponseDTO updateAssessment(Long id, AssessmentRequest request) {
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Assessment> existingAssessment = assessmentRepository.findByClientIdAndId(clientId, id);
        
        if (existingAssessment.isEmpty()) {
            throw new IllegalArgumentException("Assessment not found");
        }
        
        Assessment assessment = existingAssessment.get();
        
        // Update fields
        assessment.setAssessmentType(request.getAssessmentType());
        assessment.setAssessmentDate(request.getAssessmentDate());
        assessment.setAssessmentTime(request.getAssessmentTime());
        
        // Update scores
        assessment.setRecitationScore(request.getRecitationScore());
        assessment.setTajweedScore(request.getTajweedScore());
        assessment.setMemorizationScore(request.getMemorizationScore());
        assessment.setComprehensionScore(request.getComprehensionScore());
        assessment.setDisciplineScore(request.getDisciplineScore());
        
        // Update content fields
        assessment.setSurahName(request.getSurahName());
        assessment.setStartAyah(request.getStartAyah());
        assessment.setEndAyah(request.getEndAyah());
        assessment.setVersesAssessed(request.getVersesAssessed());
        assessment.setMistakesCount(request.getMistakesCount());
        assessment.setCorrectionsGiven(request.getCorrectionsGiven());
        
        // Update feedback fields
        assessment.setTeacherFeedback(request.getTeacherFeedback());
        assessment.setStudentStrengths(request.getStudentStrengths());
        assessment.setAreasForImprovement(request.getAreasForImprovement());
        assessment.setHomeworkAssigned(request.getHomeworkAssigned());
        assessment.setNextAssessmentDate(request.getNextAssessmentDate());
        
        // Update status fields
        assessment.setIsCompleted(request.getIsCompleted());
        assessment.setIsReassessment(request.getIsReassessment());
        assessment.setParentNotified(request.getParentNotified());
        assessment.setAssessmentDurationMinutes(request.getAssessmentDurationMinutes());
        
        // Recalculate overall score and grade
        assessment.calculateOverallScore();
        
        Assessment savedAssessment = assessmentRepository.save(assessment);
        log.info("Updated assessment: {}", savedAssessment.getId());
        
        return assessmentBusinessService.convertToResponseDTO(savedAssessment);
    }
    
    @Transactional
    public void deleteAssessment(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Assessment> assessment = assessmentRepository.findByClientIdAndId(clientId, id);
        
        if (assessment.isEmpty()) {
            throw new IllegalArgumentException("Assessment not found");
        }
        
        assessmentRepository.deleteById(id);
        log.info("Deleted assessment: {}", id);
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndStudentIdWithRelationships(clientId, studentId);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByTeacher(Long teacherId) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndTeacherIdWithRelationships(clientId, teacherId);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndClassId(clientId, classId);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndAssessmentDateBetween(clientId, startDate, endDate);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByType(String assessmentType) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndAssessmentType(clientId, assessmentType);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByGrade(String grade) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndGrade(clientId, grade);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsByScoreRange(Double minScore, Double maxScore) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndOverallScoreBetween(clientId, minScore, maxScore);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getPendingAssessments() {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndIsCompleted(clientId, false);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public List<AssessmentResponseDTO> getAssessmentsNeedingParentNotification() {
        Long clientId = TenantContextHolder.getTenantId();
        List<Assessment> assessments = assessmentRepository.findByClientIdAndParentNotified(clientId, false);
        return assessments.stream()
            .map(assessmentBusinessService::convertToResponseDTO)
            .collect(Collectors.toList());
    }
    
    public AssessmentSummaryDTO getStudentAssessmentSummary(Long studentId, LocalDate fromDate, LocalDate toDate) {
        return assessmentBusinessService.generateStudentAssessmentSummary(studentId, fromDate, toDate);
    }
    
    public AssessmentAnalyticsDTO getClassAssessmentAnalytics(Long classId, LocalDate fromDate, LocalDate toDate) {
        return assessmentBusinessService.generateClassAssessmentAnalytics(classId, fromDate, toDate);
    }
    
    public Long getAssessmentCountByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.countByClientIdAndStudentId(clientId, studentId);
    }
    
    public Long getAssessmentCountByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.countByClientIdAndClassId(clientId, classId);
    }
    
    public Double getAverageScoreByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.getAverageScoreByClientIdAndStudentId(clientId, studentId);
    }
    
    public Double getAverageScoreByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.getAverageScoreByClientIdAndClassId(clientId, classId);
    }
}
