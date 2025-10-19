package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import com.moktob.dto.TeacherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    
    public List<Teacher> getAllTeachers() {
        Long clientId = TenantContextHolder.getTenantId();
        return teacherRepository.findByClientId(clientId);
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
