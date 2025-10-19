package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.ClassRequest;
import com.moktob.dto.ClassResponseDTO;
import com.moktob.dto.ClassDropdownDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassEntityService {
    
    private final ClassEntityRepository classEntityRepository;
    
    public List<ClassResponseDTO> getAllClasses() {
        Long clientId = TenantContextHolder.getTenantId();
        log.info("ClassEntityService.getAllClasses() - clientId: {}", clientId);
        if (clientId == null) {
            log.warn("Client ID is null in TenantContextHolder!");
            return List.of(); // Return empty list if no client ID
        }
        List<Object[]> results = classEntityRepository.findClassWithTeacherNamesAndStudentCountsByClientId(clientId);
        return results.stream()
                .map(this::convertArrayToDTO)
                .collect(Collectors.toList());
    }
    
    private ClassResponseDTO convertArrayToDTO(Object[] row) {
        return new ClassResponseDTO(
                (Long) row[0],           // id
                (String) row[1],         // className
                (Long) row[2],           // teacherId
                (String) row[3],         // teacherName
                (LocalTime) row[4],      // startTime
                (LocalTime) row[5],      // endTime
                (String) row[6],         // daysOfWeek
                ((Number) row[7]).longValue() // studentCount
        );
    }
    
    public Optional<ClassEntity> getClassById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return classEntityRepository.findByClientIdAndId(clientId, id);
    }

    public ClassEntity saveClass(ClassRequest classRequest) {
        Long clientId = TenantContextHolder.getTenantId();
        ClassEntity classEntity;

        if (classRequest.getId() != null) {
            // Existing entity
            classEntity = classEntityRepository.findById(classRequest.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + classRequest.getId()));
        } else {
            // New entity
            classEntity = new ClassEntity();
        }
        classEntity.setClassName(classRequest.getClassName());
        classEntity.setClientId(clientId);
        classEntity.setTeacherId(classRequest.getTeacherId());
        classEntity.setStartTime(classRequest.getStartTime());
        classEntity.setEndTime(classRequest.getEndTime());
        classEntity.setDaysOfWeek(classRequest.getDaysOfWeek());

        return classEntityRepository.save(classEntity);
    }


    public void deleteClass(Long id) {
        classEntityRepository.deleteById(id);
    }
    
    public List<ClassEntity> getClassesByTeacher(Long teacherId) {
        Long clientId = TenantContextHolder.getTenantId();
        return classEntityRepository.findByClientIdAndTeacherId(clientId, teacherId);
    }
    
    public List<ClassEntity> searchClassesByName(String className) {
        Long clientId = TenantContextHolder.getTenantId();
        return classEntityRepository.findByClientIdAndClassNameContainingIgnoreCase(clientId, className);
    }
    
    public List<ClassDropdownDTO> getClassesForDropdown() {
        Long clientId = TenantContextHolder.getTenantId();
        log.info("ClassEntityService.getClassesForDropdown() - clientId: {}", clientId);
        if (clientId == null) {
            log.warn("Client ID is null in TenantContextHolder!");
            return List.of();
        }
        List<Object[]> results = classEntityRepository.findClassWithTeacherNamesAndStudentCountsByClientId(clientId);
        return results.stream()
                .map(this::convertArrayToDropdownDTO)
                .collect(Collectors.toList());
    }
    
    private ClassDropdownDTO convertArrayToDropdownDTO(Object[] row) {
        return new ClassDropdownDTO(
                (Long) row[0],           // id
                (String) row[1],         // className
                (String) row[3]          // teacherName
        );
    }
}
