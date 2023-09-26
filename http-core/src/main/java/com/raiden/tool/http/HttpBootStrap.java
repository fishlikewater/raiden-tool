package com.raiden.tool.http;

import cn.hutool.core.lang.Assert;
import com.raiden.tool.http.annotation.HttpServer;
import com.raiden.tool.http.annotation.RequireLine;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import com.raiden.tool.http.log.LogConfig;
import com.raiden.tool.http.processor.*;
import com.raiden.tool.http.proxy.InterfaceProxy;
import com.raiden.tool.http.proxy.JdkInterfaceProxy;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  配置入口类
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 10:14
 **/
@Slf4j
@Accessors(chain = true)
public class HttpBootStrap {


    private static SourceHttpClientRegistry registry;

    @Getter
    private static PredRequest predRequest;

    @Getter
    private static boolean selfManager;

    @Getter
    private static LogConfig logConfig = new LogConfig();

    private static InterfaceProxy interfaceProxy;

    @Getter
    private static HttpClientBeanFactory httpClientBeanFactory;

    @Getter
    private static HttpClientProcessor httpClientProcessor;

    public static void setPredRequest(PredRequest predRequest){
        HttpBootStrap.predRequest = predRequest;
    }

    public static void setHttpClientInterceptor(HttpClientInterceptor interceptor){
        httpClientBeanFactory.setHttpClientInterceptor(interceptor);
    }
    public static void setSourceHttpClientRegistry(SourceHttpClientRegistry registry){
        HttpBootStrap.registry = registry;
    }

    public static HttpClientInterceptor getHttpClientInterceptor(String className){
        return httpClientBeanFactory.getInterceptor(className);
    }

    public static HttpClient getHttpClient(String className){

        return registry.get(className);
    }

    public static void setSelfManager(boolean selfManager){
        HttpBootStrap.selfManager = selfManager;
    }

    public static void registerHttpClient(String name, HttpClient httpClient){
        Assert.notNull(registry, "请先调用init初始化...");
        registry.register(name, httpClient);
    }

    public static <T> T getProxy(Class<T> tClass){
        return httpClientBeanFactory.getProxyObject(tClass);
    }

    public static void registerDefaultHttpClient(){
       registry = new SourceHttpClientRegistry(List.of(registry -> {
           final HttpClient defaultClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).version(HttpClient.Version.HTTP_1_1).build();
           registry.register("default", defaultClient);
        }));
    }

    public static void init(String...packages) throws ClassNotFoundException {

        log.info("httpClient 接口开始初始化....");
        if (selfManager){
            registerDefaultHttpClient();
            registry.init();
        }
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
