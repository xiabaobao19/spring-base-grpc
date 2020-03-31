package com.xencio.grpc;

import com.xencio.grpc.binding.MyNameResolverProvider;
import com.xencio.grpc.config.GrpcProperties;
import com.xencio.grpc.config.RemoteServer;
import com.xencio.grpc.service.SerializeService;
import io.grpc.*;
import io.grpc.internal.DnsNameResolverProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class GrpcClient {

    private static final Map<String, ServerContext> serverMap = new HashMap<>();

    private final GrpcProperties grpcProperties;

    private final SerializeService serializeService;

    private ClientInterceptor clientInterceptor;

    public GrpcClient(GrpcProperties grpcProperties, SerializeService serializeService) {
        this.grpcProperties = grpcProperties;
        this.serializeService = serializeService;
    }

    public GrpcClient(GrpcProperties grpcProperties, SerializeService serializeService, ClientInterceptor clientInterceptor) {
        this.grpcProperties = grpcProperties;
        this.serializeService = serializeService;
        this.clientInterceptor = clientInterceptor;
    }

    /**
     * 初始化
     */
    public void init(){
        List<RemoteServer> remoteServers = grpcProperties.getRemoteServers();
        if (!CollectionUtils.isEmpty(remoteServers)) {
            Map<String, List<RemoteServer>> groupServers = remoteServers.parallelStream().collect(Collectors.groupingBy(RemoteServer::getServer));
            groupServers.forEach((k,v)->{
                ManagedChannel channel = ManagedChannelBuilder.forTarget("local")
                        .defaultLoadBalancingPolicy("round_robin")
                        .nameResolverFactory(new MyNameResolverProvider(v))
                        .enableRetry()
                        .maxRetryAttempts(5)
                        .idleTimeout(30, TimeUnit.SECONDS)
                        .usePlaintext().build();
                if (clientInterceptor != null){
                    Channel newChannel = ClientInterceptors.intercept(channel, clientInterceptor);
                    serverMap.put(k, new ServerContext(newChannel, serializeService));
                }else {
                    Class clazz = grpcProperties.getClientInterceptor();
                    if (clazz == null) {
                        serverMap.put(k, new ServerContext(channel, serializeService));
                    }else {
                        try {
                            ClientInterceptor interceptor = (ClientInterceptor) clazz.newInstance();
                            Channel newChannel = ClientInterceptors.intercept(channel, interceptor);
                            serverMap.put(k, new ServerContext(newChannel, serializeService));
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.warn("ClientInterceptor cannot use, ignoring...");
                            serverMap.put(k, new ServerContext(channel, serializeService));
                        }
                    }
                }
            });
        }
    }

    /**
     * 连接远程服务
     */
    public static ServerContext connect(String serverName) {
        return serverMap.get(serverName);
    }

}
