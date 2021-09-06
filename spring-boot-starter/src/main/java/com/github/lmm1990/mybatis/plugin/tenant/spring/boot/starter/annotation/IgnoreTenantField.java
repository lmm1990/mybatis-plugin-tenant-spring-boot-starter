package com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.annotation;

import java.lang.annotation.*;

/**
 * 忽略租户字段
 *
 * @author liumingming
 * @since 2021-09-03 12:05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreTenantField {
}
