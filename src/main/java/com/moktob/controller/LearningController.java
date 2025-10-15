package com.moktob.controller;

import com.moktob.learning.Assessment;
import com.moktob.learning.AssessmentService;
import com.moktob.learning.MemorizationRecord;
import com.moktob.learning.MemorizationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
public class LearningController {
    
    private final MemorizationRecordService memorizationRecordService;
    private final AssessmentService assessmentService;
    
    @GetMapping("/memorization")
    public ResponseEntity<List<MemorizationRecord>> getAllMemorizationRecords() {
        return ResponseEntity.ok(memorizationRecordService.getAllRecords());
    }
    
    @GetMapping("/memorization/{id}")
    public ResponseEntity<MemorizationRecord> getMemorizationRecordById(@PathVariable Long id) {
        Optional<MemorizationRecord> record = memorizationRecordService.getRecordById(id);
        return record.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/memorization")
    public ResponseEntity<MemorizationRecord> createMemorizationRecord(@RequestBody MemorizationRecord record) {
        return ResponseEntity.ok(memorizationRecordService.saveRecord(record));
    }
    
    @PutMapping("/memorization/{id}")
    public ResponseEntity<MemorizationRecord> updateMemorizationRecord(@PathVariable Long id, @RequestBody MemorizationRecord record) {
        record.setId(id);
        return ResponseEntity.ok(memorizationRecordService.saveRecord(record));
    }
    
    @DeleteMapping("/memorization/{id}")
    public ResponseEntity<Void> deleteMemorizationRecord(@PathVariable Long id) {
        memorizationRecordService.deleteRecord(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/memorization/student/{studentId}")
    public ResponseEntity<List<MemorizationRecord>> getMemorizationRecordsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(memorizationRecordService.getRecordsByStudent(studentId));
    }
    
    @GetMapping("/memorization/surah/{surahName}")
    public ResponseEntity<List<MemorizationRecord>> getMemorizationRecordsBySurah(@PathVariable String surahName) {
        return ResponseEntity.ok(memorizationRecordService.getRecordsBySurah(surahName));
    }
    
    @GetMapping("/assessments")
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return ResponseEntity.ok(assessmentService.getAllAssessments());
    }
    
    @GetMapping("/assessments/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {
        Optional<Assessment> assessment = assessmentService.getAssessmentById(id);
        return assessment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/assessments")
    public ResponseEntity<Assessment> createAssessment(@RequestBody Assessment assessment) {
        return ResponseEntity.ok(assessmentService.saveAssessment(assessment));
    }
    
    @PutMapping("/assessments/{id}")
    public ResponseEntity<Assessment> updateAssessment(@PathVariable Long id, @RequestBody Assessment assessment) {
        assessment.setId(id);
        return ResponseEntity.ok(assessmentService.saveAssessment(assessment));
    }
    
    @DeleteMapping("/assessments/{id}")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/assessments/student/{studentId}")
    public ResponseEntity<List<Assessment>> getAssessmentsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByStudent(studentId));
    }
    
    @GetMapping("/assessments/teacher/{teacherId}")
    public ResponseEntity<List<Assessment>> getAssessmentsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByTeacher(teacherId));
    }
}
