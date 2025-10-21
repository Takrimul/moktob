package com.moktob.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    
    // Core User Information
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String roleName;
    private Long roleId;
    private Boolean isActive;
    
    // Tenant Information
    private Long tenantId;
    private String clientName;
    
    // Role-specific Information
    private Long studentId;    // If user is a student
    private Long teacherId;    // If user is a teacher
    private String studentName;
    private String teacherName;
    
    // Session Information
    private String sessionId;
    private Long loginTime;
    
    // Utility Methods
    public boolean isStudent() {
        return studentId != null;
    }
    
    public boolean isTeacher() {
        return teacherId != null;
    }
    
    public boolean isAdmin() {
        return roleName != null && (roleName.equalsIgnoreCase("ADMIN") || 
                                   roleName.equalsIgnoreCase("SUPER_ADMIN"));
    }
    
    public String getDisplayName() {
        if (isStudent() && studentName != null) {
            return studentName;
        } else if (isTeacher() && teacherName != null) {
            return teacherName;
        } else if (fullName != null) {
            return fullName;
        } else {
            return username;
        }
    }
    
    public String getUserType() {
        if (isStudent()) return "STUDENT";
        if (isTeacher()) return "TEACHER";
        if (isAdmin()) return "ADMIN";
        return "USER";
    }
    
    public boolean hasRole(String roleName) {
        return this.roleName != null && this.roleName.equalsIgnoreCase(roleName);
    }
}
