package com.raiden.tool.http.annotation;

import java.lang.annotation.*;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 13:29
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpServer {

    /** 请求地址*/
    String url();

    /** 协议 http 或 https*/
    String protocol() default "http";

    String sourceHttpClient() default "default";



}
