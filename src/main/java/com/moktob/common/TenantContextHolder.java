package com.moktob.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantContextHolder {
    private static final ThreadLocal<Long> tenantId = new ThreadLocal<>();

    public static void setTenantId(Long id) {
        tenantId.set(id);
    }

    public static Long getTenantId() {
        return tenantId.get();
    }

    public static void clear() {
        tenantId.remove();
    }
}
