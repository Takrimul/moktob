package com.moktob.education;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByClientId(Long clientId);
    
    @Query("SELECT s FROM Student s WHERE s.clientId = :clientId AND s.currentClassId = :classId")
    List<Student> findByClientIdAndCurrentClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
    
    Optional<Student> findByClientIdAndId(Long clientId, Long id);
    
    List<Student> findByClientIdAndNameContainingIgnoreCase(Long clientId, String name);
    List<Student> findByClientIdAndEmail(Long clientId, String email);
    
    long countByClientId(Long clientId);
    
    @Query("SELECT s.id, s.name, s.dateOfBirth, s.guardianName, s.guardianContact, s.address, " +
           "s.enrollmentDate, s.currentClassId, s.photoUrl, c.className " +
           "FROM Student s LEFT JOIN ClassEntity c ON s.currentClassId = c.id " +
           "WHERE s.clientId = :clientId")
    List<Object[]> findStudentWithClassNamesByClientId(@Param("clientId") Long clientId);
    
    @Query("SELECT s.id, s.name, s.dateOfBirth, s.guardianName, s.guardianContact, s.address, " +
           "s.enrollmentDate, s.currentClassId, s.photoUrl, c.className " +
           "FROM Student s LEFT JOIN ClassEntity c ON s.currentClassId = c.id " +
           "WHERE s.clientId = :clientId AND s.currentClassId = :classId")
    List<Object[]> findStudentWithClassNamesByClientIdAndClassId(@Param("clientId") Long clientId, @Param("classId") Long classId);
}
