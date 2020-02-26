package com.xencio.grpc.exception;

public class GrpcException extends RuntimeException {

    public GrpcException(String message){
        super(message);
    }

}
