package com.raiden.tool.http.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.TypeUtil;
import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.annotation.*;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 20:39
 **/
@Slf4j
public class DefaultHttpClientBeanFactory implements HttpClientBeanFactory {

    ConcurrentHashMap<String, MethodArgsBean> methodCache = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> proxyCache = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, HttpClientInterceptor> interceptorCache = new ConcurrentHashMap<>();
    String URL_SPLIT = "/";

    @SneakyThrows
    private HttpClientInterceptor getInterceptor(Class<? extends HttpClientInterceptor> iClass) {
        return iClass.getDeclaredConstructor().newInstance();
    }

    @Override
    public MethodArgsBean getMethodArgsBean(String methodName) {

        return methodCache.get(methodName);
    }

    @Override
    public void cacheMethod(Method method) {

        HttpMethod requestMethodType = null;
        boolean isForm = false;
        boolean isContinue = false;
        String path = "";
        final Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof Form){
                isForm = true;
            }
            if (annotation instanceof RequireLine requireLine){
                requestMethodType = requireLine.method();
                path = requireLine.path();
                isContinue = true;
            }
            if (annotation instanceof GET get){
                requestMethodType = HttpMethod.GET;
                path = get.value();
                isContinue = true;
            }
            if (annotation instanceof POST post){
                requestMethodType = HttpMethod.POST;
                path = post.value();
                isContinue = true;
            }
            if (annotation instanceof PUT put){
                requestMethodType = HttpMethod.PUT;
                path = put.value();
                isContinue = true;
            }
            if (annotation instanceof DELETE delete){
                requestMethodType = HttpMethod.DELETE;
                path = delete.value();
                isContinue = true;
            }
        }
        if (!isContinue){
            return;
        }
        final Interceptor interceptor = method.getDeclaringClass().getAnnotation(Interceptor.class);
        HttpServer httpServer = method.getDeclaringClass().getAnnotation(HttpServer.class);
        String serverName = httpServer.serverName();
        String interceptorClassName = null;
        if (Objects.nonNull(interceptor)){
            interceptorClassName = interceptor.value().getName();
            if (HttpBootStrap.isSelfManager()){
                final HttpClientInterceptor httpClientInterceptor = interceptorCache.get(interceptor.value().getName());
                if (Objects.isNull(httpClientInterceptor)){
                    setHttpClientInterceptor(getInterceptor(interceptor.value()));
                }
            }
        }
        final Class<?> returnType = method.getReturnType();
        final Type returnType1 = TypeUtil.getReturnType(method);
        final Type typeArgument = TypeUtil.getTypeArgument(returnType1);
        Parameter[] parameters = method.getParameters();
        Heads heads = method.getAnnotation(Heads.class);
        Map<String, String> headMap = MapUtil.newHashMap();
        if (Objects.nonNull(heads)){
            Arrays.stream(heads.value()).map(h -> h.split(":")).forEach(s -> headMap.put(s[0], s[1]));
        }
        final String requestUrl = getUrl(httpServer, path);
        final String className = method.getDeclaringClass().getName();
        String name = method.getDeclaringClass().getName() + "." + method.getName();
        methodCache.put(name, new MethodArgsBean(className, method.getName(), serverName, httpServer.sourceHttpClient(),
                interceptorClassName, requestMethodType, isForm, headMap, requestUrl, parameters, returnType, typeArgument));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxyObject(Class<T> tClass) {
        return (T)proxyCache.get(tClass.getName());
    }

    @Override
    public void cacheProxyObject(String className, Object proxyObject) {
        proxyCache.put(className, proxyObject);
    }

    @Override
    public HttpClientInterceptor getInterceptor(String interceptorName) {
        return interceptorCache.get(interceptorName);
    }

    @Override
    public void setHttpClientInterceptor(HttpClientInterceptor httpClientInterceptor) {
        interceptorCache.put(httpClientInterceptor.getClass().getName(), httpClientInterceptor);
    }

    private String getUrl(HttpServer httpServer, String path) {
        String requestUrl;
        String url = httpServer.url();
        if (!url.endsWith(URL_SPLIT)) {
            url += URL_SPLIT;
        }
        if (url.startsWith("http")) {
            requestUrl = url;
        } else {
            requestUrl = httpServer.protocol() + "://" + url;
        }
        if (path.startsWith(URL_SPLIT)) {
            path = path.substring(1);
        }
        requestUrl += path;
        return requestUrl;
    }
}
