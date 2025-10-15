package com.moktob.education;

import com.moktob.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Column(name = "guardian_name", length = 100)
    private String guardianName;
    
    @Column(name = "guardian_contact", length = 20)
    private String guardianContact;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
    
    @Column(name = "current_class_id")
    private Long currentClassId;
    
    @Column(name = "photo_url", length = 255)
    private String photoUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_class_id", insertable = false, updatable = false)
    private ClassEntity currentClass;
}
