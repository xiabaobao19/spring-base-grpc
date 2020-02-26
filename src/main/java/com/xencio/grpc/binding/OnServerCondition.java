package com.xencio.grpc.binding;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author xiabaobao
 * @date 2020/2/25 13:51
 */
public class OnServerCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        Boolean server = Boolean.parseBoolean(environment.getProperty("spring.grpc.enable", "true"));
        return server;
    }
}
