package com.xencio.grpc.util;

import com.xencio.grpc.binding.GrpcServiceProxy;
import com.xencio.grpc.config.GrpcAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.util.Set;

/**
 * @author xiabaobao
 * @date 2020/2/17 8:49
 */
@Slf4j
public  class ProxyUtil {
    public static void registerBeans(BeanFactory beanFactory, Set<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String className = beanDefinition.getBeanClassName();
            if (StringUtils.isEmpty(className)) {
                continue;
            }
            try {
                // 创建代理类
                Class<?> target = Class.forName(className);
                Object invoker = new Object();
                InvocationHandler invocationHandler = new GrpcServiceProxy<>(target, invoker);
                Object proxy = Proxy.newProxyInstance(GrpcAutoConfiguration.class.getClassLoader(), new Class[]{target}, invocationHandler);

                // 注册到 Spring 容器
                String beanName = ClassNameUtils.beanName(className);
                ((DefaultListableBeanFactory) beanFactory).registerSingleton(beanName, proxy);
            } catch (ClassNotFoundException e) {
                log.warn("class not found : " + className);
            }
        }
    }
}
