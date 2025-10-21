package com.moktob.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataFilterUtils {

    /**
     * Check if current user can access data for a specific tenant
     */
    public static boolean canAccessTenant(Long tenantId) {
        Long currentTenantId = TenantContextHolder.getTenantId();
        return currentTenantId != null && currentTenantId.equals(tenantId);
    }

    /**
     * Check if current user can access student data
     */
    public static boolean canAccessStudent(Long studentId) {
        if (TenantContextHolder.isAdmin()) {
            return true; // Admins can access all students
        }
        
        if (TenantContextHolder.isStudent()) {
            Long currentStudentId = TenantContextHolder.getStudentId();
            return currentStudentId != null && currentStudentId.equals(studentId);
        }
        
        if (TenantContextHolder.isTeacher()) {
            // Teachers can access students in their classes (implement class-based filtering)
            return true; // For now, allow all students for teachers
        }
        
        return false;
    }

    /**
     * Check if current user can access teacher data
     */
    public static boolean canAccessTeacher(Long teacherId) {
        if (TenantContextHolder.isAdmin()) {
            return true; // Admins can access all teachers
        }
        
        if (TenantContextHolder.isTeacher()) {
            Long currentTeacherId = TenantContextHolder.getTeacherId();
            return currentTeacherId != null && currentTeacherId.equals(teacherId);
        }
        
        return false;
    }

    /**
     * Check if current user can access user account data
     */
    public static boolean canAccessUserAccount(Long userId) {
        if (TenantContextHolder.isAdmin()) {
            return true; // Admins can access all user accounts
        }
        
        Long currentUserId = TenantContextHolder.getUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * Check if current user can perform admin operations
     */
    public static boolean canPerformAdminOperations() {
        return TenantContextHolder.isAdmin();
    }

    /**
     * Check if current user can manage students
     */
    public static boolean canManageStudents() {
        return TenantContextHolder.isAdmin() || TenantContextHolder.isTeacher();
    }

    /**
     * Check if current user can manage teachers
     */
    public static boolean canManageTeachers() {
        return TenantContextHolder.isAdmin();
    }

    /**
     * Check if current user can view reports
     */
    public static boolean canViewReports() {
        return TenantContextHolder.isAdmin() || TenantContextHolder.isTeacher();
    }

    /**
     * Check if current user can manage attendance
     */
    public static boolean canManageAttendance() {
        return TenantContextHolder.isAdmin() || TenantContextHolder.isTeacher();
    }

    /**
     * Get tenant ID for data filtering (ensures data isolation)
     */
    public static Long getFilterTenantId() {
        Long tenantId = TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not found in context. User must be authenticated.");
        }
        return tenantId;
    }

    /**
     * Get user ID for data filtering
     */
    public static Long getFilterUserId() {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User ID not found in context. User must be authenticated.");
        }
        return userId;
    }

    /**
     * Get student ID for data filtering (if current user is a student)
     */
    public static Long getFilterStudentId() {
        if (!TenantContextHolder.isStudent()) {
            return null;
        }
        return TenantContextHolder.getStudentId();
    }

    /**
     * Get teacher ID for data filtering (if current user is a teacher)
     */
    public static Long getFilterTeacherId() {
        if (!TenantContextHolder.isTeacher()) {
            return null;
        }
        return TenantContextHolder.getTeacherId();
    }

    /**
     * Log current user context for debugging
     */
    public static void logCurrentContext() {
        UserContext context = TenantContextHolder.getUserContext();
        if (context != null) {
            System.out.println("Current User Context:");
            System.out.println("  User ID: " + context.getUserId());
            System.out.println("  Username: " + context.getUsername());
            System.out.println("  Display Name: " + context.getDisplayName());
            System.out.println("  Role: " + context.getRoleName());
            System.out.println("  User Type: " + context.getUserType());
            System.out.println("  Tenant ID: " + context.getTenantId());
            System.out.println("  Client Name: " + context.getClientName());
            System.out.println("  Student ID: " + context.getStudentId());
            System.out.println("  Teacher ID: " + context.getTeacherId());
            System.out.println("  Is Active: " + context.getIsActive());
        } else {
            System.out.println("No user context found");
        }
    }
}
