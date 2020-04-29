package com.xencio.grpc.binding;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author xiabaobao
 * @date 2020/2/25 14:01
 */
@Slf4j
public class OnClientCondition implements  Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String serversValue = conditionContext.getEnvironment().getProperty("spring.grpc.servers");
        if (StringUtils.isNotBlank(serversValue)) {
            return true;
        }
        return false;
    }
}
