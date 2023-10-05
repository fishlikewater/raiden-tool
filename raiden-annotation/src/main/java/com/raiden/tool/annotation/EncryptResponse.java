package com.raiden.tool.annotation;

import java.lang.annotation.*;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 *
 * <p>加了此注解的接口(true)将进行数据加密操作(post的body) 可
 *  以放在类上，可以放在方法上 </p>
 * @since 2022年01月15日 16:42
 **/
@Target({ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptResponse {

    /**
     * 是否对结果加密
     */
    boolean value() default true;
}
