# xiabaobao
## 此项目 来源 https://github.com/ChinaSilence/spring-boot-starter-grpc 更改如下
* 更改了项目类型，兼容spring 项目
* 将原先服务的发现形式从 注解方式更改为 配置发现
## 此项目主要使用方式
### client
* 配置 
* spring.grpc.servers: "[{server:'user',host:'127.0.0.1',port:'6566',serverPackages:'com.anoyi.grpc.facade.service'},{server:'supplier',host:'127.0.0.1',port:'6565',serverPackages:'com.xencio.supplier.service'}]"
* spring.grpc.serverPackages: com.anoyi.grpc.facade.service,com.xencio.supplier.service
* spring.grpc.enable: true
## server
* spring.grpc.enable:true
* spring.grpc.port:6565
