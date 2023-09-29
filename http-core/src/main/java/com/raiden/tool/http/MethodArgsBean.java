package com.raiden.tool.http;

import com.raiden.tool.http.enums.HttpMethod;
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

    private String serverName;

    private String sourceHttpClientName;

    private String interceptorClassName;

    private HttpMethod requestMethod;

    private boolean isForm;

    private Map<String, String> headMap;

    private String url;

    private Parameter[] urlParameters;

    private Class<?> returnType;

    private Type typeArgument;




}
