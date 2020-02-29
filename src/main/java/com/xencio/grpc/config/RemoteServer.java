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
     * 主机地址
     */
    private String host;

    /**
     * 服务端口号
     */
    private int port;

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

}
