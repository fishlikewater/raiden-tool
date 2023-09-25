package com.raiden.tool.http.core.springboot;


import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.core.springboot.annotaion.HttpScan;
import com.raiden.tool.http.enums.ProxyEnum;
import com.raiden.tool.http.log.LogConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 13:28
 **/
@Slf4j
public class HttpServerScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private ClassLoader classLoader;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) throws BeansException {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(metadata.getAnnotationAttributes(HttpScan.class.getName()));
        if (attributes == null) {
            return;
        }
        final Boolean enableLog = environment.getProperty("com.raiden.http.enable-log", boolean.class, false);
        final LogConfig.LogLevel logLevel= environment.getProperty("com.raiden.http.log-level", LogConfig.LogLevel.class, LogConfig.LogLevel.BASIC);
        final ProxyEnum proxyType = environment.getProperty("com.raiden.http.proxy-type", ProxyEnum.class);
        // Specify the base package for scanning
        String[] basePackages = getPackagesToScan(attributes);
        try {
            HttpBootStrap.init(basePackages);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (enableLog){
            HttpBootStrap.getLogConfig().setEnableLog(true);
            HttpBootStrap.getLogConfig().setLogLevel(logLevel);
        }
        ClassPathRemoteServerScanner scanner = new ClassPathRemoteServerScanner(registry, classLoader, proxyType);

        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        log.info("Scan the @HttpServer annotated interface using the @RemoteScan configuration. packages={}", Arrays.toString(basePackages));
        scanner.registerFilters();
        // Scan and register to BeanDefinition
        scanner.doScan(basePackages);
    }

    private String[] getPackagesToScan(AnnotationAttributes attributes) {
        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        if (!ObjectUtils.isEmpty(value)) {
            Assert.state(ObjectUtils.isEmpty(basePackages),
                    "@HttpScan basePackages and value attributes are mutually exclusive");
        }
        Set<String> packagesToScan = new LinkedHashSet<>();
        packagesToScan.addAll(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        return packagesToScan.toArray(new String[0]);
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setBeanClassLoader(@NonNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
