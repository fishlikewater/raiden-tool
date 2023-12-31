package com.raiden.tool.http.source;

/**
 * <p>
 *     HttpClient 注册
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 10:14
 **/
public interface SourceHttpClientRegister {

    /**
     * 向#{@link SourceHttpClientRegistry}注册数据
     *
     * @param registry SourceHttpClientRegistry
     */
    void register(SourceHttpClientRegistry registry);
}
