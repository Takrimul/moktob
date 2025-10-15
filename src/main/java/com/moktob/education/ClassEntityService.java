package com.moktob.education;

import com.moktob.common.TenantContextHolder;
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
    
    public ClassEntity saveClass(ClassEntity classEntity) {
        Long clientId = TenantContextHolder.getTenantId();
        classEntity.setClientId(clientId);
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
