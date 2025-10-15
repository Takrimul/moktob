package com.moktob.system;

import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    List<SystemSetting> findByClientId(Long clientId);
    
    Optional<SystemSetting> findByClientIdAndId(Long clientId, Long id);
    
    Optional<SystemSetting> findByClientIdAndKeyName(Long clientId, String keyName);
}
