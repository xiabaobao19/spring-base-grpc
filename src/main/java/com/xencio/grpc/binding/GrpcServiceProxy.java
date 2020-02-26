package com.xencio.grpc.binding;

import com.xencio.grpc.GrpcClient;
import com.xencio.grpc.config.RemoteServer;
import com.xencio.grpc.constant.GrpcResponseStatus;
import com.xencio.grpc.constant.SerializeType;
import com.xencio.grpc.exception.GrpcException;
import com.xencio.grpc.service.GrpcRequest;
import com.xencio.grpc.service.GrpcResponse;
import com.xencio.grpc.util.SpringUtils;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GrpcServiceProxy<T> implements InvocationHandler {

    private Class<T> grpcService;

    private Object invoker;

    public GrpcServiceProxy(Class<T> grpcService, Object invoker) {
        this.grpcService = grpcService;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String className = grpcService.getName();
        if ("toString".equals(methodName) && args.length == 0) {
            return className + "@" + invoker.hashCode();
        } else if ("hashCode".equals(methodName) && args.length == 0) {
            return invoker.hashCode();
        } else if ("equals".equals(methodName) && args.length == 1) {
            Object another = args[0];
            return proxy == another;
        }
        List<RemoteServer> remoteServers = SpringUtils.getBean("myGrpcServers");
        List<RemoteServer> servers = remoteServers.parallelStream().filter(e -> {
            List<String> serverClassNames = e.getServerClassNames();
            if (serverClassNames != null && !CollectionUtils.isEmpty(serverClassNames)) {
                return serverClassNames.contains(className);
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(servers) && servers.size() == 1) {
            RemoteServer remoteServer = servers.get(0);
            String server = remoteServer.getServer();
            GrpcRequest request = new GrpcRequest();
            request.setClazz(className);
            request.setMethod(methodName);
            request.setArgs(args);
            Integer serializeTypeValue = remoteServer.getSerializeTypeValue();
            SerializeType serializeType = SerializeType.getSerializeTypeByValue(serializeTypeValue);
            GrpcResponse response = GrpcClient.connect(server).handle(serializeType, request);
            if (GrpcResponseStatus.ERROR.getCode() == response.getStatus()) {
                Throwable throwable = response.getException();
                GrpcException exception = new GrpcException(throwable.getClass().getName() + ": " + throwable.getMessage());
                StackTraceElement[] exceptionStackTrace = exception.getStackTrace();
                StackTraceElement[] responseStackTrace = response.getStackTrace();
                StackTraceElement[] allStackTrace = Arrays.copyOf(exceptionStackTrace, exceptionStackTrace.length + responseStackTrace.length);
                System.arraycopy(responseStackTrace, 0, allStackTrace, exceptionStackTrace.length, responseStackTrace.length);
                exception.setStackTrace(allStackTrace);
                throw exception;
            }
            return response.getResult();
        }
        return null;
    }

}
