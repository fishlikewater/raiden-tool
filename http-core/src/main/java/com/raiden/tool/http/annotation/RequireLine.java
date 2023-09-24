package com.raiden.tool.http.annotation;

import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.enums.RequestEnum;

import java.lang.annotation.*;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 10:51
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireLine {

    /** 请求方式 默认GET*/
    HttpMethod method() default HttpMethod.GET;

    /** 请求路径*/
    String path() default "";

    RequestEnum mediaType() default RequestEnum.JSON;


}
