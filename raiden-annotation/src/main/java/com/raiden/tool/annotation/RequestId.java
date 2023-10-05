package com.raiden.tool.annotation;

import java.lang.annotation.*;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 *
 * <p>未线程执行流程添加唯一标识</p>
 * @since 2023年04月13日 21:20
 **/
@Target({ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestId {

}
