package com.raiden.tool.http.proxy;

import com.raiden.tool.http.processor.HttpClientBeanFactory;
import com.raiden.tool.http.processor.HttpClientProcessor;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 13:10
 *
 **/
public class JdkInterfaceProxy  implements InvocationHandler,InterfaceProxy {

    @Setter
    private HttpClientProcessor httpClientProcessor;

    @Setter
    private HttpClientBeanFactory httpClientBeanFactory;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getInstance(Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return handler(method, args, httpClientProcessor, httpClientBeanFactory);
    }


}
