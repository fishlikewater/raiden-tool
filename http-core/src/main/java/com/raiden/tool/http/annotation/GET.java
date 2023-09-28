package com.raiden.tool.http.annotation;

import java.lang.annotation.*;

/**
 * <p>
 *  GET请求方法
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月28日 20:30
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GET {

    String value() default "";

}
