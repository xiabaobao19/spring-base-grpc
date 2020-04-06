package com.xencio.grpc.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiabaobao
 * @date 2020/3/6 14:27
 */
@Slf4j
public class MyServerInterceptor implements ServerInterceptor {

    String serverToken;

    public MyServerInterceptor(String serverToken) {
        this.serverToken = serverToken;
    }

    //服务端header的key
    private static final Metadata.Key<String> SERVER_TOKEN_KEY =
            Metadata.Key.of("serverToken", Metadata.ASCII_STRING_MARSHALLER);

    //客户端端header的key
    private static final Metadata.Key<String> CLIENT_TOKEN_KEY =
            Metadata.Key.of("clientToken", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        //输出客户端传递过来的header
        String clientToken = headers.get(CLIENT_TOKEN_KEY);
        if (StringUtils.isNotBlank(serverToken)) {
            if (!serverToken.equals(clientToken)) {
                log.info("token 验证失败");
                call.close(Status.DATA_LOSS, headers);
            }
        }
        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                //在返回中增加header
                // responseHeaders.put(CUSTOM_TOKEN_KEY, "response");
                super.sendHeaders(responseHeaders);
            }
        }, headers);
    }
}
