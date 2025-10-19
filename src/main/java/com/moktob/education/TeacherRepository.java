package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findByClientId(Long clientId);
    List<Teacher> findByClientIdAndIsActiveTrue(Long clientId);
    Optional<Teacher> findByClientIdAndId(Long clientId, Long id);
    List<Teacher> findByClientIdAndNameContainingIgnoreCase(Long clientId, String name);
    
    long countByClientId(Long clientId);
    
    long countByClientIdAndIsActiveTrue(Long clientId);
    
    @Query("SELECT t.id, t.name, t.email, t.phone, null, null, " +
           "t.qualification, null, t.joiningDate, null, t.isActive, null " +
           "FROM Teacher t " +
           "WHERE t.clientId = :clientId")
    List<Object[]> findTeacherWithDepartmentNamesByClientId(@Param("clientId") Long clientId);
}
