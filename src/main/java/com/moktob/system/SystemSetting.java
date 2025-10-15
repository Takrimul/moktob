package com.moktob.system;

import com.moktob.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_setting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SystemSetting extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "key_name", nullable = false, length = 100)
    private String keyName;
    
    @Column(name = "key_value", columnDefinition = "TEXT")
    private String keyValue;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
