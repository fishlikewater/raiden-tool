package com.raiden.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典注解
 * @author fishlikewater
 * @since 2022年01月15日 16:42
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    /**
     * 数据code
     * @return 返回类型： String
     */
    String dicCode();

    /**
     * 数据Text
     * @return 返回类型： String
     */
    String dicText() default "";

    /**
     * 数据字典表
     * @return 返回类型： String
     */
    String dictTable() default "";
}
