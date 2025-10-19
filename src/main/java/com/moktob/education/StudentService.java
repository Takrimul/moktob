package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.StudentRequest;
import com.moktob.dto.StudentResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentResponseDTO> getAllStudents() {
        Long clientId = TenantContextHolder.getTenantId();
        log.info("StudentService.getAllStudents() - clientId: {}", clientId);
        if (clientId == null) {
            log.warn("Client ID is null in TenantContextHolder!");
            return List.of(); // Return empty list if no client ID
        }
        List<Student> students = studentRepository.findByClientId(clientId);
        return students.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private StudentResponseDTO convertToDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getDateOfBirth(),
                student.getGuardianName(),
                student.getGuardianContact(),
                student.getAddress(),
                student.getEnrollmentDate(),
                student.getCurrentClassId(),
                student.getPhotoUrl(),
                student.getCurrentClass().getClassName()// className will be populated separately if needed
        );
    }

    public Optional<Student> getStudentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientIdAndId(clientId, id);
    }

    public Student saveStudent(StudentRequest studentRequest) {
        Long clientId = TenantContextHolder.getTenantId();

        Student student = studentRequest.getId() != null
                ? studentRepository.findByClientIdAndId(clientId, studentRequest.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"))
                : new Student();

        student.setName(studentRequest.getName());
        student.setDateOfBirth(studentRequest.getDob());
        student.setGuardianContact(studentRequest.getGuardianContact());
        student.setGuardianName(studentRequest.getGuardianName());
        student.setAddress(studentRequest.getAddress());
        student.setCurrentClassId(studentRequest.getClassId());
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
