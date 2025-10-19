package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.TeacherRequest;
import com.moktob.dto.TeacherResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    
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

        return teacherRepository.save(entity);
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
