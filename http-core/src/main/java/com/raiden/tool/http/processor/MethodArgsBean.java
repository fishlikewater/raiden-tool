package com.raiden.tool.http.processor;

import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.enums.RequestEnum;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 9:37
 **/
@Data
@AllArgsConstructor
public class MethodArgsBean {

    private String className;

    private String methodName;

    private HttpClientInterceptor interceptor;

    private HttpMethod requestMethod;

    private RequestEnum mediaType;

    private Map<String, String> headMap;

    private String realUrl;

    private Parameter[] urlParameters;

    private Class<?> returnType;

    private Type typeArgument;




}
