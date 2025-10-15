package com.moktob.controller;

import com.moktob.core.Role;
import com.moktob.core.RoleService;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountService;
import com.moktob.dto.ChangePasswordRequest;
import com.moktob.dto.CreateUserRequest;
import com.moktob.dto.ResetPasswordRequest;
import com.moktob.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAccountController {
    
    private final UserAccountService userAccountService;
    private final RoleService roleService;
    private final AuthenticationService authenticationService;
    
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
        UserAccount savedUser = authenticationService.createUser(request);
        savedUser.setPasswordHash(null); // Don't return password hash
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/create-teacher")
    public ResponseEntity<UserAccount> createTeacher(@Valid @RequestBody CreateUserRequest request) {
        request.setRoleName("TEACHER");
        UserAccount savedUser = authenticationService.createUser(request);
        savedUser.setPasswordHash(null);
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/create-student")
    public ResponseEntity<UserAccount> createStudent(@Valid @RequestBody CreateUserRequest request) {
        request.setRoleName("STUDENT");
        UserAccount savedUser = authenticationService.createUser(request);
        savedUser.setPasswordHash(null);
        return ResponseEntity.ok(savedUser);
    }
    
    @PostMapping("/create-parent")
    public ResponseEntity<UserAccount> createParent(@Valid @RequestBody CreateUserRequest request) {
        request.setRoleName("PARENT");
        UserAccount savedUser = authenticationService.createUser(request);
        savedUser.setPasswordHash(null);
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
        List<UserAccount> users = authenticationService.getUsersByRole(roleName);
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequest request) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("Authentication required");
        }
        
        try {
            UserAccount user = authenticationService.getUserByUsername(authentication.getName());
            authenticationService.changePassword(user.getId(), request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<String> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        try {
            authenticationService.resetPassword(id, request);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserAccount> getUserProfile(Authentication authentication) {
        if (authentication != null) {
            UserAccount user = authenticationService.getUserByUsername(authentication.getName());
            user.setPasswordHash(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/teachers")
    public ResponseEntity<List<UserAccount>> getTeachers() {
        return ResponseEntity.ok(authenticationService.getUsersByRole("TEACHER"));
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<UserAccount>> getStudents() {
        return ResponseEntity.ok(authenticationService.getUsersByRole("STUDENT"));
    }
    
    @GetMapping("/parents")
    public ResponseEntity<List<UserAccount>> getParents() {
        return ResponseEntity.ok(authenticationService.getUsersByRole("PARENT"));
    }
}