package com.moktob.service;

import com.moktob.common.UserContext;
import com.moktob.core.Client;
import com.moktob.core.ClientRepository;
import com.moktob.core.Role;
import com.moktob.core.RoleRepository;
import com.moktob.education.Student;
import com.moktob.education.StudentRepository;
import com.moktob.education.Teacher;
import com.moktob.education.TeacherRepository;
import com.moktob.core.UserAccount;
import com.moktob.core.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContextService {

    private final UserAccountRepository userAccountRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * Build complete user context from username
     */
    public UserContext buildUserContext(String username) {
        try {
            Optional<UserAccount> userOpt = userAccountRepository.findByUsernameWithRole(username);
            if (userOpt.isEmpty()) {
                log.warn("User not found for username: {}", username);
                return null;
            }

            UserAccount user = userOpt.get();
            UserContext.UserContextBuilder builder = UserContext.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .tenantId(user.getClientId())
                    .isActive(user.getIsActive())
                    .loginTime(System.currentTimeMillis());

            // Set role information
            if (user.getRoleId() != null) {
                Optional<Role> roleOpt = roleRepository.findById(user.getRoleId());
                if (roleOpt.isPresent()) {
                    Role role = roleOpt.get();
                    builder.roleId(role.getId())
                           .roleName(role.getRoleName());
                }
            }

            // Set client information
            Optional<Client> clientOpt = clientRepository.findById(user.getClientId());
            if (clientOpt.isPresent()) {
                builder.clientName(clientOpt.get().getClientName());
            }

            // Set role-specific information
            String roleName = builder.build().getRoleName();
            if ("STUDENT".equalsIgnoreCase(roleName)) {
                // For students, we'll set the student name from the user's full name
                // The actual student ID mapping can be enhanced later if needed
                builder.studentName(user.getFullName());
            } else if ("TEACHER".equalsIgnoreCase(roleName)) {
                // For teachers, we'll set the teacher name from the user's full name
                // The actual teacher ID mapping can be enhanced later if needed
                builder.teacherName(user.getFullName());
            }

            UserContext context = builder.build();
            log.debug("Built user context for {}: userId={}, role={}, tenantId={}", 
                     username, context.getUserId(), context.getRoleName(), context.getTenantId());
            
            return context;

        } catch (Exception e) {
            log.error("Failed to build user context for username: {}", username, e);
            return null;
        }
    }

    /**
     * Build user context from UserAccount object
     */
    public UserContext buildUserContext(UserAccount user) {
        if (user == null) {
            return null;
        }

        try {
            UserContext.UserContextBuilder builder = UserContext.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .tenantId(user.getClientId())
                    .isActive(user.getIsActive())
                    .loginTime(System.currentTimeMillis());

            // Set role information
            if (user.getRoleId() != null) {
                Optional<Role> roleOpt = roleRepository.findById(user.getRoleId());
                if (roleOpt.isPresent()) {
                    Role role = roleOpt.get();
                    builder.roleId(role.getId())
                           .roleName(role.getRoleName());
                }
            }

            // Set client information
            Optional<Client> clientOpt = clientRepository.findById(user.getClientId());
            if (clientOpt.isPresent()) {
                builder.clientName(clientOpt.get().getClientName());
            }

            return builder.build();

        } catch (Exception e) {
            log.error("Failed to build user context for user: {}", user.getUsername(), e);
            return null;
        }
    }
}
