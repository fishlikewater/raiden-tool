package com.raiden.tool.http.proxy;

import com.raiden.tool.http.processor.HttpClientBeanFactory;
import com.raiden.tool.http.processor.HttpClientProcessor;
import com.raiden.tool.http.processor.MethodArgsBean;

import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:30
 **/
public interface InterfaceProxy {

    default Object handler(Method method, Object[] args, HttpClientProcessor httpClientProcessor, HttpClientBeanFactory httpClientBeanFactory) {
        final MethodArgsBean methodArgsBean = httpClientBeanFactory.getMethodArgsBean(method.getName());
        return httpClientProcessor.handler(methodArgsBean, args);
    }

    <T> T getInstance(Class<T> interfaceClass);
}
