package com.moktob.service.impl;

import com.moktob.common.TenantContextHolder;
import com.moktob.core.Client;
import com.moktob.core.ClientService;
import com.moktob.core.Role;
import com.moktob.core.RoleService;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountService;
import com.moktob.dto.ClientRegistrationRequest;
import com.moktob.dto.ClientRegistrationResponse;
import com.moktob.service.AuthenticationService;
import com.moktob.service.ClientRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientRegistrationServiceImpl implements ClientRegistrationService {

    private final ClientService clientService;
    private final RoleService roleService;
    private final UserAccountService userAccountService;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        // Create the client
        Client client = createClient(request);
        Client savedClient = clientService.saveClient(client);
        
        // Create default roles for the client
        createDefaultRoles(savedClient.getClientId());
        
        // Create admin user for the client
        UserAccount adminUser = createAdminUser(savedClient.getClientId(), request);
        
        return new ClientRegistrationResponse(
                savedClient,
                adminUser.getUsername(),
                "Temporary password: " + tempPasswordForResponse
        );
    }

    private Client createClient(ClientRegistrationRequest request) {
        Client client = new Client();
        client.setClientName(request.getClientName());
        client.setContactEmail(request.getContactEmail());
        client.setContactPhone(request.getContactPhone());
        client.setAddress(request.getAddress());
        client.setSubscriptionPlan(request.getSubscriptionPlan());
        if (request.getExpiryDate() != null) {
            client.setExpiryDate(LocalDate.parse(request.getExpiryDate()));
        }
        client.setIsActive(true);
        return client;
    }

    private void createDefaultRoles(Long clientId) {
        TenantContextHolder.setTenantId(clientId);
        
        try {
            // Create ADMIN role
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            adminRole.setDescription("System Administrator");
            roleService.saveRole(adminRole);
            
            // Create TEACHER role
            Role teacherRole = new Role();
            teacherRole.setRoleName("TEACHER");
            teacherRole.setDescription("Teacher Role");
            roleService.saveRole(teacherRole);
            
            // Create STUDENT role
            Role studentRole = new Role();
            studentRole.setRoleName("STUDENT");
            studentRole.setDescription("Student Role");
            roleService.saveRole(studentRole);
            
            // Create PARENT role
            Role parentRole = new Role();
            parentRole.setRoleName("PARENT");
            parentRole.setDescription("Parent Role");
            roleService.saveRole(parentRole);
            
        } finally {
            TenantContextHolder.clear();
        }
    }

    private UserAccount createAdminUser(Long clientId, ClientRegistrationRequest request) {
        TenantContextHolder.setTenantId(clientId);
        
        try {
            // Get the ADMIN role
            Role adminRole = roleService.getRoleByName("ADMIN").orElse(null);
            
            // Generate temporary password
            String tempPassword = authenticationService.generateTemporaryPassword();
            
            UserAccount adminUser = new UserAccount();
            adminUser.setUsername(request.getAdminUsername());
            adminUser.setPasswordHash(authenticationService.passwordEncoder.encode(tempPassword));
            adminUser.setFullName(request.getAdminFullName());
            adminUser.setEmail(request.getAdminEmail());
            adminUser.setPhone(request.getAdminPhone());
            adminUser.setRoleId(adminRole != null ? adminRole.getId() : null);
            adminUser.setIsActive(true);
            
            UserAccount savedUser = userAccountService.saveUser(adminUser);
            
            // Store temp password in response
            tempPasswordForResponse = tempPassword;
            
            return savedUser;
            
        } finally {
            TenantContextHolder.clear();
        }
    }

    private String tempPasswordForResponse;
}
