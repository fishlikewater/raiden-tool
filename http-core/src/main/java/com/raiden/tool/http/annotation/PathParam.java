package com.raiden.tool.http.annotation;

import java.lang.annotation.*;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月27日 10:40
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    String value() default "";

    String name() default "";
}
