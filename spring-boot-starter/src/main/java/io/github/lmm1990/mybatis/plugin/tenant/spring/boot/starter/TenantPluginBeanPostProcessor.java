package io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter;

import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.annotation.IgnoreTenantField;
import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler.TenantDataHandler;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 后置处理器，解析mapper方法自定义注解
 *
 * @author liumingming
 * @since 2021-09-03 12:02
 */
@Component
public class TenantPluginBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof MapperFactoryBean)) {
            return bean;
        }
        MapperFactoryBean mapperFactoryBean = (MapperFactoryBean) bean;
        final String mapperName = mapperFactoryBean.getObjectType().getName();
        Method[] methods = mapperFactoryBean.getObjectType().getMethods();
        for (Method item : methods) {
            IgnoreTenantField annotation = item.getAnnotation(IgnoreTenantField.class);
            if (annotation != null) {
                TenantDataHandler.ignoreTenantfieldMethods.add(String.format("%s.%s", mapperName, item.getName()));
            }
        }

        Configuration configuration = mapperFactoryBean.getSqlSession().getConfiguration();
        configuration.getMapper(mapperFactoryBean.getObjectType(), mapperFactoryBean.getSqlSession());

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
