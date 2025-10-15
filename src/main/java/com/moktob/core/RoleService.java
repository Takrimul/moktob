package com.moktob.core;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    
    private final RoleRepository roleRepository;
    
    public List<Role> getAllRoles() {
        Long clientId = TenantContextHolder.getTenantId();
        return roleRepository.findByClientId(clientId);
    }
    
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Role saveRole(Role role) {
        Long clientId = TenantContextHolder.getTenantId();
        role.setClientId(clientId);
        return roleRepository.save(role);
    }
    
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    
    public Optional<Role> getRoleByName(String roleName) {
        Long clientId = TenantContextHolder.getTenantId();
        return roleRepository.findByClientIdAndRoleName(clientId, roleName);
    }
}
