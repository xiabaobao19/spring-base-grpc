package com.xencio.grpc.interceptor;

import io.grpc.*;

/**
 * @author xiabaobao
 * @date 2020/3/6 14:27
 */
public class MyServerInterceptor implements ServerInterceptor {

    //服务端header的key
    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("serverHeader", Metadata.ASCII_STRING_MARSHALLER);
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        //输出客户端传递过来的header
        System.out.println("header received from client:" + headers);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendHeaders(Metadata responseHeaders) {
                //在返回中增加header
                responseHeaders.put(CUSTOM_HEADER_KEY, "response");
                super.sendHeaders(responseHeaders);
            }
        }, headers);
    }
}
