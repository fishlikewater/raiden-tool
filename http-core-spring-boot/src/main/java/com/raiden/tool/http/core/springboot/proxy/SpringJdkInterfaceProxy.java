package com.raiden.tool.http.core.springboot.proxy;

import com.raiden.tool.http.proxy.JdkInterfaceProxy;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 15:39
 **/
public class SpringJdkInterfaceProxy<T> extends JdkInterfaceProxy implements FactoryBean<T> {

    @Setter
    private Class<T> interfaceClass;

    @Override
    public T getObject(){
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
}
