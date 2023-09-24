package com.raiden.tool.http;

import com.raiden.tool.http.annotation.HttpServer;
import com.raiden.tool.http.annotation.RequireLine;
import com.raiden.tool.http.processor.DefaultHttpClientBeanFactory;
import com.raiden.tool.http.processor.DefaultHttpClientProcessor;
import com.raiden.tool.http.processor.HttpClientBeanFactory;
import com.raiden.tool.http.processor.HttpClientProcessor;
import com.raiden.tool.http.proxy.InterfaceProxy;
import com.raiden.tool.http.proxy.JdkInterfaceProxy;
import io.github.classgraph.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * <p>
 *  配置入口类
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 10:14
 **/
@Setter
@Getter
@Builder
@Slf4j
@Accessors(chain = true)
public class HttpBootStrap {


    public static HttpClient httpClient;

    private boolean enableLog;

    private boolean selfManager;

    private InterfaceProxy interfaceProxy;

    private HttpClientBeanFactory httpClientBeanFactory;

    private HttpClientProcessor httpClientProcessor;

    public <T> T getProxy(Class<T> tClass){
        return this.httpClientBeanFactory.getProxyObject(tClass);
    }


    public void init(String...packages) throws ClassNotFoundException, NoSuchMethodException {

        log.info("httpClient 接口开始初始化....");
        HttpBootStrap.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).version(HttpClient.Version.HTTP_1_1).build();
        if (Objects.isNull(httpClientBeanFactory)){
            httpClientBeanFactory = new DefaultHttpClientBeanFactory();
        }

        if (Objects.isNull(httpClientProcessor)){
            httpClientProcessor = new DefaultHttpClientProcessor();
        }
        if (Objects.isNull(interfaceProxy)){
            final JdkInterfaceProxy jdkInterfaceProxy = new JdkInterfaceProxy();
            jdkInterfaceProxy.setHttpClientBeanFactory(httpClientBeanFactory);
            jdkInterfaceProxy.setHttpClientProcessor(httpClientProcessor);
            interfaceProxy = jdkInterfaceProxy;
        }

        final ClassGraph classGraph = new ClassGraph();
        try(ScanResult scan = classGraph.enableAllInfo().acceptPackages(packages).scan()) {
            final ClassInfoList allClasses = scan.getClassesWithAnnotation(HttpServer.class);
            for (ClassInfo allClass : allClasses) {
                Class<?> clazz = Class.forName(allClass.getName());
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(RequireLine.class) != null){
                        if (selfManager){
                            final Object instance = interfaceProxy.getInstance(clazz);
                            httpClientBeanFactory.cacheProxyObject(allClass.getName(), instance);
                        }
                        httpClientBeanFactory.cacheMethod(method);
                    }
                }
            }
        }
        log.info("httpClient 接口初始化完成....");

    }

}
