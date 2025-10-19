package com.moktob.controller;

import com.moktob.attendance.Attendance;
import com.moktob.attendance.AttendanceService;
import com.moktob.common.AttendanceStatus;
import com.moktob.dto.AttendanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable Long id) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        return attendance.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Attendance> createAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendanceRequest));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Attendance> updateAttendance(@PathVariable Long id, @RequestBody AttendanceRequest attendanceRequest) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendanceRequest));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getAttendanceByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStudent(studentId));
    }
    
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Attendance>> getAttendanceByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByClass(classId));
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Attendance>> getAttendanceByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByTeacher(teacherId));
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Attendance>> getAttendanceByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Attendance>> getAttendanceByStatus(@PathVariable AttendanceStatus status) {
        return ResponseEntity.ok(attendanceService.getAttendanceByStatus(status));
    }
}
