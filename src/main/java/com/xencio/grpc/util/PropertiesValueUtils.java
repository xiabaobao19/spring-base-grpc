package com.xencio.grpc.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author xiabaobao
 * @date 2020/2/27 12:56
 */
@Slf4j
public class PropertiesValueUtils  {

    private static final String SETTINGS_FILE = "application.properties,application.yml,application.yaml";

    public static String getRealValue(String propertiesKey) {
        String[] split = SETTINGS_FILE.split(",");
        String propertyValue = "";
        List<String> appList = Arrays.asList(split);
        String realPath = null;
        Resource resource = null;
        ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
        for (String s : appList) {
            try {
                Resource[] resources = resourceLoader.getResources("classpath*:**/" + s);
                if (resources != null && resources.length > 0) {
                    boolean exists = resources[0].exists();
                    if (exists) {
                        realPath = s;
                        resource = resources[0];
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotBlank(realPath)) {
            Properties properties = getProperties(realPath,resource);
            propertyValue = properties.getProperty(propertiesKey);
        }
        return propertyValue;
    }

    private static Properties getProperties(String path,Resource resource) {

        Properties properties = new Properties();
        try {
            if (path.endsWith(".yml") || path.endsWith(".yaml")) {
                YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
                yaml.setResources(resource);
                properties = yaml.getObject();
            } else {
                FileInputStream inputStream = new FileInputStream(resource.getFile());
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            log.info("加载 yml 失败");
        }
        return properties;
    }


}
