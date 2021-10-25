package io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter;

import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.annotation.IgnoreTenantField;
import io.github.lmm1990.mybatis.plugin.tenant.spring.boot.starter.handler.TenantDataHandler;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

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
        if(mapperFactoryBean.getObjectType().isAnnotationPresent(IgnoreTenantField.class)){
            TenantDataHandler.ignoreTenantfieldMethods.add(mapperName);
            return bean;
        }
        ReflectionUtils.doWithMethods(mapperFactoryBean.getObjectType(), method -> {
            if (method.isAnnotationPresent(IgnoreTenantField.class)) {
                TenantDataHandler.ignoreTenantfieldMethods.add(String.format("%s.%s", mapperName, method.getName()));
            }
        });
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
