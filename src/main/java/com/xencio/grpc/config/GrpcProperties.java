package com.xencio.grpc.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
     * client interceptor
     */
    private Class clientInterceptor;

    /**
     * server interceptor
     */
    private Class serverInterceptor;

    /**
     * client interceptor
     */
    private String clientInterceptorName;

    /**
     * server interceptor
     */
    private String serverInterceptorName;

}
