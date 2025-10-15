package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByClientId(Long clientId);
    List<Teacher> findByClientIdAndIsActiveTrue(Long clientId);
    Optional<Teacher> findByClientIdAndId(Long clientId, Long id);
    List<Teacher> findByClientIdAndNameContainingIgnoreCase(Long clientId, String name);
}
