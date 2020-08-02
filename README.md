# xiabaobao
## 此项目 来源 https://github.com/ChinaSilence/spring-boot-starter-grpc 更改如下
* 更改了项目类型，兼容spring 项目
* 将原先服务的发现形式从 注解方式更改为 配置发现
* 更加适用于 中小型企业在发展过程中项目处于部分功能需要以微服务方式的呈现，但是如果引入springcloud 或者 dubbo 会太过庞大，此插件更适用于过渡阶段，更小更简洁
## 此项目主要使用方式
### client
* 配置 
* spring.grpc.servers: [{server:'user',addresses:[{host:'127.0.0.1',port:'6565'}],enableScan:'false',scanPackages:'com.xencio',token:'xencio',serverPackages:'com.anoyi.grpc.facade.service',serializeTypeValue:'1'},{server:'supplier',host:'127.0.0.1',port:'6565',serverPackages:'com.xencio.supplier.service',serializeTypeValue:'1'}]
   1. server:服务名称自定义
   2. addresses 服务 的ip 端口 为支持负载均衡而改变
   * 2.1. host:服务所在服务器ip
   * 2.2. port:grpc监听端口
   3. enableScan 开启注解方式  @MyGrpcService(server=cfa-tagging)
   4. scanPackages 注解所在包 
   5. serverPackages:grpc调用接口所在包
   6. serializeTypeValue:grpc通信时用的序列化类型，默认 1 也就是sofa-hessian
   7. token token 验证
   8. **对于采用负载均衡（负载均衡默认采用客户端 轮询策略），则需要addresses 配置多组ip port**
* spring.grpc.clientInterceptorName: com.xencio.grpc.interceptor.MyClientInterceptor
   1. 此参数为 客户端拦截器，在发送grpc 通信之前，可自主定义
## server
* spring.grpc.port:6565
   1. 服务端监听端口
* spring.grpc.serverInterceptorName:com.xencio.grpc.interceptor.MyServerInterceptor
   1. 服务端拦截器，在服务端grpc 接收client 端请求后，但是并未去调用方法前，可用于权限校验，同时可以用于返回数据时定义header
* spring.grpc.token
   1. 开启token验证 
## 下一阶段计划
* 整合 eureka



