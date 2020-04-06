package com.xencio.grpc.config;

import lombok.Data;

import java.util.List;

@Data
public class GrpcProperties {

    /**
     * enable server start
     */
    private boolean enable;

    /**
     * server listen port
     */
    private int port;

    /**
     * client config
     */
    private List<RemoteServer> remoteServers;

    /**
     * server interceptor
     */
    private Class serverInterceptor;

    /**
     * server interceptor
     */
    private String serverInterceptorName;
    /**
     * 服务端配置 token 参数
     */
    private String token;

}
