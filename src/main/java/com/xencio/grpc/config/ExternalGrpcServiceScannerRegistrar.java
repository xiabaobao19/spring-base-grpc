package com.xencio.grpc.config;

/**
 * @author xiabaobao
 * @date 2020/2/17 8:46
 */

import com.alibaba.fastjson.JSONArray;
import com.xencio.grpc.util.ClassPathGrpcServiceScanner;
import com.xencio.grpc.util.MyTypeFilter;
import com.xencio.grpc.util.ProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 手动扫描 @GrpcService 注解的接口，
 * 生成动态代理类，注入到 Spring 容器，
 * 因为引入了@GrpcServiceScan  @GrpcserviceScan
 * 中包含@Import  ExternalGrpcServiceScannerRegistrar
 * 所以才会在初始化的时候 初始化此类
 */
@Slf4j
public class ExternalGrpcServiceScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private BeanFactory beanFactory;

    private ResourceLoader resourceLoader;

    private String serverPackages;

    private Boolean server;

    private GrpcProperties grpcProperties;


    private static final String SETTINGS_FILE = "/**/application.properties,/**/application.yml,/**/application.yaml";

    //将BeanFactoryAware 的beanFactory 暴露出来 用于填充 特定注解的bean
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    //ResourceLoaderAware 的resourceLoader 代表的是 spring容器
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        setServerPackages();
    }

    //将@GrpcService 标注的class 的bean 注入到spring容器
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ((DefaultListableBeanFactory) beanFactory).registerSingleton("grpcProperties", this.grpcProperties);
        if (server) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathGrpcServiceScanner(registry);
            scanner.setResourceLoader(this.resourceLoader);
            scanner.addIncludeFilter(new MyTypeFilter());
            Set<BeanDefinition> beanDefinitions = scanPackages(importingClassMetadata, scanner);//扫描器获取的包含又@GrpcService 注解的类
            ProxyUtil.registerBeans(beanFactory, beanDefinitions);
        }
    }

    /**
     * 包扫描
     */
    private Set<BeanDefinition> scanPackages(AnnotationMetadata importingClassMetadata, ClassPathBeanDefinitionScanner scanner) {
        List<String> packages = new ArrayList<>();
        if (StringUtils.isNotBlank(serverPackages)) {
            String[] basePackages = serverPackages.split(",");//(String[]) annotationAttributes.get("packages");
            if (basePackages.length > 0) {
                packages.addAll(Arrays.asList(basePackages));
            }
        }
        //}
        Set<BeanDefinition> beanDefinitions = new HashSet<>();
        if (CollectionUtils.isEmpty(packages)) {
            return beanDefinitions;
        }
        packages.forEach(pack -> beanDefinitions.addAll(scanner.findCandidateComponents(pack)));
        return beanDefinitions;
    }

    public void setServerPackages() {
        String[] split = SETTINGS_FILE.split(",");
        List<String> appList = Arrays.asList(split);
        String realPath = null;
        ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        for (String s : appList) {
            try {
                Resource[] resources = resourceLoader.getResources(s);
                if (resources != null && resources.length > 0) {
                    realPath = s;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotBlank(realPath)) {
            Properties properties = getProperties(realPath);
            String property = properties.getProperty("spring.grpc.serverPackages", null);
            Boolean server = Boolean.parseBoolean(properties.getProperty("spring.grpc.enable", "false"));
            this.server = server;
            this.serverPackages = property;
            this.grpcProperties=registerBeanGrpcProperties(properties);
        }

    }

    public Properties getProperties(String path) {
        Properties properties = new Properties();
        try {
            ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourceLoader.getResources(path);
            if (path.endsWith(".yml") || path.endsWith(".yaml")) {
                YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                yaml.setResources(resources[0]);
                properties = yaml.getObject();
            } else {
                FileInputStream inputStream = new FileInputStream(resources[0].getFile());
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            log.info("加载 yml 失败");
        }
        return properties;
    }

    private GrpcProperties registerBeanGrpcProperties(Properties properties)  {
        GrpcProperties grpcProperties = new GrpcProperties();
        grpcProperties.setPort(Integer.parseInt(properties.getProperty("spring.grpc.port", "0")));
        grpcProperties.setEnable(Boolean.parseBoolean(properties.getProperty("spring.grpc.enable", "true")));
        grpcProperties.setClientInterceptorName(properties.getProperty("spring.grpc.clientInterceptorName", ""));
        boolean clientIntercept = StringUtils.isNotBlank(grpcProperties.getClientInterceptorName());
        if (clientIntercept){
            try {
                Class<?> aClass = Class.forName(grpcProperties.getClientInterceptorName());
                grpcProperties.setClientInterceptor(aClass);
            } catch (ClassNotFoundException e) {
                log.info("获取clientInterceptName 失败");
            }
        }
        grpcProperties.setServerInterceptorName(properties.getProperty("spring.grpc.serverInterceptorName", ""));
        boolean serverIntercept = StringUtils.isNotBlank(grpcProperties.getServerInterceptorName());
        if (serverIntercept){
            try {
                Class<?> aClass = Class.forName(grpcProperties.getServerInterceptorName());
                grpcProperties.setServerInterceptor(aClass);
            } catch (ClassNotFoundException e) {
                log.info("获取clientInterceptName 失败");
            }
        }
        String servers = properties.getProperty("spring.grpc.servers", "");
        if (StringUtils.isNotBlank(servers)) {
            List<RemoteServer> remoteServers = JSONArray.parseArray(servers, RemoteServer.class);
            if (remoteServers != null) {
                remoteServers.forEach(e -> {
                    String serverPackages = e.getServerPackages();
                    List<String> clas = new ArrayList<>();
                    if (StringUtils.isNotBlank(serverPackages)) {
                        String[] packages = serverPackages.split(",");
                        for (String pack : packages) {
                            GenericApplicationContext context = new GenericApplicationContext();
                            ClassPathGrpcServiceScanner scanner = new ClassPathGrpcServiceScanner(context);
                            scanner.addIncludeFilter(new MyTypeFilter());
                            Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(pack);
                            beanDefinitions.forEach(eb -> clas.add(eb.getBeanClassName()));
                        }
                        e.setServerClassNames(clas);
                    }
                });
            }
            grpcProperties.setRemoteServers(remoteServers);
        }
        return grpcProperties;
    }
}
