package com.xencio.grpc.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiabaobao
 * @date 2020/4/4 12:44
 */
@Getter
@Setter
public class GrpcAddress {

    private String host;

    private Integer port;

}
