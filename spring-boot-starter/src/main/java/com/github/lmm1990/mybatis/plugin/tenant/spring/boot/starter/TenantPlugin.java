package com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter;

import com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.config.TenantConfig;
import com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.entity.BoundSqlSource;
import com.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler.TenantDataHandler;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Properties;

/**
 * mybatis多租户插件
 *
 * @author liumingming
 * @since 2021-09-02 16:44:19
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class TenantPlugin implements Interceptor {

    @Autowired
    TenantConfig tenantConfig;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation
                .getArgs()[0];

        //忽略租户字段
        if (TenantDataHandler.ignoreTenantfieldMethods.contains(ms.getId())) {
            return invocation.proceed();
        }

        Object parameterObject = null;
        if (invocation.getArgs().length > 1) {
            parameterObject = invocation.getArgs()[1];
        }
        if (ms.getSqlCommandType() == SqlCommandType.UNKNOWN || ms.getSqlCommandType() == SqlCommandType.FLUSH) {
            return invocation.proceed();
        }
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        //已经存在租户字段，返回
        if (sql.contains(tenantConfig.getFieldName())) {
            return invocation.proceed();
        }
        for (String tableName : tenantConfig.getIgnoreTableNames()) {
            //忽略数据表
            if (sql.contains(tableName)) {
                return invocation.proceed();
            }
        }
        if (sql == null || sql.isEmpty()) {
            return invocation.proceed();
        }
        sql = addTenantInfo(ms.getSqlCommandType(), sql);
        resetSql2Invocation(invocation, sql);
        return invocation.proceed();
    }

    /**
     * 将新sql绑定到mybatis调用
     *
     * @param invocation: mybatis调用
     * @param sql:        sql语句
     * @since 刘明明/2021-09-02 13:53:33
     **/
    private void resetSql2Invocation(Invocation invocation, String sql) {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        final BoundSql boundSql = statement.getBoundSql(parameterObject);

        // 重新new一个查询语句对像
        BoundSql newBoundSql = new BoundSql(statement.getConfiguration(), sql, boundSql.getParameterMappings(),
                parameterObject);
        // 把新的查询放到statement里
        MappedStatement newStatement = copyFromMappedStatement(statement, new BoundSqlSource(newBoundSql));

        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        args[0] = newStatement;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * 添加租户信息
     *
     * @param commandType: sql类型
     * @param sql:         sql语句
     * @return java.lang.String
     * @since 刘明明/2021-09-02 13:45:54
     **/
    private String addTenantInfo(SqlCommandType commandType, String sql) {
        if (commandType == SqlCommandType.INSERT) {
            return addInsertTenantInfo(sql);
        }
        return addWhereTenantInfo(sql);
    }

    /**
     * insert语句新增租户信息
     *
     * @param sql sql语句
     * @return 改写后的sql
     */
    private String addInsertTenantInfo(String sql) {
        int index = sql.indexOf(")");
        int valueIndex = sql.lastIndexOf(")");
        if (index == -1 || valueIndex == -1 || index == valueIndex) {
            return sql;
        }
        StringBuilder finalSql = new StringBuilder();
        finalSql.append(sql, 0, index);
        finalSql.append(",");
        finalSql.append(tenantConfig.getFieldName());
        finalSql.append(sql, index, valueIndex);
        finalSql.append(",'");
        finalSql.append(TenantDataHandler.getTenantFieldValue());
        finalSql.append("'");
        finalSql.append(sql.substring(valueIndex));
        return finalSql.toString();
    }

    /**
     * update语句新增租户信息
     *
     * @param sql sql语句
     * @return 改写后的sql
     */
    private String addWhereTenantInfo(String sql) {
        StringBuilder finalSql = new StringBuilder();
        finalSql.append(sql);
        int whereIndex = sql.toLowerCase(Locale.ROOT).indexOf("where");
        if (whereIndex == -1) {
            finalSql.append(" where ");
        } else {
            finalSql.append(" and ");
        }
        for (String condition : tenantConfig.getFieldNamePreFixConditions()) {
            if (sql.contains(condition)) {
                finalSql.append(tenantConfig.getFieldNamePreFix());
                finalSql.append(".");
                break;
            }
        }
        finalSql.append(tenantConfig.getFieldName());
        finalSql.append("='");
        finalSql.append(TenantDataHandler.getTenantFieldValue());
        finalSql.append("'");
        return finalSql.toString();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}