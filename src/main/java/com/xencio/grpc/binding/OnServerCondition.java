package com.xencio.grpc.binding;

import com.xencio.grpc.util.PropertiesValueUtils;
import org.apache.commons.lang3.StringUtils;
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
        Boolean enable = Boolean.parseBoolean(PropertiesValueUtils.getRealValue("spring.grpc.enable"));
        String portValue = PropertiesValueUtils.getRealValue("spring.grpc.port");
        if (enable && StringUtils.isNotBlank(portValue)) {
            return true;
        }
        return false;
    }
}
