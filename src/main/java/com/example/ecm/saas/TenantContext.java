package com.example.ecm.saas;



public class TenantContext {
    private static final ThreadLocal<Long> currentTenantId = new ThreadLocal<>();

    public static Long getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void setCurrentTenant(Long tenantId) {
        currentTenantId.set(tenantId);
    }

    public static void clear() {
        currentTenantId.remove();
    }
}