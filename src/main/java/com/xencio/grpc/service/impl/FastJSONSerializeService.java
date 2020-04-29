package com.xencio.grpc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.xencio.grpc.service.MyGrpcSerializeService;
import com.xencio.grpc.service.GrpcRequest;
import com.xencio.grpc.service.GrpcResponse;
import com.xencio.rpc.GrpcService;
import com.google.protobuf.ByteString;

/**
 * FastJSON 序列化/反序列化工具
 */
public class FastJSONSerializeService implements MyGrpcSerializeService {

    @Override
    public ByteString serialize(GrpcResponse response) {
        return ByteString.copyFrom(JSON.toJSONBytes(response));
    }

    @Override
    public ByteString serialize(GrpcRequest request) {
        return ByteString.copyFrom(JSON.toJSONBytes(request));
    }

    @Override
    public GrpcRequest deserialize(GrpcService.Request request) {
        byte[] bytes = request.getRequest().toByteArray();
        return JSON.parseObject(bytes, GrpcRequest.class, Feature.OrderedField);
    }

    @Override
    public GrpcResponse deserialize(GrpcService.Response response) {
        byte[] bytes = response.getResponse().toByteArray();
        return JSON.parseObject(bytes, GrpcResponse.class, Feature.OrderedField);
    }

}
