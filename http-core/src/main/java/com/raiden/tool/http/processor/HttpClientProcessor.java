package com.raiden.tool.http.processor;


import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.MultipartData;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;

import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.Map;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 18:42
 *
 **/
public interface HttpClientProcessor {

    /**
     * 处理请求
     * @author fishlikewater@126.com
     * @param method 请求方法
     * @param headMap 请求头
     * @param returnType 返回类型
     * @param typeArgument 返回类型的泛型
     * @param form 是否为form表单请求
     * @param url 请求地址
     * @param paramMap 请求路径上的参数
     * @param bodyObject 请求body数据
     * @param interceptor 拦截器
     * @param multipartData 文件数据
     * @param httpClient 客户端
     * @since 2023/9/26 14:21
     * @return java.lang.Object
     */
    Object handler(HttpMethod method, Map<String, String> headMap, Class<?> returnType, Type typeArgument, boolean form,
                   String url, Map<String, String> paramMap, Object bodyObject, HttpClientInterceptor interceptor, MultipartData multipartData, HttpClient httpClient);

}
