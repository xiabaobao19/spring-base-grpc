package com.xencio.grpc.util;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * @author xiabaobao
 * @date 2020/2/17 8:48
 */
//类扫描器
public class ClassPathGrpcServiceScanner extends ClassPathBeanDefinitionScanner {

   public ClassPathGrpcServiceScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

}
