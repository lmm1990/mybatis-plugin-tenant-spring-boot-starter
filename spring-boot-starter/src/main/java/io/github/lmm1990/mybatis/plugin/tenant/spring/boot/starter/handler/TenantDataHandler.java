package io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler;

import java.util.HashSet;
import java.util.Set;

/**
 * 公共数据
 *
 * @author liumingming
 * @since 2021-09-02 16:37
 */
public class TenantDataHandler {

    /**
     * 租户值
     */
    private static final ThreadLocal<Object> TENANT_FIELD_VALUE = new ThreadLocal<>();


    /**
     * 忽略租户字段方法列表
     */
    public static final Set<String> ignoreTenantfieldMethods = new HashSet<>();

    /**
     * 获得租户值
     *
     * @return java.lang.Object
     * @since 刘明明/2021-09-02 16:41:29
     **/
    public static Object getTenantFieldValue() {
        return TENANT_FIELD_VALUE.get();
    }

    /**
     * 设置租户值
     *
     * @param tenantFieldValue: 租户值
     * @since 刘明明/2021-09-02 16:41:52
     **/
    public static void setTenantFieldValue(Object tenantFieldValue) {
        TenantDataHandler.TENANT_FIELD_VALUE.set(tenantFieldValue);
    }
}
