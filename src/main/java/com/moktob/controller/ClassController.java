package com.moktob.controller;

import com.moktob.dto.ClassRequest;
import com.moktob.dto.ClassResponseDTO;
import com.moktob.dto.ClassDropdownDTO;
import com.moktob.education.ClassEntity;
import com.moktob.education.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {
    
    private final ClassEntityService classEntityService;
    
    @GetMapping
    public ResponseEntity<List<ClassResponseDTO>> getAllClasses() {
        return ResponseEntity.ok(classEntityService.getAllClasses());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClassEntity> getClassById(@PathVariable Long id) {
        Optional<ClassEntity> classEntity = classEntityService.getClassById(id);
        return classEntity.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Create and Update methods use ClassRequest DTO for better request handling
    @PostMapping
    public ResponseEntity<ClassEntity> createClass(@RequestBody ClassRequest classRequest) {
        return ResponseEntity.ok(classEntityService.saveClass(classRequest));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClassEntity> updateClass(@PathVariable Long id, @RequestBody ClassRequest classRequest) {
        return ResponseEntity.ok(classEntityService.saveClass(classRequest));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classEntityService.deleteClass(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<ClassEntity>> getClassesByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(classEntityService.getClassesByTeacher(teacherId));
    }
    
    @GetMapping("/dropdown")
    public ResponseEntity<List<ClassDropdownDTO>> getClassesForDropdown() {
        return ResponseEntity.ok(classEntityService.getClassesForDropdown());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ClassEntity>> searchClassesByName(@RequestParam String className) {
        return ResponseEntity.ok(classEntityService.searchClassesByName(className));
    }
}
