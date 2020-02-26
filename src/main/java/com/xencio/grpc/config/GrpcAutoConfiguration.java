package com.xencio.grpc.config;

import com.xencio.grpc.GrpcClient;
import com.xencio.grpc.GrpcServer;
import com.xencio.grpc.binding.OnClientCondition;
import com.xencio.grpc.binding.OnServerCondition;
import com.xencio.grpc.service.CommonService;
import com.xencio.grpc.service.SerializeService;
import com.xencio.grpc.service.impl.SofaHessianSerializeService;
import com.xencio.grpc.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Configuration
@Import(ExternalGrpcServiceScannerRegistrar.class)
public class GrpcAutoConfiguration {

    @Value("#{grpcProperties}")
    private  GrpcProperties grpcProperties;

    /**
     * 全局 properties 序列化/反序列化
     */
    @Bean(autowire = Autowire.BY_NAME,name = "myGrpcServers")
    public List<RemoteServer> myGrpcServers() {
        List<RemoteServer> remoteServers = grpcProperties.getRemoteServers();
        return remoteServers;
    }

    @Bean
    public SpringUtils grpcSpringUtils() {
        return new SpringUtils();
    }


    /**
     * 全局 RPC 序列化/反序列化
     */
    @Bean
    public SerializeService serializeService() {
        return new SofaHessianSerializeService();
    }

    /**
     * PRC 服务调用
     */
    @Bean
    public CommonService commonService(SerializeService serializeService) {
        return new CommonService(serializeService);
    }

    /**
     * RPC 服务端
     */
    @Bean
    @Conditional({OnServerCondition.class})
    public GrpcServer grpcServer(CommonService commonService) throws Exception {
        GrpcServer server = new GrpcServer(grpcProperties, commonService);
        server.start();
        return server;
    }

    /**
     * RPC 客户端
     */
    @Bean
    @Conditional({OnClientCondition.class})
    public GrpcClient grpcClient(SerializeService serializeService) {
        GrpcClient client = new GrpcClient(grpcProperties, serializeService);
        client.init();
        return client;
    }

}
