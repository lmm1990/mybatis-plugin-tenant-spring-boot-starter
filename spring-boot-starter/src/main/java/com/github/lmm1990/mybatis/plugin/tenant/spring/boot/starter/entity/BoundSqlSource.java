package com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.entity;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * sql
 *
 * @author liumingming
 * @since 2021-09-02 13:50
 */
public class BoundSqlSource implements SqlSource {

    private BoundSql boundSql;

    public BoundSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }

}
