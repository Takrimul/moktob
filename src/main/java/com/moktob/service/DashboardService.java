package com.moktob.service;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.*;
import com.moktob.education.ClassEntityRepository;
import com.moktob.education.StudentRepository;
import com.moktob.education.TeacherRepository;
import com.moktob.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;

    public DashboardOverviewDTO getDashboardOverview(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        log.debug("Getting dashboard overview for client: {}, date range: {} to {}", clientId, startDate, endDate);

        // Get basic counts
        long totalStudents = studentRepository.countByClientId(clientId);
        long totalTeachers = teacherRepository.countByClientId(clientId);
        long totalClasses = classEntityRepository.countByClientId(clientId);

        // Get attendance summaries
        List<ClassAttendanceSummaryDTO> classSummaries = getClassAttendanceSummaries(startDate, endDate);
        List<AttendanceTrendDTO> attendanceTrends = getAttendanceTrends(startDate, endDate);
        List<StudentPerformanceDTO> topStudents = getTopPerformingStudents(startDate, endDate);
        List<TeacherPerformanceDTO> teacherPerformance = getTeacherPerformance(startDate, endDate);

        // Calculate overall statistics
        long totalAttendanceRecords = classSummaries.stream()
                .mapToLong(ClassAttendanceSummaryDTO::getTotalAttendanceRecords)
                .sum();

        long totalPresent = classSummaries.stream()
                .mapToLong(ClassAttendanceSummaryDTO::getPresentCount)
                .sum();

        double overallAttendanceRate = totalAttendanceRecords > 0 
                ? (double) totalPresent / totalAttendanceRecords * 100 
                : 0.0;

        long activeStudents = studentRepository.countByClientId(clientId);
        long activeTeachers = teacherRepository.countByClientIdAndIsActiveTrue(clientId);
        long activeClasses = classEntityRepository.countByClientId(clientId);

        return new DashboardOverviewDTO(
                totalStudents,
                totalTeachers,
                totalClasses,
                totalAttendanceRecords,
                overallAttendanceRate,
                activeStudents,
                activeTeachers,
                activeClasses,
                classSummaries,
                attendanceTrends,
                topStudents,
                teacherPerformance
        );
    }

    public List<ClassAttendanceSummaryDTO> getClassAttendanceSummaries(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Object[]> results = dashboardRepository.getClassAttendanceSummary(clientId, startDate, endDate);

        return results.stream().map(row -> {
            Long classId = (Long) row[0];
            String className = (String) row[1];
            String teacherName = (String) row[2];
            Long totalStudents = (Long) row[3];
            Long presentCount = (Long) row[4];
            Long absentCount = (Long) row[5];
            Long lateCount = (Long) row[6];
            Long totalAttendanceRecords = (Long) row[7];
            String timeSlot = (String) row[8];
            String daysOfWeek = (String) row[9];

            double attendanceRate = totalAttendanceRecords > 0 
                    ? (double) presentCount / totalAttendanceRecords * 100 
                    : 0.0;

            return new ClassAttendanceSummaryDTO(
                    classId, className, teacherName, totalStudents,
                    presentCount, absentCount, lateCount, attendanceRate,
                    totalAttendanceRecords, timeSlot, daysOfWeek
            );
        }).collect(Collectors.toList());
    }

    public List<AttendanceTrendDTO> getAttendanceTrends(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Object[]> results = dashboardRepository.getAttendanceTrends(clientId, startDate, endDate);

        return results.stream().map(row -> {
            LocalDate date = (LocalDate) row[0];
            Long presentCount = (Long) row[1];
            Long absentCount = (Long) row[2];
            Long lateCount = (Long) row[3];
            Long totalStudents = (Long) row[4];

            double attendanceRate = totalStudents > 0 
                    ? (double) presentCount / totalStudents * 100 
                    : 0.0;

            return new AttendanceTrendDTO(date, presentCount, absentCount, lateCount, attendanceRate, totalStudents);
        }).collect(Collectors.toList());
    }

    public List<StudentPerformanceDTO> getTopPerformingStudents(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Object[]> results = dashboardRepository.getTopPerformingStudents(clientId, startDate, endDate);

        return results.stream().map(row -> {
            Long studentId = (Long) row[0];
            String studentName = (String) row[1];
            String className = (String) row[2];
            Long totalAttendanceRecords = (Long) row[3];
            Long presentCount = (Long) row[4];
            Double avgRecitationScore = (Double) row[5];
            Double avgTajweedScore = (Double) row[6];
            Double avgDisciplineScore = (Double) row[7];
            Long totalAssessments = (Long) row[8];

            double attendanceRate = totalAttendanceRecords > 0 
                    ? (double) presentCount / totalAttendanceRecords * 100 
                    : 0.0;

            double overallScore = 0.0;
            if (avgRecitationScore != null && avgTajweedScore != null && avgDisciplineScore != null) {
                overallScore = (avgRecitationScore + avgTajweedScore + avgDisciplineScore) / 3.0;
            }

            return new StudentPerformanceDTO(
                    studentId, studentName, className, attendanceRate,
                    avgRecitationScore != null ? avgRecitationScore : 0.0,
                    avgTajweedScore != null ? avgTajweedScore : 0.0,
                    avgDisciplineScore != null ? avgDisciplineScore : 0.0,
                    overallScore, totalAssessments, totalAttendanceRecords
            );
        }).collect(Collectors.toList());
    }

    public List<TeacherPerformanceDTO> getTeacherPerformance(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Object[]> results = dashboardRepository.getTeacherPerformance(clientId, startDate, endDate);

        return results.stream().map(row -> {
            Long teacherId = (Long) row[0];
            String teacherName = (String) row[1];
            String qualification = (String) row[2];
            Boolean isActive = (Boolean) row[3];
            Long totalClasses = (Long) row[4];
            Long totalStudents = (Long) row[5];
            Long totalAttendanceRecords = (Long) row[6];
            Long presentCount = (Long) row[7];
            Long totalAssessmentsConducted = (Long) row[8];
            Double avgStudentScores = (Double) row[9];

            double averageClassAttendanceRate = totalAttendanceRecords > 0 
                    ? (double) presentCount / totalAttendanceRecords * 100 
                    : 0.0;

            return new TeacherPerformanceDTO(
                    teacherId, teacherName, totalClasses, totalStudents,
                    averageClassAttendanceRate, totalAssessmentsConducted,
                    avgStudentScores != null ? avgStudentScores : 0.0,
                    qualification, isActive
            );
        }).collect(Collectors.toList());
    }

    public List<ClassWiseStudentDTO> getClassWiseStudents(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        List<Object[]> classSummaries = dashboardRepository.getClassWiseStudentSummary(clientId, startDate, endDate);

        List<ClassWiseStudentDTO> result = new ArrayList<>();

        for (Object[] row : classSummaries) {
            Long classId = (Long) row[0];
            String className = (String) row[1];
            String teacherName = (String) row[2];
            String timeSlot = (String) row[3];
            String daysOfWeek = (String) row[4];
            Long totalStudents = (Long) row[5];
            Long presentCount = (Long) row[6];
            Long totalAttendanceRecords = (Long) row[7];

            double classAttendanceRate = totalAttendanceRecords > 0 
                    ? (double) presentCount / totalAttendanceRecords * 100 
                    : 0.0;

            // Get students for this class
            List<Object[]> studentData = dashboardRepository.getStudentsByClass(clientId, classId, startDate, endDate);
            List<StudentDetailDTO> students = studentData.stream().map(studentRow -> {
                Long studentId = (Long) studentRow[0];
                String studentName = (String) studentRow[1];
                String guardianName = (String) studentRow[2];
                String guardianContact = (String) studentRow[3];
                LocalDate enrollmentDate = (LocalDate) studentRow[4];
                Long studentAttendanceRecords = (Long) studentRow[5];
                Long studentPresentCount = (Long) studentRow[6];
                Double averageScore = (Double) studentRow[7];

                double studentAttendanceRate = studentAttendanceRecords > 0 
                        ? (double) studentPresentCount / studentAttendanceRecords * 100 
                        : 0.0;

                return new StudentDetailDTO(
                        studentId, studentName, guardianName, guardianContact,
                        enrollmentDate, studentAttendanceRate,
                        averageScore != null ? averageScore : 0.0, true
                );
            }).collect(Collectors.toList());

            result.add(new ClassWiseStudentDTO(
                    classId, className, teacherName, totalStudents,
                    students, classAttendanceRate, timeSlot, daysOfWeek
            ));
        }

        return result;
    }

    public AttendanceAnalyticsDTO getAttendanceAnalytics(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        
        List<AttendanceTrendDTO> dailyBreakdown = getAttendanceTrends(startDate, endDate);
        
        long totalAttendanceRecords = dailyBreakdown.stream()
                .mapToLong(AttendanceTrendDTO::getTotalStudents)
                .sum();
        
        long totalPresent = dailyBreakdown.stream()
                .mapToLong(AttendanceTrendDTO::getPresentCount)
                .sum();
        
        long totalAbsent = dailyBreakdown.stream()
                .mapToLong(AttendanceTrendDTO::getAbsentCount)
                .sum();
        
        long totalLate = dailyBreakdown.stream()
                .mapToLong(AttendanceTrendDTO::getLateCount)
                .sum();

        double overallAttendanceRate = totalAttendanceRecords > 0 
                ? (double) totalPresent / totalAttendanceRecords * 100 
                : 0.0;

        double averageDailyAttendance = dailyBreakdown.isEmpty() ? 0.0 
                : dailyBreakdown.stream()
                .mapToDouble(AttendanceTrendDTO::getAttendanceRate)
                .average()
                .orElse(0.0);

        long totalUniqueStudents = studentRepository.countByClientId(clientId);
        long totalUniqueClasses = classEntityRepository.countByClientId(clientId);

        List<DailyAttendanceDTO> dailyBreakdownDTO = dailyBreakdown.stream()
                .map(trend -> new DailyAttendanceDTO(
                        trend.getDate(),
                        trend.getPresentCount(),
                        trend.getAbsentCount(),
                        trend.getLateCount(),
                        trend.getAttendanceRate()
                ))
                .collect(Collectors.toList());

        return new AttendanceAnalyticsDTO(
                startDate, endDate, totalAttendanceRecords,
                totalPresent, totalAbsent, totalLate, overallAttendanceRate,
                averageDailyAttendance, totalUniqueStudents, totalUniqueClasses,
                dailyBreakdownDTO
        );
    }
}
