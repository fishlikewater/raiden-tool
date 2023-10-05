package com.raiden.tool.annotation;

import java.lang.annotation.*;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0.0
 * @since 2021年12月04日 12:21
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    String value();
}
