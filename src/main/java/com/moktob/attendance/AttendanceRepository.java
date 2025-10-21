package com.moktob.attendance;

import com.moktob.common.AttendanceStatus;
import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByClientId(Long clientId);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.studentId = :studentId")
    List<Attendance> findByClientIdAndStudentId(@Param("clientId") Long clientId, @Param("studentId") Long studentId);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId")
    List<Attendance> findByClientIdAndClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.teacherId = :teacherId")
    List<Attendance> findByClientIdAndTeacherId(@Param("clientId") Long clientId, @Param("teacherId") Long teacherId);
    
    Optional<Attendance> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.attendanceDate = :date")
    List<Attendance> findByClientIdAndAttendanceDate(@Param("clientId") Long clientId, @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.status = :status")
    List<Attendance> findByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") AttendanceStatus status);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByClientIdAndAttendanceDateBetween(@Param("clientId") Long clientId, 
                                                          @Param("startDate") LocalDate startDate, 
                                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.attendanceDate = :date AND a.classId = :classId")
    List<Attendance> findByClientIdAndAttendanceDateAndClassId(@Param("clientId") Long clientId, 
                                                               @Param("date") LocalDate date, 
                                                               @Param("classId") Long classId);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId AND a.attendanceDate = :date")
    List<Attendance> findByClientIdAndClassIdAndAttendanceDate(@Param("clientId") Long clientId, 
                                                              @Param("classId") Long classId, 
                                                              @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findByClientIdAndClassIdAndAttendanceDateBetween(@Param("clientId") Long clientId, 
                                                                     @Param("classId") Long classId, 
                                                                     @Param("startDate") LocalDate startDate, 
                                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.clientId = :clientId AND a.studentId = :studentId AND a.status = :status AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Long countByClientIdAndStudentIdAndStatusAndAttendanceDateBetween(@Param("clientId") Long clientId, 
                                                                     @Param("studentId") Long studentId, 
                                                                     @Param("status") AttendanceStatus status, 
                                                                     @Param("startDate") LocalDate startDate, 
                                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId AND a.attendanceDate = :date AND a.studentId = :studentId")
    Optional<Attendance> findByClientIdAndClassIdAndAttendanceDateAndStudentId(@Param("clientId") Long clientId, 
                                                                               @Param("classId") Long classId, 
                                                                               @Param("date") LocalDate date, 
                                                                               @Param("studentId") Long studentId);
    
    // Statistics queries
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Long countTotalAttendanceByClassAndDateRange(@Param("clientId") Long clientId, 
                                                 @Param("classId") Long classId, 
                                                 @Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.clientId = :clientId AND a.classId = :classId AND a.status = :status AND a.attendanceDate BETWEEN :startDate AND :endDate")
    Long countAttendanceByClassAndStatusAndDateRange(@Param("clientId") Long clientId, 
                                                     @Param("classId") Long classId, 
                                                     @Param("status") AttendanceStatus status, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.studentId = :studentId AND a.classId = :classId AND a.attendanceDate = :date")
    Optional<Attendance> findByClientIdAndStudentIdAndClassIdAndAttendanceDate(@Param("clientId") Long clientId, 
                                                                              @Param("studentId") Long studentId, 
                                                                              @Param("classId") Long classId, 
                                                                              @Param("date") LocalDate date);
    
    @Query("SELECT a FROM Attendance a WHERE a.clientId = :clientId AND a.studentId = :studentId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findByClientIdAndStudentIdAndAttendanceDateBetween(@Param("clientId") Long clientId, 
                                                                       @Param("studentId") Long studentId, 
                                                                       @Param("startDate") LocalDate startDate, 
                                                                       @Param("endDate") LocalDate endDate);
}
