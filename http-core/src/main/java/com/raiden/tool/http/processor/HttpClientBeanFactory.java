package com.raiden.tool.http.processor;

import com.raiden.tool.http.interceptor.HttpClientInterceptor;

import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 20:25
 **/
public interface HttpClientBeanFactory {


    MethodArgsBean getMethodArgsBean(String methodName);

    void cacheMethod(Method method);

    <T> T getProxyObject(Class<T> tClass);


    void cacheProxyObject(String className,  Object proxyObject);

    HttpClientInterceptor getInterceptor(String interceptorName);

    void setHttpClientInterceptor(HttpClientInterceptor httpClientInterceptor);
}
