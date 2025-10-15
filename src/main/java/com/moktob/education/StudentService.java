package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    
    public List<Student> getAllStudents() {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientId(clientId);
    }
    
    public Optional<Student> getStudentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientIdAndId(clientId, id);
    }
    
    public Student saveStudent(Student student) {
        Long clientId = TenantContextHolder.getTenantId();
        student.setClientId(clientId);
        return studentRepository.save(student);
    }
    
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    
    public List<Student> getStudentsByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientIdAndCurrentClassId(clientId, classId);
    }
    
    public List<Student> searchStudentsByName(String name) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientIdAndNameContainingIgnoreCase(clientId, name);
    }
}
