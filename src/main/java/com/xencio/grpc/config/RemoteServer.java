package com.xencio.grpc.config;

import lombok.Data;

import java.util.List;

/**
 * 远程服务
 */
@Data
public class RemoteServer {

    /**
     * 服务名
     */
    private String server;


    /**
     * 服务的 ip 与 port
     */
    private List<GrpcAddress> addresses;

    /**
     * 调用服务的公共 类接口名
     */
    private List<String> serverClassNames;
    /**
     * 调用的公共服务类的包名
     */
    private String serverPackages;
    /**
     * 服务器 序列化
     */
    private Integer serializeTypeValue = 1;

    /**
     * 是否开启注释扫描  @MyGrpcService
     */
    private Boolean enableScan;

    /**
     * 扫描路径 默认
     */
    private String scanPackages;

    /**
     * 用于 验证token
     */
    private String token;

    /**
     * client interceptor
     */
    private String clientInterceptorName;

    /**
     * client interceptor
     */
    private Class clientInterceptor;

}
