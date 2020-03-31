# xiabaobao
## 此项目 来源 https://github.com/ChinaSilence/spring-boot-starter-grpc 更改如下
* 更改了项目类型，兼容spring 项目
* 将原先服务的发现形式从 注解方式更改为 配置发现
## 此项目主要使用方式
### client
* 配置 
* spring.grpc.servers: "[{server:'user',host:'127.0.0.1',port:'6566',serverPackages:'com.anoyi.grpc.facade.service',serializeTypeValue:'1'},{server:'supplier',host:'127.0.0.1',port:'6565',serverPackages:'com.xencio.supplier.service',serializeTypeValue:'1'}]"
   1. server:服务名称自定义
   2. host:服务所在服务器ip
   3. port:grpc监听端口
   4. serverPackages:grpc调用接口所在包
   5. serializeTypeValue:grpc通信时用的序列化类型，默认 1 也就是sofa-hessian
   6. **对于采用负载均衡（负载均衡默认采用客户端 轮询策略），则需要将server名称相同，并且serverPackages 相同**
* spring.grpc.clientInterceptorName: com.xencio.grpc.interceptor.MyClientInterceptor
   1. 此参数为 客户端拦截器，在发送grpc 通信之前，可自主定义
## server
* spring.grpc.port:6565
   1. 服务端监听端口
* spring.grpc.serverInterceptorName:com.xencio.grpc.interceptor.MyServerInterceptor
   1. 服务端拦截器，在服务端grpc 接收client 端请求后，但是并未去调用方法前，可用于权限校验，同时可以用于返回数据时定义header
   
## 下一阶段计划
* 整合 eureka



