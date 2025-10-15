package com.moktob.system;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemSettingService {
    
    private final SystemSettingRepository systemSettingRepository;
    
    public List<SystemSetting> getAllSettings() {
        Long clientId = TenantContextHolder.getTenantId();
        return systemSettingRepository.findByClientId(clientId);
    }
    
    public Optional<SystemSetting> getSettingById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return systemSettingRepository.findByClientIdAndId(clientId, id);
    }
    
    public SystemSetting saveSetting(SystemSetting setting) {
        Long clientId = TenantContextHolder.getTenantId();
        setting.setClientId(clientId);
        return systemSettingRepository.save(setting);
    }
    
    public void deleteSetting(Long id) {
        systemSettingRepository.deleteById(id);
    }
    
    public Optional<SystemSetting> getSettingByKey(String keyName) {
        Long clientId = TenantContextHolder.getTenantId();
        return systemSettingRepository.findByClientIdAndKeyName(clientId, keyName);
    }
    
    public String getSettingValue(String keyName) {
        Optional<SystemSetting> setting = getSettingByKey(keyName);
        return setting.map(SystemSetting::getKeyValue).orElse(null);
    }
    
    public void setSettingValue(String keyName, String keyValue, String description) {
        Optional<SystemSetting> existingSetting = getSettingByKey(keyName);
        if (existingSetting.isPresent()) {
            SystemSetting setting = existingSetting.get();
            setting.setKeyValue(keyValue);
            setting.setDescription(description);
            saveSetting(setting);
        } else {
            SystemSetting newSetting = new SystemSetting();
            newSetting.setKeyName(keyName);
            newSetting.setKeyValue(keyValue);
            newSetting.setDescription(description);
            saveSetting(newSetting);
        }
    }
}
