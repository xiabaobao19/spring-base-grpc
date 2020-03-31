package com.xencio.grpc.binding;

import com.xencio.grpc.config.RemoteServer;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiabaobao
 * @date 2020/3/31 11:05
 */
@Slf4j
public class MyNameResolver extends NameResolver {

    List<RemoteServer> remoteServers;

    public MyNameResolver(List<RemoteServer> remoteServers){
        this.remoteServers =remoteServers;
    }

    @Override
    public String getServiceAuthority() {
        return "none";
    }


    // 配置可用服务，RPC在调用的时候，轮询选择这里配置的可用的服务地址列表
    @Override
    public void start(Listener listener) {
        log.info("LocalNameResolver start ...");
        ArrayList<EquivalentAddressGroup> addressGroups = new ArrayList<>();
        // 获取rpc地址的配置列表
        for (RemoteServer server : remoteServers) {
            String host = server.getHost();
            int port = server.getPort();
            if (host.trim().length() > 0 && port > 0) {
                List<SocketAddress> socketAddresses = new ArrayList<>();
                socketAddresses.add(new InetSocketAddress(host,port));
                addressGroups.add(new EquivalentAddressGroup(socketAddresses));
            }
        }
        listener.onAddresses(addressGroups, Attributes.EMPTY);
    }


    @Override
    public void shutdown() {

    }
}
