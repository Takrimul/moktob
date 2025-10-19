package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.ClassRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassEntityService {
    
    private final ClassEntityRepository classEntityRepository;
    
    public List<ClassEntity> getAllClasses() {
        Long clientId = TenantContextHolder.getTenantId();
        return classEntityRepository.findByClientId(clientId);
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
}
