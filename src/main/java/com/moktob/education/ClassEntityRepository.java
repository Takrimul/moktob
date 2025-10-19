package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByClientId(Long clientId);
    Optional<ClassEntity> findByClientIdAndId(Long clientId, Long id);
    List<ClassEntity> findByClientIdAndTeacherId(Long clientId, Long teacherId);
    List<ClassEntity> findByClientIdAndClassNameContainingIgnoreCase(Long clientId, String className);
    
    long countByClientId(Long clientId);
    
    @Query("SELECT c.id, c.className, c.teacherId, t.name, c.startTime, c.endTime, c.daysOfWeek, " +
           "COALESCE(COUNT(scm.studentId), 0) " +
           "FROM ClassEntity c " +
           "LEFT JOIN Teacher t ON c.teacherId = t.id " +
           "LEFT JOIN StudentClassMap scm ON c.id = scm.classEntity.id " +
           "WHERE c.clientId = :clientId " +
           "GROUP BY c.id, c.className, c.teacherId, t.name, c.startTime, c.endTime, c.daysOfWeek")
    List<Object[]> findClassWithTeacherNamesAndStudentCountsByClientId(@Param("clientId") Long clientId);
}
