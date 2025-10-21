package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.core.UserAccount;
import com.moktob.dto.StudentRequest;
import com.moktob.dto.StudentResponseDTO;
import com.moktob.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserCredentialService userCredentialService;

    public List<StudentResponseDTO> getAllStudents() {
        Long clientId = TenantContextHolder.getTenantId();
        log.info("StudentService.getAllStudents() - clientId: {}", clientId);
        if (clientId == null) {
            log.warn("Client ID is null in TenantContextHolder!");
            return List.of(); // Return empty list if no client ID
        }
        List<Object[]> results = studentRepository.findStudentWithClassNamesByClientId(clientId);
        return results.stream()
                .map(this::convertArrayToDTO)
                .collect(Collectors.toList());
    }
    
    private StudentResponseDTO convertArrayToDTO(Object[] row) {
        return new StudentResponseDTO(
                (Long) row[0],           // id
                (String) row[1],         // name
                (LocalDate) row[2],      // dateOfBirth
                (String) row[3],         // guardianName
                (String) row[4],         // guardianContact
                (String) row[5],         // address
                (LocalDate) row[6],      // enrollmentDate
                (Long) row[7],           // currentClassId
                (String) row[8],         // photoUrl
                (String) row[9]          // className
        );
    }

    public Optional<Student> getStudentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentRepository.findByClientIdAndId(clientId, id);
    }

    @Transactional
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

        Student savedStudent = studentRepository.save(student);

        // Create user account and send credentials if requested
        if (studentRequest.getSendCredentials() != null && studentRequest.getSendCredentials()) {
            try {
                UserAccount userAccount = userCredentialService.createStudentUser(
                    studentRequest.getName(), 
                    studentRequest.getEmail(), 
                    true
                );
                log.info("User account created for student: {} with username: {}", 
                        studentRequest.getName(), userAccount.getUsername());
            } catch (Exception e) {
                log.error("Failed to create user account for student: {}. Error: {}", 
                         studentRequest.getName(), e.getMessage(), e);
                // Don't fail the student creation if user account creation fails
            }
        }

        return savedStudent;
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
