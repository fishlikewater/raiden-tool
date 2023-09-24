package com.raiden.tool.http.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.TypeUtil;
import com.raiden.tool.http.annotation.Heads;
import com.raiden.tool.http.annotation.HttpServer;
import com.raiden.tool.http.annotation.Interceptor;
import com.raiden.tool.http.annotation.RequireLine;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.enums.RequestEnum;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
        final Interceptor interceptor = method.getDeclaringClass().getAnnotation(Interceptor.class);
        final Class<? extends HttpClientInterceptor> httpClientInterceptorClass = Objects.isNull(interceptor) ? null : interceptor.value();
        final HttpClientInterceptor httpClientInterceptor = getInterceptor(httpClientInterceptorClass);
        final RequireLine requireLine = method.getAnnotation(RequireLine.class);
        final HttpMethod requestMethodType = requireLine.method();
        final RequestEnum mediaType = requireLine.mediaType();
        final Class<?> returnType = method.getReturnType();
        final Type returnType1 = TypeUtil.getReturnType(method);
        final Type typeArgument = TypeUtil.getTypeArgument(returnType1);
        Parameter[] parameters = method.getParameters();
        Heads heads = method.getAnnotation(Heads.class);
        Map<String, String> headMap = MapUtil.newHashMap();
        if (Objects.nonNull(heads)){
            Arrays.stream(heads.value()).map(h -> h.split(":")).forEach(s -> headMap.put(s[0], s[1]));
        }
        final String requestUrl = getUrl(method, requireLine);
        methodCache.put(method.getName(), new MethodArgsBean(method.getName(), httpClientInterceptor, requestMethodType, mediaType, headMap, requestUrl, parameters, returnType, typeArgument));
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

    private String getUrl(Method method, RequireLine requireLine) {
        String requestUrl;
        HttpServer httpServer = method.getDeclaringClass().getAnnotation(HttpServer.class);
        String url = httpServer.url();
        if (!url.endsWith(URL_SPLIT)) {
            url += URL_SPLIT;
        }
        if (url.startsWith("http")) {
            requestUrl = url;
        } else {
            requestUrl = httpServer.protocol() + "://" + url;
        }
        String path = requireLine.path();
        if (path.startsWith(URL_SPLIT)) {
            path = path.substring(1);
        }
        requestUrl += path;
        return requestUrl;
    }
}
