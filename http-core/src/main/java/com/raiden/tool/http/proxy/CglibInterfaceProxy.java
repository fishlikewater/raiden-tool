package com.raiden.tool.http.proxy;

import com.raiden.tool.http.processor.HttpClientBeanFactory;
import com.raiden.tool.http.processor.HttpClientProcessor;
import lombok.Setter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:20
 **/
public class CglibInterfaceProxy implements MethodInterceptor, InterfaceProxy {

    @Setter
    private HttpClientProcessor httpClientProcessor;

    @Setter
    private HttpClientBeanFactory httpClientBeanFactory;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        // 回调方法
        enhancer.setCallback(this);
        // 创建代理对象
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) {
        return handler(method, args, httpClientProcessor, httpClientBeanFactory);
    }
}
