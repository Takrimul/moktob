package com.moktob.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantContextHolder {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    // Set complete user context
    public static void setUserContext(UserContext context) {
        userContext.set(context);
    }

    // Get complete user context
    public static UserContext getUserContext() {
        return userContext.get();
    }

    // Legacy method for backward compatibility
    public static void setTenantId(Long id) {
        UserContext context = userContext.get();
        if (context == null) {
            context = UserContext.builder().build();
        }
        context.setTenantId(id);
        userContext.set(context);
    }

    // Legacy method for backward compatibility
    public static Long getTenantId() {
        UserContext context = userContext.get();
        return context != null ? context.getTenantId() : null;
    }

    // Quick access methods for common properties
    public static String getClientName() {
        UserContext context = userContext.get();
        return context != null ? context.getClientName() : null;
    }

    public static Long getUserId() {
        UserContext context = userContext.get();
        return context != null ? context.getUserId() : null;
    }

    public static String getUsername() {
        UserContext context = userContext.get();
        return context != null ? context.getUsername() : null;
    }

    public static String getDisplayName() {
        UserContext context = userContext.get();
        return context != null ? context.getDisplayName() : null;
    }

    public static String getUserType() {
        UserContext context = userContext.get();
        return context != null ? context.getUserType() : null;
    }

    public static String getRoleName() {
        UserContext context = userContext.get();
        return context != null ? context.getRoleName() : null;
    }

    public static Long getStudentId() {
        UserContext context = userContext.get();
        return context != null ? context.getStudentId() : null;
    }

    public static Long getTeacherId() {
        UserContext context = userContext.get();
        return context != null ? context.getTeacherId() : null;
    }

    public static boolean isStudent() {
        UserContext context = userContext.get();
        return context != null && context.isStudent();
    }

    public static boolean isTeacher() {
        UserContext context = userContext.get();
        return context != null && context.isTeacher();
    }

    public static boolean isAdmin() {
        UserContext context = userContext.get();
        return context != null && context.isAdmin();
    }

    public static boolean hasRole(String roleName) {
        UserContext context = userContext.get();
        return context != null && context.hasRole(roleName);
    }

    public static boolean isActive() {
        UserContext context = userContext.get();
        return context != null && context.getIsActive() != null && context.getIsActive();
    }

    // Clear context
    public static void clear() {
        userContext.remove();
    }

    // Check if context is set
    public static boolean hasContext() {
        return userContext.get() != null;
    }
}
