package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByClientId(Long clientId);
    Optional<ClassEntity> findByClientIdAndId(Long clientId, Long id);
    List<ClassEntity> findByClientIdAndTeacherId(Long clientId, Long teacherId);
    List<ClassEntity> findByClientIdAndClassNameContainingIgnoreCase(Long clientId, String className);
}
