package com.moktob.repository;

import com.moktob.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Attendance, Long> {
    
    @Query("""
        SELECT 
            c.id as classId,
            c.className as className,
            t.name as teacherName,
            COUNT(DISTINCT scm.studentId) as totalStudents,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) as absentCount,
            COUNT(CASE WHEN a.status = 'LATE' THEN 1 END) as lateCount,
            COUNT(a.id) as totalAttendanceRecords,
            CONCAT(c.startTime, ' - ', c.endTime) as timeSlot,
            c.daysOfWeek as daysOfWeek
        FROM ClassEntity c
        LEFT JOIN Teacher t ON c.teacherId = t.id
        LEFT JOIN StudentClassMap scm ON c.id = scm.classId
        LEFT JOIN Attendance a ON c.id = a.classId AND a.attendanceDate BETWEEN :startDate AND :endDate
        WHERE c.clientId = :clientId
        GROUP BY c.id, c.className, t.name, c.startTime, c.endTime, c.daysOfWeek
        ORDER BY c.className
        """)
    List<Object[]> getClassAttendanceSummary(@Param("clientId") Long clientId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    @Query("""
        SELECT 
            a.attendanceDate as date,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            COUNT(CASE WHEN a.status = 'ABSENT' THEN 1 END) as absentCount,
            COUNT(CASE WHEN a.status = 'LATE' THEN 1 END) as lateCount,
            COUNT(DISTINCT a.studentId) as totalStudents
        FROM Attendance a
        WHERE a.clientId = :clientId 
        AND a.attendanceDate BETWEEN :startDate AND :endDate
        GROUP BY a.attendanceDate
        ORDER BY a.attendanceDate
        """)
    List<Object[]> getAttendanceTrends(@Param("clientId") Long clientId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
    
    @Query("""
        SELECT 
            s.id as studentId,
            s.name as studentName,
            c.className as className,
            COUNT(a.id) as totalAttendanceRecords,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            AVG(ass.recitationScore) as avgRecitationScore,
            AVG(ass.tajweedScore) as avgTajweedScore,
            AVG(ass.disciplineScore) as avgDisciplineScore,
            COUNT(ass.id) as totalAssessments
        FROM Student s
        LEFT JOIN ClassEntity c ON s.currentClassId = c.id
        LEFT JOIN Attendance a ON s.id = a.studentId AND a.attendanceDate BETWEEN :startDate AND :endDate
        LEFT JOIN Assessment ass ON s.id = ass.studentId AND ass.assessmentDate BETWEEN :startDate AND :endDate
        WHERE s.clientId = :clientId
        GROUP BY s.id, s.name, c.className
        ORDER BY 
            CASE WHEN COUNT(a.id) > 0 THEN COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 1.0 / COUNT(a.id) ELSE 0 END DESC,
            AVG(COALESCE(ass.recitationScore, 0) + COALESCE(ass.tajweedScore, 0) + COALESCE(ass.disciplineScore, 0)) DESC
        """)
    List<Object[]> getTopPerformingStudents(@Param("clientId") Long clientId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
    
    @Query("""
        SELECT 
            t.id as teacherId,
            t.name as teacherName,
            t.qualification as qualification,
            t.isActive as isActive,
            COUNT(DISTINCT c.id) as totalClasses,
            COUNT(DISTINCT scm.studentId) as totalStudents,
            COUNT(a.id) as totalAttendanceRecords,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            COUNT(ass.id) as totalAssessmentsConducted,
            AVG(ass.recitationScore + ass.tajweedScore + ass.disciplineScore) as avgStudentScores
        FROM Teacher t
        LEFT JOIN ClassEntity c ON t.id = c.teacherId
        LEFT JOIN StudentClassMap scm ON c.id = scm.classId
        LEFT JOIN Attendance a ON c.id = a.classId AND a.attendanceDate BETWEEN :startDate AND :endDate
        LEFT JOIN Assessment ass ON t.id = ass.teacherId AND ass.assessmentDate BETWEEN :startDate AND :endDate
        WHERE t.clientId = :clientId
        GROUP BY t.id, t.name, t.qualification, t.isActive
        ORDER BY 
            CASE WHEN COUNT(a.id) > 0 THEN COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) * 1.0 / COUNT(a.id) ELSE 0 END DESC
        """)
    List<Object[]> getTeacherPerformance(@Param("clientId") Long clientId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
    
    @Query("""
        SELECT 
            c.id as classId,
            c.className as className,
            t.name as teacherName,
            CONCAT(c.startTime, ' - ', c.endTime) as timeSlot,
            c.daysOfWeek as daysOfWeek,
            COUNT(DISTINCT scm.studentId) as totalStudents,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            COUNT(a.id) as totalAttendanceRecords
        FROM ClassEntity c
        LEFT JOIN Teacher t ON c.teacherId = t.id
        LEFT JOIN StudentClassMap scm ON c.id = scm.classId
        LEFT JOIN Attendance a ON c.id = a.classId AND a.attendanceDate BETWEEN :startDate AND :endDate
        WHERE c.clientId = :clientId
        GROUP BY c.id, c.className, t.name, c.startTime, c.endTime, c.daysOfWeek
        ORDER BY c.className
        """)
    List<Object[]> getClassWiseStudentSummary(@Param("clientId") Long clientId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
    
    @Query("""
        SELECT 
            s.id as studentId,
            s.name as studentName,
            s.guardianName as guardianName,
            s.guardianContact as guardianContact,
            s.enrollmentDate as enrollmentDate,
            COUNT(a.id) as totalAttendanceRecords,
            COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) as presentCount,
            AVG(ass.recitationScore + ass.tajweedScore + ass.disciplineScore) as averageScore
        FROM Student s
        LEFT JOIN Attendance a ON s.id = a.studentId AND a.attendanceDate BETWEEN :startDate AND :endDate
        LEFT JOIN Assessment ass ON s.id = ass.studentId AND ass.assessmentDate BETWEEN :startDate AND :endDate
        WHERE s.clientId = :clientId AND s.currentClassId = :classId
        GROUP BY s.id, s.name, s.guardianName, s.guardianContact, s.enrollmentDate
        ORDER BY s.name
        """)
    List<Object[]> getStudentsByClass(@Param("clientId") Long clientId,
                                    @Param("classId") Long classId,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
