package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClassMapRepository extends JpaRepository<StudentClassMap, StudentClassMapId> {
    List<StudentClassMap> findByClientId(Long clientId);
    List<StudentClassMap> findByClientIdAndStudentId(Long clientId, Long studentId);
    List<StudentClassMap> findByClientIdAndClassId(Long clientId, Long classId);
    void deleteByClientIdAndStudentIdAndClassId(Long clientId, Long studentId, Long classId);
}
