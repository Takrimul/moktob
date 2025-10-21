package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.core.UserAccount;
import com.moktob.dto.TeacherRequest;
import com.moktob.dto.TeacherResponseDTO;
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
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final UserCredentialService userCredentialService;
    
    public List<TeacherResponseDTO> getAllTeachers() {
        Long clientId = TenantContextHolder.getTenantId();
        log.info("TeacherService.getAllTeachers() - clientId: {}", clientId);
        if (clientId == null) {
            log.warn("Client ID is null in TenantContextHolder!");
            return List.of(); // Return empty list if no client ID
        }
        List<Object[]> results = teacherRepository.findTeacherWithDepartmentNamesByClientId(clientId);
        return results.stream()
                .map(this::convertArrayToDTO)
                .collect(Collectors.toList());
    }
    
    private TeacherResponseDTO convertArrayToDTO(Object[] row) {
        return new TeacherResponseDTO(
                (Long) row[0],           // id
                (String) row[1],         // name
                (String) row[2],         // email
                (String) row[3],         // phone
                (LocalDate) row[4],      // dateOfBirth
                (String) row[5],         // address
                (String) row[6],         // qualification
                (String) row[7],         // specialization
                (LocalDate) row[8],      // joiningDate
                (String) row[9],         // photoUrl
                (Boolean) row[10],       // isActive
                (String) row[11]         // departmentName
        );
    }
    
    public Optional<Teacher> getTeacherById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return teacherRepository.findByClientIdAndId(clientId, id);
    }

    @Transactional
    public Teacher saveTeacher(TeacherRequest teacher) {
        Long clientId = TenantContextHolder.getTenantId();

        Teacher entity = teacher.getId() != null
                ? teacherRepository.findByClientIdAndId(clientId, teacher.getId())
                .orElse(new Teacher())
                : new Teacher();

        entity.setName(teacher.getName());
        entity.setEmail(teacher.getEmail());
        entity.setQualification(teacher.getQualification());
        entity.setPhone(teacher.getPhoneNumber());
        entity.setIsActive(teacher.getIsActive());
        entity.setClientId(clientId);

        Teacher savedTeacher = teacherRepository.save(entity);

        // Create user account and send credentials if requested
        if (teacher.getSendCredentials() != null && teacher.getSendCredentials()) {
            try {
                UserAccount userAccount = userCredentialService.createTeacherUser(
                    teacher.getName(), 
                    teacher.getEmail(),
                    teacher.getPhoneNumber(), // Pass phone number
                    true
                );
                log.info("User account created for teacher: {} with username: {}", 
                        teacher.getName(), userAccount.getUsername());
            } catch (Exception e) {
                log.error("Failed to create user account for teacher: {}. Error: {}", 
                         teacher.getName(), e.getMessage(), e);
                // Don't fail the teacher creation if user account creation fails
            }
        }

        return savedTeacher;
    }


    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }
    
    public List<Teacher> getActiveTeachers() {
        Long clientId = TenantContextHolder.getTenantId();
        return teacherRepository.findByClientIdAndIsActiveTrue(clientId);
    }
    
    public List<Teacher> searchTeachersByName(String name) {
        Long clientId = TenantContextHolder.getTenantId();
        return teacherRepository.findByClientIdAndNameContainingIgnoreCase(clientId, name);
    }
}
