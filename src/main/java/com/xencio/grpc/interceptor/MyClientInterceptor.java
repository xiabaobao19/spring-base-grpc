package com.xencio.grpc.interceptor;

import io.grpc.*;

/**
 * @author xiabaobao
 * @date 2020/3/6 14:27
 */
public class MyClientInterceptor implements ClientInterceptor {
    //客户端header的key
    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("clientToken", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                //放入客户端的header
                headers.put(CUSTOM_HEADER_KEY, "request");
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        //输出服务端传递回来的header
                        System.out.println("header received from server:" + headers);
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
