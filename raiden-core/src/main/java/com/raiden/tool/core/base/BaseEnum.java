package com.raiden.tool.core.base;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月12日 10:19
 **/
public interface BaseEnum<T> {
    /**
     * 获取枚举编码
     *
     * @return 编码
     */
    T getCode();
}
