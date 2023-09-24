package com.raiden.tool.http.annotation;

import com.raiden.tool.http.interceptor.HttpClientInterceptor;

import java.lang.annotation.*;

/**
 * <p>
 *  拦截器注解
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 14:44
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor {

    /** 拦截器类*/
    Class<? extends HttpClientInterceptor> value();



}
