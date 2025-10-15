package com.moktob.controller;

import com.moktob.core.Role;
import com.moktob.core.RoleService;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAccountController {
    
    private final UserAccountService userAccountService;
    private final RoleService roleService;
    
    @GetMapping
    public ResponseEntity<List<UserAccount>> getAllUsers() {
        return ResponseEntity.ok(userAccountService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserAccount> getUserById(@PathVariable Long id) {
        Optional<UserAccount> user = userAccountService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<UserAccount> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setIsActive(true);
        
        // Set role if provided
        if (request.getRoleName() != null) {
            Optional<Role> role = roleService.getRoleByName(request.getRoleName());
            role.ifPresent(r -> user.setRoleId(r.getId()));
        }
        
        UserAccount savedUser = userAccountService.saveUser(user);
        savedUser.setPasswordHash(null); // Don't return password hash
        
        return ResponseEntity.ok(savedUser);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserAccount> updateUser(@PathVariable Long id, @RequestBody UserAccount user) {
        user.setId(id);
        UserAccount updatedUser = userAccountService.saveUser(user);
        updatedUser.setPasswordHash(null); // Don't return password hash
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<UserAccount>> getActiveUsers() {
        return ResponseEntity.ok(userAccountService.getActiveUsers());
    }
    
    @GetMapping("/by-role/{roleName}")
    public ResponseEntity<List<UserAccount>> getUsersByRole(@PathVariable String roleName) {
        Optional<Role> role = roleService.getRoleByName(roleName);
        if (role.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<UserAccount> users = userAccountService.getAllUsers().stream()
                .filter(user -> user.getRoleId() != null && user.getRoleId().equals(role.get().getId()))
                .toList();
        
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/change-password/{id}")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        Optional<UserAccount> userOpt = userAccountService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        UserAccount user = userOpt.get();
        user.setPasswordHash(request.getNewPassword());
        userAccountService.saveUser(user);
        
        return ResponseEntity.ok("Password changed successfully");
    }
    
    @Data
    @AllArgsConstructor
    public static class CreateUserRequest {
        @NotBlank
        private String username;
        
        @NotBlank
        private String password;
        
        @NotBlank
        private String fullName;
        
        @Email
        private String email;
        
        private String phone;
        private String roleName; // ADMIN, TEACHER, STUDENT, PARENT
    }
    
    @Data
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank
        private String newPassword;
    }
}