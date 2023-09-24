package com.raiden.tool.http.core.springboot.annotaion;

import com.raiden.tool.http.core.springboot.HttpServerScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 包扫描
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 12:32
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpServerScannerRegistrar.class)
@Inherited
public @interface HttpScan {

    /**
     * Scan package path
     * Same meaning as basePackages
     *
     * @return basePackages
     */
    String[] value() default {};

    /**
     * Scan package path
     *
     * @return basePackages
     */
    String[] basePackages() default {};

    /**
     * Scan package classes
     *
     * @return Scan package classes
     */
    Class<?>[] basePackageClasses() default {};

}
