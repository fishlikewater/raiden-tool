package com.raiden.tool.http.annotation;

import java.lang.annotation.*;

/**
 * 适用于url上的拼接参数
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 19:09
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    String value() default "";

    String name() default "";
}
