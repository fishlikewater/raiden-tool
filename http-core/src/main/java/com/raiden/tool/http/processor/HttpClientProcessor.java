package com.raiden.tool.http.processor;


/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 18:42
 *
 **/
public interface HttpClientProcessor {

    /**
     *  处理请求
     * @param methodArgsBean 接口参数
     * @param args 方法实际入参
     * @since 2023/3/13 14:48
     * @return okhttp3.Response
     */
    Object handler(MethodArgsBean methodArgsBean, Object[] args);

}
