package com.raiden.tool.http.proxy;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.annotation.Body;
import com.raiden.tool.http.annotation.Param;
import com.raiden.tool.http.annotation.PathParam;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.MultipartData;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import com.raiden.tool.http.processor.HttpClientBeanFactory;
import com.raiden.tool.http.processor.HttpClientProcessor;
import com.raiden.tool.http.MethodArgsBean;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:30
 **/
public interface InterfaceProxy {

    default Object handler(Method method, Object[] args, HttpClientProcessor httpClientProcessor, HttpClientBeanFactory httpClientBeanFactory) {
        String name = method.getDeclaringClass().getName() + "." + method.getName();
        final MethodArgsBean methodArgsBean = httpClientBeanFactory.getMethodArgsBean(name);
        if (Objects.nonNull(HttpBootStrap.getPredRequest())){
            HttpBootStrap.getPredRequest().handler(methodArgsBean);
        }
        HttpMethod httpMethod = methodArgsBean.getRequestMethod();
        final boolean form = methodArgsBean.isForm();
        final Parameter[] parameters = methodArgsBean.getUrlParameters();
        String url = methodArgsBean.getUrl();
        final Class<?> returnType = methodArgsBean.getReturnType();
        final Type typeArgument = methodArgsBean.getTypeArgument();
        final String interceptorClassName = methodArgsBean.getInterceptorClassName();
        final HttpClientInterceptor interceptor = Objects.isNull(interceptorClassName) ? null : HttpBootStrap.getHttpClientInterceptor(interceptorClassName);
        Map<String, String> headMap = methodArgsBean.getHeadMap();
        Map<String, String> paramMap = MapUtil.newHashMap();
        Map<String, String> paramPath = MapUtil.newHashMap();
        final HttpClient httpClient = HttpBootStrap.getHttpClient(methodArgsBean.getSourceHttpClientName());
        Object bodyObject = null;
        MultipartData multipartData = null;
        /* 构建请求参数*/
        if (ObjectUtil.isNotNull(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                Param param = parameters[i].getAnnotation(Param.class);
                if (ObjectUtil.isNotNull(param)) {
                    paramMap.put(param.value(), (String) args[i]);
                    continue;
                }
                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                if (ObjectUtil.isNotNull(pathParam)) {
                    paramPath.put(pathParam.value(), (String) args[i]);
                    continue;
                }
                Body body = parameters[i].getAnnotation(Body.class);
                if (ObjectUtil.isNotNull(body)) {
                    bodyObject = args[i];
                    continue;
                }
                if (args[i] instanceof MultipartData mData){
                    multipartData = mData;
                }
            }
            if (!paramPath.isEmpty()) {
                url = StrFormatter.format(url, paramPath, true);
            }
        }
        return httpClientProcessor.handler(httpMethod, headMap, returnType, typeArgument, form, url,  paramMap,
                bodyObject,  interceptor,  multipartData, httpClient);
    }

    <T> T getInstance(Class<T> interfaceClass);
}
