package com.moktob.controller;

import com.moktob.core.Client;
import com.moktob.core.ClientService;
import com.moktob.core.Role;
import com.moktob.core.RoleService;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountService;
import com.moktob.dto.ClientRegistrationRequest;
import com.moktob.dto.ClientRegistrationResponse;
import com.moktob.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    
    private final ClientService clientService;
    private final RoleService roleService;
    private final UserAccountService userAccountService;
    private final AuthenticationService authenticationService;
    
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.saveClient(client));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ClientRegistrationResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) {
        // Create the client
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
        
        Client savedClient = clientService.saveClient(client);
        
        // Create default roles for the client
        createDefaultRoles(savedClient.getClientId());
        
        // Create admin user for the client
        UserAccount adminUser = createAdminUser(savedClient.getClientId(), request);
        
        return ResponseEntity.ok(new ClientRegistrationResponse(
                savedClient,
                adminUser.getUsername(),
                "Temporary password: " + tempPasswordForResponse
        ));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        client.setClientId(id);
        return ResponseEntity.ok(clientService.saveClient(client));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Client>> getActiveClients() {
        return ResponseEntity.ok(clientService.getActiveClients());
    }
    
    private void createDefaultRoles(Long clientId) {
        // Temporarily set tenant context for role creation
        com.moktob.common.TenantContextHolder.setTenantId(clientId);
        
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
            com.moktob.common.TenantContextHolder.clear();
        }
    }
    
    private UserAccount createAdminUser(Long clientId, ClientRegistrationRequest request) {
        // Temporarily set tenant context for user creation
        com.moktob.common.TenantContextHolder.setTenantId(clientId);
        
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
            com.moktob.common.TenantContextHolder.clear();
        }
    }
    
    private String tempPasswordForResponse;
}