package com.xencio.grpc.binding;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author xiabaobao
 * @date 2020/2/25 13:51
 */
public class OnServerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String portValue = conditionContext.getEnvironment().getProperty("spring.grpc.port");
        if (StringUtils.isNotBlank(portValue)) {
            return true;
        }
        return false;
    }
}
