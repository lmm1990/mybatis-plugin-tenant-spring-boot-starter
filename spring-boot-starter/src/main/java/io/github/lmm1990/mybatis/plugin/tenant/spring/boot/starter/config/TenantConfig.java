package io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 多租户配置
 *
 * @author liumingming
 * @since 2021-09-02 16:32
 */
@Component
@EnableConfigurationProperties(TenantConfig.class)
@ConfigurationProperties(prefix = "mybatis.tenant-plugin", ignoreInvalidFields = true)
public class TenantConfig {

    private static final int TEMP_FIELD_NAME_LENGTH = 2;

    /**
     * 租户字段名称
     */
    private String fieldName = "tenantId";

    /**
     * 租户字段名称前缀
     */
    private String fieldNamePreFix = "";

    /**
     * 租户字段名称前缀条件列表
     */
    private String[] fieldNamePreFixConditions = new String[0];

    /**
     * 忽略表名列表
     */
    private Set<String> ignoreTableNames = new HashSet<>();

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        String[] tempFieldNames = fieldName.split("\\.");
        if (tempFieldNames.length == TEMP_FIELD_NAME_LENGTH) {
            this.fieldNamePreFix = tempFieldNames[0];
            this.fieldName = tempFieldNames[1];
            fieldNamePreFixConditions = new String[]{
                    String.format(" %s ", this.fieldNamePreFix),
                    String.format(" %s\n", this.fieldNamePreFix)
            };
            return;
        }
        this.fieldName = fieldName;
    }

    public Set<String> getIgnoreTableNames() {
        return ignoreTableNames;
    }

    public void setIgnoreTableNames(Set<String> ignoreTableNames) {
        this.ignoreTableNames = ignoreTableNames;
    }

    public String getFieldNamePreFix() {
        return fieldNamePreFix;
    }

    public String[] getFieldNamePreFixConditions() {
        return fieldNamePreFixConditions;
    }
}
