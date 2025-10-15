package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentClassMapService {
    
    private final StudentClassMapRepository studentClassMapRepository;
    
    public List<StudentClassMap> getAllMappings() {
        Long clientId = TenantContextHolder.getTenantId();
        return studentClassMapRepository.findByClientId(clientId);
    }
    
    public StudentClassMap saveMapping(StudentClassMap mapping) {
        Long clientId = TenantContextHolder.getTenantId();
        mapping.setClientId(clientId);
        return studentClassMapRepository.save(mapping);
    }
    
    public void deleteMapping(Long studentId, Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        studentClassMapRepository.deleteByClientIdAndStudentIdAndClassId(clientId, studentId, classId);
    }
    
    public List<StudentClassMap> getMappingsByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentClassMapRepository.findByClientIdAndStudentId(clientId, studentId);
    }
    
    public List<StudentClassMap> getMappingsByClass(Long classId) {
        Long clientId = TenantContextHolder.getTenantId();
        return studentClassMapRepository.findByClientIdAndClassId(clientId, classId);
    }
}
