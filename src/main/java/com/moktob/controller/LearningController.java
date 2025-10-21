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
}
