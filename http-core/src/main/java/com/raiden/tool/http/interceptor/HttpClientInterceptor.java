package com.raiden.tool.http.interceptor;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * <p>
 *  请求拦截器
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 19:07
 **/
public interface HttpClientInterceptor {


    /**
     *
     * @param httpRequest 请求数据
     * @since 2023/9/22 19:59
     * @author fishlikewater@126.com
     */
    HttpRequest requestBefore(HttpRequest httpRequest);


    /**
     *
     * @param response 响应
     * @since 2023/9/22 19:59
     * @author fishlikewater@126.com
     */
    <T> HttpResponse<T> requestAfter(HttpResponse<T> response);

}
