package com.xencio.grpc.binding;

import com.xencio.grpc.config.RemoteServer;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import javax.annotation.Nullable;
import java.net.URI;

/**
 * @author xiabaobao
 * @date 2020/3/31 10:44
 */
public class MyNameResolverProvider extends NameResolverProvider {

    RemoteServer remoteServers;

    public MyNameResolverProvider(RemoteServer remoteServers){
        this.remoteServers =remoteServers;
    }
    // 服务是否可用
    @Override
    protected boolean isAvailable() {
        return true;
    }

    // 优先级默认5
    @Override
    protected int priority() {
        return 5;
    }

    // 服务发现类
    @Nullable
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        return new MyNameResolver(remoteServers);
    }

    // 服务协议
    @Override
    public String getDefaultScheme() {
        return "local";
    }


}
