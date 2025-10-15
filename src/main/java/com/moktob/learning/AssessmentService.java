package com.moktob.learning;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentService {
    
    private final AssessmentRepository assessmentRepository;
    
    public List<Assessment> getAllAssessments() {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.findByClientId(clientId);
    }
    
    public Optional<Assessment> getAssessmentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.findByClientIdAndId(clientId, id);
    }
    
    public Assessment saveAssessment(Assessment assessment) {
        Long clientId = TenantContextHolder.getTenantId();
        assessment.setClientId(clientId);
        return assessmentRepository.save(assessment);
    }
    
    public void deleteAssessment(Long id) {
        assessmentRepository.deleteById(id);
    }
    
    public List<Assessment> getAssessmentsByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.findByClientIdAndStudentId(clientId, studentId);
    }
    
    public List<Assessment> getAssessmentsByTeacher(Long teacherId) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.findByClientIdAndTeacherId(clientId, teacherId);
    }
    
    public List<Assessment> getAssessmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return assessmentRepository.findByClientIdAndAssessmentDateBetween(clientId, startDate, endDate);
    }
}
