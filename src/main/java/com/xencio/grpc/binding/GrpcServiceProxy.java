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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GrpcServiceProxy<T> implements InvocationHandler {

    private Class<T> grpcService;

    private Object invoker;

    private String server;

    public GrpcServiceProxy(Class<T> grpcService, Object invoker,String server) {
        this.grpcService = grpcService;
        this.invoker = invoker;
        this.server=server;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
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
        Map<String, List<RemoteServer>> serverMap = remoteServers.parallelStream().collect(Collectors.groupingBy(RemoteServer::getServer));
        RemoteServer remoteServer = serverMap.get(server).get(0);
        GrpcRequest request = new GrpcRequest();
        request.setClazz(className);
        request.setMethod(methodName);
        request.setArgs(args);
        request.setToken(remoteServer.getToken());
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
        Object result = response.getResult();
        return result;
    }


}
