package com.moktob.service;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.*;
import com.moktob.education.ClassEntity;
import com.moktob.education.Student;
import com.moktob.education.Teacher;
import com.moktob.education.ClassEntityService;
import com.moktob.education.StudentService;
import com.moktob.education.TeacherService;
import com.moktob.learning.Assessment;
import com.moktob.learning.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssessmentBusinessService {

    private final AssessmentRepository assessmentRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassEntityService classEntityService;

    /**
     * Validate assessment request
     */
    public void validateAssessmentRequest(AssessmentRequest request) {
        if (request.getStudentId() == null) {
            throw new IllegalArgumentException("Student ID is required");
        }
        
        if (request.getAssessmentDate() == null) {
            throw new IllegalArgumentException("Assessment date is required");
        }
        
        if (request.getAssessmentDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create assessment for future dates");
        }
        
        // Allow assessments for current academic year (approximately 2 years back)
        if (request.getAssessmentDate().isBefore(LocalDate.now().minusDays(730))) {
            throw new IllegalArgumentException("Cannot create assessment for dates older than 2 years");
        }
        
        // Validate scores are within range
        validateScore(request.getRecitationScore(), "Recitation score");
        validateScore(request.getTajweedScore(), "Tajweed score");
        validateScore(request.getMemorizationScore(), "Memorization score");
        validateScore(request.getComprehensionScore(), "Comprehension score");
        validateScore(request.getDisciplineScore(), "Discipline score");
        
        // Validate student exists and belongs to client
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Student> student = studentService.getStudentById(request.getStudentId());
        if (student.isEmpty() || !student.get().getClientId().equals(clientId)) {
            throw new IllegalArgumentException("Student not found or does not belong to your organization");
        }
    }

    /**
     * Process assessment creation with business logic
     */
    @Transactional
    public AssessmentResponseDTO processAssessmentCreation(AssessmentRequest request) {
        validateAssessmentRequest(request);
        
        // Get teacher ID from context if not provided
        Long teacherId = request.getTeacherId() != null ? request.getTeacherId() : TenantContextHolder.getTeacherId();
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID is required for assessment");
        }
        
        // Validate teacher exists and belongs to client
        Long clientId = TenantContextHolder.getTenantId();
        Optional<Teacher> teacher = teacherService.getTeacherById(teacherId);
        if (teacher.isEmpty() || !teacher.get().getClientId().equals(clientId)) {
            // If teacher not found in Teacher table, check if it's a user ID fallback
            Long userId = TenantContextHolder.getUserId();
            if (teacherId.equals(userId)) {
                log.warn("Using user ID {} as teacher ID for assessment creation", userId);
                // Allow this case - user ID is being used as teacher ID
            } else {
                throw new IllegalArgumentException("Teacher not found or does not belong to your organization");
            }
        }
        
        // Create assessment entity
        Assessment assessment = new Assessment();
        assessment.setStudentId(request.getStudentId());
        assessment.setTeacherId(teacherId);
        assessment.setClassId(request.getClassId());
        assessment.setAssessmentType(request.getAssessmentType());
        assessment.setAssessmentDate(request.getAssessmentDate());
        assessment.setAssessmentTime(request.getAssessmentTime() != null ? request.getAssessmentTime() : java.time.LocalDateTime.now());
        
        // Set scores
        assessment.setRecitationScore(request.getRecitationScore());
        assessment.setTajweedScore(request.getTajweedScore());
        assessment.setMemorizationScore(request.getMemorizationScore());
        assessment.setComprehensionScore(request.getComprehensionScore());
        assessment.setDisciplineScore(request.getDisciplineScore());
        
        // Set content fields
        assessment.setSurahName(request.getSurahName());
        assessment.setStartAyah(request.getStartAyah());
        assessment.setEndAyah(request.getEndAyah());
        assessment.setVersesAssessed(request.getVersesAssessed());
        assessment.setMistakesCount(request.getMistakesCount());
        assessment.setCorrectionsGiven(request.getCorrectionsGiven());
        
        // Set feedback fields
        assessment.setTeacherFeedback(request.getTeacherFeedback());
        assessment.setStudentStrengths(request.getStudentStrengths());
        assessment.setAreasForImprovement(request.getAreasForImprovement());
        assessment.setHomeworkAssigned(request.getHomeworkAssigned());
        assessment.setNextAssessmentDate(request.getNextAssessmentDate());
        
        // Set status fields
        assessment.setIsCompleted(request.getIsCompleted() != null ? request.getIsCompleted() : true);
        assessment.setIsReassessment(request.getIsReassessment() != null ? request.getIsReassessment() : false);
        assessment.setParentNotified(request.getParentNotified() != null ? request.getParentNotified() : false);
        assessment.setAssessmentDurationMinutes(request.getAssessmentDurationMinutes());
        
        // Calculate overall score and grade
        assessment.calculateOverallScore();
        
        // Save assessment
        Assessment savedAssessment = assessmentRepository.save(assessment);
        
        // Convert to response DTO
        return convertToResponseDTO(savedAssessment);
    }

    /**
     * Generate assessment summary for a student
     */
    public AssessmentSummaryDTO generateStudentAssessmentSummary(Long studentId, LocalDate fromDate, LocalDate toDate) {
        Long clientId = TenantContextHolder.getTenantId();
        
        // Get student information
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        Student student = studentOpt.get();
        
        // Get assessments in date range
        List<Assessment> assessments = assessmentRepository.findByClientIdAndStudentIdAndAssessmentDateBetween(
            clientId, studentId, fromDate, toDate);
        
        if (assessments.isEmpty()) {
            return AssessmentSummaryDTO.builder()
                .studentId(studentId)
                .studentName(student.getName())
                .fromDate(fromDate)
                .toDate(toDate)
                .totalAssessments(0)
                .averageScore(0.0)
                .averageGrade("N/A")
                .assessmentsCompleted(0)
                .assessmentsPending(0)
                .build();
        }
        
        // Calculate statistics
        int totalAssessments = assessments.size();
        int completedAssessments = (int) assessments.stream().filter(a -> Boolean.TRUE.equals(a.getIsCompleted())).count();
        int pendingAssessments = totalAssessments - completedAssessments;
        
        double averageScore = assessments.stream()
            .filter(a -> a.getOverallScore() != null)
            .mapToDouble(Assessment::getOverallScore)
            .average()
            .orElse(0.0);
        
        // Calculate average scores by type
        double avgRecitation = calculateAverageScore(assessments, Assessment::getRecitationScore);
        double avgTajweed = calculateAverageScore(assessments, Assessment::getTajweedScore);
        double avgMemorization = calculateAverageScore(assessments, Assessment::getMemorizationScore);
        double avgComprehension = calculateAverageScore(assessments, Assessment::getComprehensionScore);
        double avgDiscipline = calculateAverageScore(assessments, Assessment::getDisciplineScore);
        
        // Determine progress trend
        String progress = determineProgressTrend(assessments);
        
        // Get recent assessments
        List<AssessmentResponseDTO> recentAssessments = assessments.stream()
            .sorted((a, b) -> b.getAssessmentDate().compareTo(a.getAssessmentDate()))
            .limit(5)
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        
        return AssessmentSummaryDTO.builder()
            .studentId(studentId)
            .studentName(student.getName())
            .classId(student.getCurrentClassId())
            .fromDate(fromDate)
            .toDate(toDate)
            .totalAssessments(totalAssessments)
            .averageScore(averageScore)
            .averageGrade(calculateGrade(averageScore))
            .assessmentsCompleted(completedAssessments)
            .assessmentsPending(pendingAssessments)
            .averageRecitationScore(avgRecitation)
            .averageTajweedScore(avgTajweed)
            .averageMemorizationScore(avgMemorization)
            .averageComprehensionScore(avgComprehension)
            .averageDisciplineScore(avgDiscipline)
            .overallProgress(progress)
            .recentAssessments(recentAssessments)
            .build();
    }

    /**
     * Generate class assessment analytics
     */
    public AssessmentAnalyticsDTO generateClassAssessmentAnalytics(Long classId, LocalDate fromDate, LocalDate toDate) {
        Long clientId = TenantContextHolder.getTenantId();
        
        // Get class information
        Optional<ClassEntity> classOpt = classEntityService.getClassById(classId);
        if (classOpt.isEmpty()) {
            throw new IllegalArgumentException("Class not found");
        }
        ClassEntity classEntity = classOpt.get();
        
        // Get all students in class
        List<Student> students = studentService.getStudentsByClass(classId);
        
        // Get all assessments for students in this class
        List<Assessment> assessments = assessmentRepository.findByClientIdAndClassIdAndAssessmentDateBetween(
            clientId, classId, fromDate, toDate);
        
        // Calculate class statistics
        int totalAssessments = assessments.size();
        int studentsAssessed = (int) assessments.stream()
            .map(Assessment::getStudentId)
            .distinct()
            .count();
        
        double classAverageScore = assessments.stream()
            .filter(a -> a.getOverallScore() != null)
            .mapToDouble(Assessment::getOverallScore)
            .average()
            .orElse(0.0);
        
        // Grade distribution
        Map<String, Integer> gradeDistribution = assessments.stream()
            .filter(a -> a.getGrade() != null)
            .collect(Collectors.groupingBy(
                Assessment::getGrade,
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        // Average scores by type
        Map<String, Double> averageScoresByType = new HashMap<>();
        averageScoresByType.put("RECITATION", calculateAverageScore(assessments, Assessment::getRecitationScore));
        averageScoresByType.put("TAJWEED", calculateAverageScore(assessments, Assessment::getTajweedScore));
        averageScoresByType.put("MEMORIZATION", calculateAverageScore(assessments, Assessment::getMemorizationScore));
        averageScoresByType.put("COMPREHENSION", calculateAverageScore(assessments, Assessment::getComprehensionScore));
        averageScoresByType.put("DISCIPLINE", calculateAverageScore(assessments, Assessment::getDisciplineScore));
        
        // Top performers and students needing attention
        List<StudentPerformanceDTO> topPerformers = getTopPerformers(assessments, students);
        List<StudentPerformanceDTO> studentsNeedingAttention = getStudentsNeedingAttention(assessments, students);
        
        return AssessmentAnalyticsDTO.builder()
            .classId(classId)
            .className(classEntity.getClassName())
            .fromDate(fromDate)
            .toDate(toDate)
            .totalAssessments(totalAssessments)
            .classAverageScore(classAverageScore)
            .classAverageGrade(calculateGrade(classAverageScore))
            .studentsAssessed(studentsAssessed)
            .totalStudents(students.size())
            .gradeDistribution(gradeDistribution)
            .averageScoresByType(averageScoresByType)
            .topPerformers(topPerformers)
            .studentsNeedingAttention(studentsNeedingAttention)
            .completionRate(calculateCompletionRate(assessments))
            .assessmentsCompleted((int) assessments.stream().filter(a -> Boolean.TRUE.equals(a.getIsCompleted())).count())
            .assessmentsPending((int) assessments.stream().filter(a -> Boolean.FALSE.equals(a.getIsCompleted())).count())
            .build();
    }

    // Helper methods
    private void validateScore(Integer score, String fieldName) {
        if (score != null && (score < 0 || score > 100)) {
            throw new IllegalArgumentException(fieldName + " must be between 0 and 100");
        }
    }

    public AssessmentResponseDTO convertToResponseDTO(Assessment assessment) {
        try {
            return AssessmentResponseDTO.builder()
                .id(assessment.getId())
                .studentId(assessment.getStudentId())
                .studentName(getStudentName(assessment))
                .teacherId(assessment.getTeacherId())
                .teacherName(getTeacherName(assessment))
                .classId(assessment.getClassId())
                .className(getClassName(assessment))
                .assessmentType(assessment.getAssessmentType())
                .assessmentDate(assessment.getAssessmentDate())
                .assessmentTime(assessment.getAssessmentTime())
                .recitationScore(assessment.getRecitationScore())
                .tajweedScore(assessment.getTajweedScore())
                .memorizationScore(assessment.getMemorizationScore())
                .comprehensionScore(assessment.getComprehensionScore())
                .disciplineScore(assessment.getDisciplineScore())
                .overallScore(assessment.getOverallScore())
                .grade(assessment.getGrade())
                .surahName(assessment.getSurahName())
                .startAyah(assessment.getStartAyah())
                .endAyah(assessment.getEndAyah())
                .versesAssessed(assessment.getVersesAssessed())
                .mistakesCount(assessment.getMistakesCount())
                .correctionsGiven(assessment.getCorrectionsGiven())
                .teacherFeedback(assessment.getTeacherFeedback())
                .studentStrengths(assessment.getStudentStrengths())
                .areasForImprovement(assessment.getAreasForImprovement())
                .homeworkAssigned(assessment.getHomeworkAssigned())
                .nextAssessmentDate(assessment.getNextAssessmentDate())
                .isCompleted(assessment.getIsCompleted())
                .isReassessment(assessment.getIsReassessment())
                .parentNotified(assessment.getParentNotified())
                .assessmentDurationMinutes(assessment.getAssessmentDurationMinutes())
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt())
                .build();
        } catch (Exception e) {
            log.error("Error converting assessment to DTO: {}", e.getMessage(), e);
            // Return a basic DTO with just the IDs if conversion fails
            return AssessmentResponseDTO.builder()
                .id(assessment.getId())
                .studentId(assessment.getStudentId())
                .studentName("Unknown Student")
                .teacherId(assessment.getTeacherId())
                .teacherName("Unknown Teacher")
                .classId(assessment.getClassId())
                .className("Unknown Class")
                .assessmentType(assessment.getAssessmentType())
                .assessmentDate(assessment.getAssessmentDate())
                .build();
        }
    }
    
    private String getStudentName(Assessment assessment) {
        try {
            return assessment.getStudent() != null ? assessment.getStudent().getName() : "Unknown Student";
        } catch (Exception e) {
            log.debug("Could not get student name for assessment {}: {}", assessment.getId(), e.getMessage());
            return "Unknown Student";
        }
    }
    
    private String getTeacherName(Assessment assessment) {
        try {
            return assessment.getTeacher() != null ? assessment.getTeacher().getName() : "Unknown Teacher";
        } catch (Exception e) {
            log.debug("Could not get teacher name for assessment {}: {}", assessment.getId(), e.getMessage());
            return "Unknown Teacher";
        }
    }
    
    private String getClassName(Assessment assessment) {
        try {
            return assessment.getClassEntity() != null ? assessment.getClassEntity().getClassName() : "Unknown Class";
        } catch (Exception e) {
            log.debug("Could not get class name for assessment {}: {}", assessment.getId(), e.getMessage());
            return "Unknown Class";
        }
    }

    private double calculateAverageScore(List<Assessment> assessments, java.util.function.Function<Assessment, Integer> scoreExtractor) {
        return assessments.stream()
            .map(scoreExtractor)
            .filter(Objects::nonNull)
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    private String calculateGrade(double score) {
        if (score >= 95) return "A+";
        if (score >= 90) return "A";
        if (score >= 85) return "B+";
        if (score >= 80) return "B";
        if (score >= 75) return "C+";
        if (score >= 70) return "C";
        if (score >= 60) return "D";
        return "F";
    }

    private String determineProgressTrend(List<Assessment> assessments) {
        if (assessments.size() < 2) return "STABLE";
        
        List<Assessment> sortedAssessments = assessments.stream()
            .sorted((a, b) -> a.getAssessmentDate().compareTo(b.getAssessmentDate()))
            .collect(Collectors.toList());
        
        double firstHalf = sortedAssessments.subList(0, sortedAssessments.size() / 2).stream()
            .filter(a -> a.getOverallScore() != null)
            .mapToDouble(Assessment::getOverallScore)
            .average()
            .orElse(0.0);
        
        double secondHalf = sortedAssessments.subList(sortedAssessments.size() / 2, sortedAssessments.size()).stream()
            .filter(a -> a.getOverallScore() != null)
            .mapToDouble(Assessment::getOverallScore)
            .average()
            .orElse(0.0);
        
        double difference = secondHalf - firstHalf;
        if (difference > 5) return "IMPROVING";
        if (difference < -5) return "DECLINING";
        return "STABLE";
    }

    private List<StudentPerformanceDTO> getTopPerformers(List<Assessment> assessments, List<Student> students) {
        Map<Long, Double> studentAverages = assessments.stream()
            .filter(a -> a.getOverallScore() != null)
            .collect(Collectors.groupingBy(
                Assessment::getStudentId,
                Collectors.averagingDouble(Assessment::getOverallScore)
            ));
        
        return studentAverages.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(5)
            .map(entry -> {
                Student student = students.stream()
                    .filter(s -> s.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);
                return StudentPerformanceDTO.builder()
                    .studentId(entry.getKey())
                    .studentName(student != null ? student.getName() : "Unknown")
                    .overallScore(entry.getValue())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<StudentPerformanceDTO> getStudentsNeedingAttention(List<Assessment> assessments, List<Student> students) {
        Map<Long, Double> studentAverages = assessments.stream()
            .filter(a -> a.getOverallScore() != null)
            .collect(Collectors.groupingBy(
                Assessment::getStudentId,
                Collectors.averagingDouble(Assessment::getOverallScore)
            ));
        
        return studentAverages.entrySet().stream()
            .filter(entry -> entry.getValue() < 70) // Below C grade
            .sorted(Map.Entry.<Long, Double>comparingByValue())
            .limit(5)
            .map(entry -> {
                Student student = students.stream()
                    .filter(s -> s.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);
                return StudentPerformanceDTO.builder()
                    .studentId(entry.getKey())
                    .studentName(student != null ? student.getName() : "Unknown")
                    .overallScore(entry.getValue())
                    .build();
            })
            .collect(Collectors.toList());
    }

    private double calculateCompletionRate(List<Assessment> assessments) {
        if (assessments.isEmpty()) return 0.0;
        long completed = assessments.stream().filter(a -> Boolean.TRUE.equals(a.getIsCompleted())).count();
        return (double) completed / assessments.size() * 100;
    }
}
